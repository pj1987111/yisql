package org.apache.spark.sql.streaming

import org.apache.spark.sql.{Dataset, Row, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-05
  *  \* Time: 17:43
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
