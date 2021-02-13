package com.zhy.yisql.core.datasource

import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 14:16
  *  \* Description: 
  *  \*/
trait DataSource {
    def dbSplitter = {
        "."
    }

    def fullFormat: String

    def shortFormat: String

    def aliasFormat: String = {
        shortFormat
    }
}

trait BatchSource extends DataSource {
    def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame
}

trait BatchSink extends DataSource {
    def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any
}

trait StreamSource extends DataSource {
    def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame
}

trait StreamSink extends DataSource {
    def sSave(writer: DataStreamWriter[Row], config: DataSinkConfig): Any
}