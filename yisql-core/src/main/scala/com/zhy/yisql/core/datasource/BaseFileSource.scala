package com.zhy.yisql.core.datasource

import com.zhy.yisql.core.dsl.ScriptSQLExec
import com.zhy.yisql.core.dsl.adaptor.DslTool
import org.apache.spark.sql._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 19:42
  *  \* Description: 
  *  \*/
trait BaseFileSource extends BatchSource with BatchSink with Registry with DslTool {
    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val context = ScriptSQLExec.getContext()
        val format = config.config.getOrElse("implClass", fullFormat)
        val owner = config.config.getOrElse("owner", context.owner)
        reader.options(config.config).format(format).load(resourceRealPath(context.execListener, Option(owner), config.path))
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        val context = ScriptSQLExec.getContext()
        val format = config.config.getOrElse("implClass", fullFormat)
        val partitionByCol = config.config.getOrElse("partitionByCol", "").split(",").filterNot(_.isEmpty)
        if (partitionByCol.length > 0) {
            writer.partitionBy(partitionByCol: _*)
        }
        writer.options(config.config).mode(config.mode).format(format).save(resourceRealPath(context.execListener, Option(context.owner), config.path))
    }

    override def register(): Unit = {
        DataSourceRegistry.register(fullFormat, this)
        DataSourceRegistry.register(shortFormat, this)
    }

    override def unRegister(): Unit = {
        DataSourceRegistry.unRegister(fullFormat)
        DataSourceRegistry.unRegister(shortFormat)
    }
}
