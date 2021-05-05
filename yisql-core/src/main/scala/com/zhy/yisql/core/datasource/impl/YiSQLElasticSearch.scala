package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig}
import com.zhy.yisql.core.execute.SQLExecuteContext
import org.apache.spark.sql.{DataFrame, DataFrameWriter, Dataset, Row}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter, ForeachBatchRunner}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:19
  *  \* Description: 
  *  \*/
class YiSQLElasticSearch extends BaseStreamSource with BaseBatchSource {

  override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
    val newConfig = config.cloneWithNewMode("append")
    ForeachBatchRunner.run(dataStreamWriter, config, (writer: DataFrameWriter[Row], batchId: Long) => {
      bSave(writer, newConfig)
    })
  }

  override def skipFormat: Boolean = true

  override def fullFormat: String = "org.elasticsearch.spark.sql"

  override def shortFormat: String = "es"

  override def dbSplitter: String = "/"

}
