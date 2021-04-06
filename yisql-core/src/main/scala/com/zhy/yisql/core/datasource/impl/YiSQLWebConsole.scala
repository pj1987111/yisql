package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-17
  *  \* Time: 23:09
  *  \* Description: 
  *  \*/
class YiSQLWebConsole extends BaseStreamSource with BaseBatchSource {
  override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"batch load is not support with ${shortFormat} ")
  }

  override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {

  }

  override def fullFormat: String = "org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider"

  override def shortFormat: String = "webConsole"
}