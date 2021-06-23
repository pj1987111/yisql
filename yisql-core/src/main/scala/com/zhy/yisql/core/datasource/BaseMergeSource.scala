package com.zhy.yisql.core.datasource

import com.zhy.yisql.addon.cmd.python.PythonExecutor.{getBinAndRunConf, streamExecute}
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

trait BaseMergeSource extends BaseBatchSource with BaseStreamSource {
  /**
   * 流中跑批
   * 高级etl批处理
   *
   * @param dataStreamWriter
   * @param config
   */
  override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
    val newConfig: DataSinkConfig = config.cloneWithNewMode("append")
    val configMap: Map[String, String] = newConfig.config
    //代码etl预处理
    if (configMap.contains("etl.code")) {
      val session: SparkSession = newConfig.spark
      val binRunConf: (Map[String, String], Map[String, String]) = getBinAndRunConf(session)
      dataStreamWriter.foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        val preExecFrame: DataFrame = streamExecute(session, configMap("etl.code"), dataBatch, binRunConf._1, binRunConf._2)
        bSave(preExecFrame.write, newConfig)
      }
    } else if(foreachBatchCallbackStreamEnable){
      dataStreamWriter.foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        bSave(dataBatch.write, newConfig)
      }
    }
  }

  /**
   * 是否为foreach 批写入，若不是自己实现的话一般为true
   * @return
   */
  def foreachBatchCallbackStreamEnable = true
}
