package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseMergeSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-08
  *  \* Time: 14:07
  *  \* Description: 
  *  \*/
class YiSQLConsole extends BaseMergeSource {
    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        throw new RuntimeException(s"batch load is not support with ${shortFormat} ")
    }

    override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        super.bSave(writer, config)
    }

    override def sSave(batchWriter: DataStreamWriter[Row], config: DataSinkConfig): Any = {
        super.sSave(batchWriter, config)
    }

    override def foreachBatchCallbackStreamEnable = false
    override def fullFormat: String = "console"

    override def shortFormat: String = "console"
}
