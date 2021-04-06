package com.zhy.yisql.exchange

import java.sql.Timestamp

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
}

case class Event(word: String, timestamp: Timestamp)

// stream internal state
case class State(c: Int)

// stream output
case class StateUpdate(updateTimestamp: Timestamp, word: String, c: Int)