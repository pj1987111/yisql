package other.python

import org.apache.spark.{SparkConf, SparkContext, TaskContext}
import org.apache.spark.sql.{Row, SQLContext, SparkSession, SparkUtils}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.junit.{Before, Test}
import tech.mlsql.arrow.python.iapp.{AppContextImpl, JavaContext}
import tech.mlsql.arrow.python.ispark.SparkContextImp
import tech.mlsql.arrow.python.runner.{ArrowPythonRunner, ChainedPythonFunctions, PythonConf, PythonFunction}

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 17:48
  *  \* Description: 
  *  \*/
class PyTest {
    var sparkSession: SparkSession = _

    @Before
    def setUp() = {
        val conf = new SparkConf().setMaster("local[*]").setAppName("test")
        val sc = new SparkContext(conf)
        val sqlContext = new SQLContext(sc)
        sparkSession = sqlContext.sparkSession
    }

    /**
      * Using python code snippet to process data in Java/Scala
      */
    @Test
    def test1(): Unit = {
        val envs = new java.util.HashMap[String, String]()
        // prepare python environment
        //        envs.put(PythonConf.PYTHON_ENV, "source activate dev && export ARROW_PRE_0_15_IPC_FORMAT=1 ")
        envs.put(PythonConf.PYTHON_ENV, "export ARROW_PRE_0_15_IPC_FORMAT=1 ")

        // describe the data which will be transfered to python
        val sourceSchema = StructType(Seq(StructField("value", StringType)))

        val batch = new ArrowPythonRunner(
            Seq(ChainedPythonFunctions(Seq(PythonFunction(
                """
                  |import pandas as pd
                  |import numpy as np
                  |
                  |#context.fetch_once_as_rows()也可以
                  |
                  |def process():
                  |    for item in data_manager.fetch_once_as_rows():
                  |        item["value1"] = item["value"] + "_suffix"
                  |        yield item
                  |
                  |context.build_result(process())
                """.stripMargin, envs, "python3", "3.7.3")))), sourceSchema,
            "GMT", Map("python.bin.path"->"/usr/local/bin/python3.71")
        )

        // prepare data
        val sourceEnconder = RowEncoder.apply(sourceSchema).resolveAndBind()
        val newIter = Seq(Row.fromSeq(Seq("a1")), Row.fromSeq(Seq("a2"))).map { irow =>
            sourceEnconder.toRow(irow).copy()
        }.iterator

        // run the code and get the return result
        val javaConext = new JavaContext
        val commonTaskContext = new AppContextImpl(javaConext, batch)
        val columnarBatchIter = batch.compute(Iterator(newIter), TaskContext.getPartitionId(), commonTaskContext)

        //f.copy(), copy function is required
        columnarBatchIter.flatMap { batch =>
            batch.rowIterator.asScala
        }.foreach(f => println(f.copy()))
        javaConext.markComplete
        javaConext.close
    }

    /**
      * Using python code snippet to process data in Spark
      */
    @Test
    def test2(): Unit = {
        val session = sparkSession
        import session.implicits._
        val timezoneid = session.sessionState.conf.sessionLocalTimeZone
        val df = session.createDataset[String](Seq("a1", "b1")).toDF("value")
        val struct = df.schema
        val abc = df.rdd.mapPartitions { iter =>
            val enconder = RowEncoder.apply(struct).resolveAndBind()
            val envs = new java.util.HashMap[String, String]()
            //            envs.put(PythonConf.PYTHON_ENV, "source activate streamingpro-spark-2.4.x")
            //            envs.put(PythonConf.PYTHON_ENV, "export ARROW_PRE_0_15_IPC_FORMAT=1 ")
            val batch = new ArrowPythonRunner(
                Seq(ChainedPythonFunctions(Seq(PythonFunction(
                    """
                      |import pandas as pd
                      |import numpy as np
                      |for item in data_manager.fetch_once():
                      |    print(item)
                      |df = pd.DataFrame({'AAA': [4, 5, 6, 7],'BBB': [10, 20, 30, 40],'CCC': [100, 50, -30, -50]})
                      |data_manager.set_output([[df['AAA'],df['BBB']]])
                    """.stripMargin, envs, "python3", "3.7.3")))), struct,
                timezoneid, Map("python.bin.path"->"/usr/local/bin/python3.7")
            )
            val newIter = iter.map { irow =>
                enconder.toRow(irow)
            }
            val commonTaskContext = new SparkContextImp(TaskContext.get(), batch)
            val columnarBatchIter = batch.compute(Iterator(newIter), TaskContext.getPartitionId(), commonTaskContext)
            columnarBatchIter.flatMap { batch =>
                batch.rowIterator.asScala.map(_.copy)
            }
        }

        val wow = SparkUtils.internalCreateDataFrame(session, abc, StructType(Seq(StructField("AAA", LongType), StructField("BBB", LongType))), false)
        wow.show()
    }
}
