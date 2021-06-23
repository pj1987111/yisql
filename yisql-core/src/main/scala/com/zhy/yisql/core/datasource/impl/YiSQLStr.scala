package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, DataSinkConfig, DataSourceConfig}
import com.zhy.yisql.core.execute.{ExecuteContext, SQLExecuteContext}
import org.apache.spark.sql._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-09
  *  \* Time: 16:20
  *  \* Description: 
  *  \*/
class YiSQLJsonStr extends BaseBatchSource {
    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val option = config.config
        val context: ExecuteContext = SQLExecuteContext.getContext()
        val items: Array[String] = cleanBlockStr(context.execListener.env()(cleanStr(config.path))).split("\n")
        val spark: SparkSession = config.df.get.sparkSession
        import spark.implicits._
        val loadTable: DataFrame = reader.options(config.config).json(spark.createDataset[String](items))
        //        if (option.getOrElse("withRaw", "false").toBoolean) {
        //            loadTable.select()
        //        }
        loadTable
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        throw new RuntimeException(s"save is not supported in ${shortFormat}")
    }

    override def fullFormat: String = "jsonStr"

    override def shortFormat: String = fullFormat
}

class YiSQLCsvStr extends BaseBatchSource {

    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val context: ExecuteContext = SQLExecuteContext.getContext()
        val items: Array[String] = cleanBlockStr(context.execListener.env()(cleanStr(config.path))).split("\n")
        val spark: SparkSession = config.df.get.sparkSession
        import spark.implicits._
        reader.options(config.config).csv(spark.createDataset[String](items))
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        throw new RuntimeException(s"save is not supported in ${shortFormat}")
    }

    override def fullFormat: String = "csvStr"

    override def shortFormat: String = fullFormat
}
