package other.python.pyjava

import java.util

import com.zhy.yisql.common.utils.reflect.ScalaMethodMacros.str
import org.apache.spark.TaskContext
import org.apache.spark.sql.{SparkSession, SparkUtils}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{LongType, StructField, StructType}
import org.junit.{Before, Test}
import tech.mlsql.arrow.python.ispark._
import tech.mlsql.arrow.python.runner.{ArrowPythonRunner, ChainedPythonFunctions, PythonConf, PythonFunction}

import scala.collection.JavaConverters._

/**
  * 2019-08-14 WilliamZhu(allwefantasy@gmail.com)
  */
class SparkSpec  {
  var spark: SparkSession = _

  @Before
  def init() = {
    System.setProperty("HADOOP_USER_NAME", "admin")
    spark = SparkSession.builder()
        .master("local[*]")
        .appName("Binlog2DeltaTest")
        .enableHiveSupport()
        .getOrCreate()
  }

  @Test
  def test1() {
    val session = spark
    import session.implicits._
    val timezoneid = session.sessionState.conf.sessionLocalTimeZone
    val df = session.createDataset[String](Seq("a1", "b1")).toDF("value")
    val struct = df.schema
//    val abc = df.rdd.coalesce(1).mapPartitions { iter =>
    val abc = df.rdd.mapPartitions { iter =>
      val enconder = RowEncoder.apply(struct).resolveAndBind()
      val envs = new util.HashMap[String, String]()
//      envs.put(str(PythonConf.PYTHON_ENV), "source activate dev && export ARROW_PRE_0_15_IPC_FORMAT=1")
      envs.put(str(PythonConf.PYTHON_ENV), "export ARROW_PRE_0_15_IPC_FORMAT=1")
      val batch = new ArrowPythonRunner(
        Seq(ChainedPythonFunctions(Seq(PythonFunction(
          """
            |import pandas as pd
            |import numpy as np
            |for item in data_manager.fetch_once():
            |    print(item)
            |df = pd.DataFrame({'AAA': [4, 5, 6, 7, 8],'BBB': [10, 20, 30, 40, 50],'CCC': [100, 50, -30, -50, 1]})
            |data_manager.set_output([[df['AAA'],df['BBB']]])
          """.stripMargin, envs, "python", "3.6")))), struct,
//        timezoneid, Map()
        timezoneid, Map("python.bin.path" -> "/Library/Frameworks/Python.framework/Versions/3.6/bin/python3")
      )
      val newIter = iter.map { irow =>
        enconder.toRow(irow)
      }
      val commonTaskContext = new SparkContextImp(TaskContext.get(), batch)
      val columnarBatchIter = batch.compute(Iterator(newIter), TaskContext.getPartitionId(), commonTaskContext)
      columnarBatchIter.flatMap { batch =>
        batch.rowIterator.asScala
      }.map(f=>f.copy())
    }

    val wow = SparkUtils.internalCreateDataFrame(session, abc, StructType(Seq(StructField("AAA", LongType), StructField("BBB", LongType))), false)
    wow.show()
  }


}
