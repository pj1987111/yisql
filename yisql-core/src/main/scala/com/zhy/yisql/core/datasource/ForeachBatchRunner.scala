package com.zhy.yisql.core.datasource

import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.streaming.DataStreamWriter

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-10
  *  \* Time: 20:00
  *  \* Description: 
  *  \*/
object ForeachBatchRunner {
    def run(dataStreamWriter: DataStreamWriter[Row], outputName: String, callback: (Long, SparkSession) => Unit): Unit = {
        dataStreamWriter.foreachBatch { (dataBatch, batchId) =>
            dataBatch.createOrReplaceTempView(outputName)
            callback(batchId, dataBatch.sparkSession)
        }
    }

    def run(dataStreamWriter: DataStreamWriter[Row], callback: (Dataset[Row], Long) => Unit): Unit = {
        dataStreamWriter.foreachBatch { (dataBatch, batchId) =>
            callback(dataBatch, batchId)
        }
    }
}
