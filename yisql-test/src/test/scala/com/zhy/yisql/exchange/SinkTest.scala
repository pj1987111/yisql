package com.zhy.yisql.exchange

import com.zhy.yisql.addon.cmd.python.PythonExecutor.streamExecute
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.junit.Test

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-04
 *  \* Time: 23:37
 *  \* Description: 
 *  \ */
class SinkTest {

  @Test
  def staticJoin1(): Unit = {
    // spark session
    val spark = SparkSession
      .builder
      .master("local[*]")
      .config("spark.sql.streaming.checkpointLocation", "./spark-checkpoints")
      .appName("streaming-test")
      .getOrCreate()

    def getJson(unit: Long): String = {
      s"""
      {"id":1,"content":"c1_$unit"}
      {"id":2,"content":"c2_$unit"}
      {"id":3,"content":"c3_$unit"}
      {"id":4,"content":"c4_$unit"}
      """
    }
    import spark.implicits._
    var cacheT = spark.read.json(spark.createDataset[String](getJson(0).split("\n")))
    cacheT.persist()

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        cacheT.unpersist()
        cacheT = spark.read.json(spark.createDataset[String](getJson(milisecs).split("\n")))
        cacheT.persist()
        cacheT.show()
      }
    }, 0, 10, TimeUnit.SECONDS)

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //        .option("includeTimestamp", true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    val line3 = line2.join(cacheT, "id")
    val query = line3.writeStream
      .outputMode("append")
      .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
      //.format("console")
      .start()

    query.awaitTermination()
  }

  @Test
  def testWebConsoleSink2(): Unit = {
    // spark session
    val spark = SparkSession
      .builder
      .master("local[*]")
      .config("spark.sql.streaming.checkpointLocation", "./spark-checkpoints3")
      .appName("streaming-test")
      .getOrCreate()

    val host = "localhost"
    val port1 = "9999"
    val port2 = "9998"

    // define socket source
    val lines1 = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port1)
      .load()

    val lines2 = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port2)
      .load()

    import spark.implicits._
    val line1M = lines1.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    val line2M = lines2.as[String].map(_.split(",")).select(
      $"value"(0).as("id"),
      $"value"(1).as("name"),
      $"value"(2).cast(IntegerType).as("age"))

    val line3 = line1M.join(line2M, "id")
    val query = line3.writeStream
      .outputMode("append")
      .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
      //.format("console")
      .start()
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
