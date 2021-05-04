package com.zhy.yisql.exchange

import java.util

import com.zhy.yisql.common.utils.reflect.ScalaMethodMacros.str
import org.apache.spark.TaskContext
import org.apache.spark.sql.{SparkSession, SparkUtils}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.junit.Test
import tech.mlsql.arrow.python.ispark.SparkContextImp
import tech.mlsql.arrow.python.runner.{ArrowPythonRunner, ChainedPythonFunctions, PythonConf, PythonFunction}
import tech.mlsql.schema.parser.SparkSimpleSchemaParser

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-04
  *  \* Time: 23:37
  *  \* Description: 
  *  \*/
class SinkTest {

  @Test
  def testWebConsoleSink(): Unit = {
    // spark session
    val spark = SparkSession
        .builder
        .master("local[*]")
        .config("spark.sql.streaming.checkpointLocation", "./spark-checkpoints")
        .appName("streaming-test")
        .getOrCreate()

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
        .format("socket")
        .option("host", host)
        .option("port", port)
        //        .option("includeTimestamp", true)
        .load()

    val query = lines.writeStream
        .outputMode("append")
        .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
        //        .format("console")
        .start()

    query.awaitTermination()
  }

  /**
    * 实时流处理结合python代码
    */
  @Test
  def testWebConsoleSink2(): Unit = {
    // spark session
    val spark = SparkSession.builder().master("local[*]")
        .appName("SokerWOrk").getOrCreate()

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
        .format("socket")
        .option("host", host)
        .option("port", port)
        //        .option("includeTimestamp", true)
        .load()

    //    val query = lines.writeStream
    //        .format("console")
    //        .outputMode("append")
    //        .trigger(Trigger.ProcessingTime(0))
    //        .start()
    val timezoneid = spark.sessionState.conf.sessionLocalTimeZone

    val targetSchema = SparkSimpleSchemaParser.parse("st(field(content,string))").asInstanceOf[StructType]
    val query = lines.writeStream.foreachBatch { (dataBatch, batchId) => {
      import spark.implicits._
      val sourceSchema = dataBatch.schema
      //Unable to find encoder for type stored in a Dataset
      //https://blog.csdn.net/sparkexpert/article/details/52871000
      val abc = dataBatch.rdd.mapPartitions { iter =>
        val enconder = RowEncoder.apply(sourceSchema).resolveAndBind()
        val envs = new util.HashMap[String, String]()
        envs.put(str(PythonConf.PYTHON_ENV), "export ARROW_PRE_0_15_IPC_FORMAT=1")
        val batch = new ArrowPythonRunner(
          Seq(ChainedPythonFunctions(Seq(PythonFunction(
            """
              |import json
              |
              |data = context.fetch_once_as_rows()
              |def process(data):
              |    for row in data:
              |        print(row)
              |        #获取数据 socket数据在value中
              |        value = row["value"]
              |        value_dic = json.loads(value)
              |        new_row = { }
              |        new_row["content"] = "---" + value_dic["content"]+"---"
              |        yield new_row
              |
              |context.build_result(process(data))
            """.stripMargin
            , envs, "python", "3.6")))), sourceSchema,
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
        }
      }

      val wow = SparkUtils.internalCreateDataFrame(spark, abc, targetSchema, false)
      wow.show()
    }}.start()

    query.awaitTermination()
  }

  @Test
  def testCk(): Unit = {
    val spark = SparkSession.builder()
        .appName("TextApp")
        .master("local[2]")
        .getOrCreate()
    val df = spark.read
        .format("jdbc")
        .option("url", "jdbc:clickhouse://192.168.6.52:8123")
        .option("user", "default")
        .option("password", "ck2020")
        //        .option("query", "select id from testParquet where name='p_test_tikv_cpu_dj_20190626_092737932'")
        .option("query", "SELECT * FROM test_users where name='dd'")
        .load()
    df.show()

    val query = df.write
        .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
        .option("port", "6666")
        .save()
  }
}
