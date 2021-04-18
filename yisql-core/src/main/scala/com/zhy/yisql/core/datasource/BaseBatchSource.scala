package com.zhy.yisql.core.datasource

import com.zhy.yisql.core.dsl.adaptor.DslTool
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 19:42
  *  \* Description: 
  *  \*/
trait BaseBatchSource extends BatchSource with BatchSink with Registry with DslTool {
    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
//        var dbtable = config.path
//        // if contains splitter, then we will try to find dbname in dbMapping.
//        // otherwize we will do nothing since elasticsearch use something like index/type
//        // it will do no harm.
//        val format = config.config.getOrElse("implClass", fullFormat)
//        //load configs should overwrite connect configs
//        reader.options(config.config)
//
//        val table = reader.format(format).load
//        val columns = table.columns
//        val colNames = new Array[String](columns.length)
//        for (i <- columns.indices) {
//            val (dbtable, column) = parseTableAndColumnFromStr(columns(i))
//            colNames(i) = column
//        }
//        val newdf = table.toDF(colNames: _*)
//        newdf

        var dbtable = config.path
        // if contains splitter, then we will try to find dbname in dbMapping.
        // otherwize we will do nothing since elasticsearch use something like index/type
        // it will do no harm.
        val format = config.config.getOrElse("implClass", fullFormat)
        //load configs should overwrite connect configs
        reader.options(config.config)
        reader.format(format).load(dbtable)
    }

    override def cleanStr(str: String): String = {
        if (str.startsWith("`") || str.startsWith("\""))
            str.substring(1, str.length - 1)
        else str
    }

    def parseTableAndColumnFromStr(str: String): (String, String) = {
        val cleanedStr = cleanStr(str)
        val dbAndTable = cleanedStr.split("\\.")
        if (dbAndTable.length > 1) {
            val table = dbAndTable(0)
            val column = dbAndTable.splitAt(1)._2.mkString(".")
            (table, column)
        } else {
            (cleanedStr, cleanedStr)
        }
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        var dbtable = config.path
        // if contains splitter, then we will try to find dbname in dbMapping.
        // otherwize we will do nothing since elasticsearch use something like index/type
        // it will do no harm.
        writer.mode(config.mode)
        //load configs should overwrite connect configs
        writer.options(config.config)
        config.config.get("partitionByCol").map { item =>
            writer.partitionBy(item.split(","): _*)
        }
        writer.format(config.config.getOrElse("implClass", fullFormat)).save(dbtable)
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
