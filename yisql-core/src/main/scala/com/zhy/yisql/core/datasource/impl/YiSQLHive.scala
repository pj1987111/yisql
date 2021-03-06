package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter, ForeachBatchRunner}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Dataset, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:19
  *  \* Description: 
  *  \*/
class YiSQLHive  extends BaseStreamSource with BaseBatchSource {

    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val format = config.config.getOrElse("implClass", fullFormat)
        reader.options(config.config).format(format).table(config.path)
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        writer.format(config.config.getOrElse("file_format", "parquet"))
        val options = config.config - "file_format" - "implClass"
        config.config.get("partitionByCol").map(partitionColumn => partitionColumn.split(",").filterNot(_.isEmpty)).filterNot(_.length == 0)
                .map(partitionColumns => writer.partitionBy(partitionColumns: _*))
        writer.options(options).mode(config.mode).saveAsTable(config.path)
    }

    override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
    }

    override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
        val newConfig = config.cloneWithNewMode("append")
        ForeachBatchRunner.run(dataStreamWriter, (batch:Dataset[Row], batchId:Long) => {
            bSave(batch.write, newConfig)
        })
    }

    override def skipFormat: Boolean = true

    override def fullFormat: String = "hive"

    override def shortFormat: String = fullFormat
}
