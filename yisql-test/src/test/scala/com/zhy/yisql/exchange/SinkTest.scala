package com.zhy.yisql.exchange

import java.sql.Timestamp

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-04
  *  \* Time: 23:37
  *  \* Description: 
  *  \*/
class Spark30Test {

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
