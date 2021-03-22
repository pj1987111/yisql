package com.zhy.yisql.core.datasource.impl

import java.io.DataOutputStream
import java.net.Socket

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}

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

//  override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
//    val option = config.config
//    val host = option.getOrElse("host", "127.0.0.1")
//    val port = option.getOrElse("port", "6049")
//    val socket = new Socket(host, port.toInt)
//
//    val dOut = new DataOutputStream(socket.getOutputStream)
//
//    writer
//    dOut.writeInt(bytes.length)
//    dOut.write(bytes)
//    dOut.flush()
//  }

  override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {

  }

  override def fullFormat: String = "com.zhy.yisql.core.datasource.sink.YiSQLConsoleSinkProvider"

  override def shortFormat: String = "webConsole"
}
