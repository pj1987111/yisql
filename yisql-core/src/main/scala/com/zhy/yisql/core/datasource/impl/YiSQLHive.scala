package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseMergeSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.streaming.DataStreamReader
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:19
  *  \* Description: 
  *  \*/
class YiSQLHive extends BaseMergeSource {

  override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
    val format: String = config.config.getOrElse("implClass", fullFormat)
    reader.options(config.config).format(format).table(config.path)
  }

  override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
    writer.format(config.config.getOrElse("file_format", "parquet"))
    val options: Map[String, String] = config.config - "file_format" - "implClass"
    config.config.get("partitionByCol").map((partitionColumn: String) => partitionColumn.split(",").filterNot((_: String).isEmpty)).filterNot((_: Array[String]).length == 0)
      .map((partitionColumns: Array[String]) => writer.partitionBy(partitionColumns: _*))
    writer.options(options).mode(config.mode).saveAsTable(config.path)
  }

  override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def skipFormat: Boolean = true

  override def fullFormat: String = "hive"

  override def shortFormat: String = fullFormat
}
