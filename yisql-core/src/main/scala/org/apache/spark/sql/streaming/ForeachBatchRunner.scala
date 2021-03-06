package org.apache.spark.sql.streaming

import com.zhy.yisql.addon.cmd.python.PythonExecutor.{getBinAndRunConf, streamExecute}
import com.zhy.yisql.core.datasource.DataSinkConfig
import org.apache.spark.sql.{DataFrameWriter, Dataset, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-05
  *  \* Time: 17:43
  *  \* Description: 
  *  \*/
object ForeachBatchRunner {
//  def run(dataStreamWriter: DataStreamWriter[Row], outputName: String, callback: (Long, SparkSession) => Unit): Unit = {
//    dataStreamWriter.foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
//      dataBatch.createOrReplaceTempView(outputName)
//      callback(batchId, dataBatch.sparkSession)
//    }
//  }

  def run(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig, callback: (DataFrameWriter[Row], Long) => Unit): Unit = {
    val configMap = config.config
    //代码etl预处理
    if (configMap.contains("etl.code")) {
      val session = config.spark
      val binRunConf = getBinAndRunConf(session)
      dataStreamWriter.foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        val preExecFrame = streamExecute(session, configMap("etl.code"), dataBatch, binRunConf._1, binRunConf._2)
        callback(preExecFrame.write, batchId)
      }
    } else {
      dataStreamWriter.foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        callback(dataBatch.write, batchId)
      }
    }
  }
}
