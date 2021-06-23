package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.common.utils.reflect.ScalaReflect
import com.zhy.yisql.core.datasource._
import org.apache.spark.sql._
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.streaming.DataStreamReader

import java.util.Properties
import scala.language.reflectiveCalls

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-08
  *  \* Time: 10:22
  *  \* Description: 
  *  \*/
class YiSQLJDBC extends BaseMergeSource {

  def forceUseFormat: String = ""

  def useFormat(config: Map[String, String]): String = {
    val format: String = if(forceUseFormat.nonEmpty) {
      forceUseFormat
    } else {
      config.getOrElse("implClass", fullFormat)
    }
    format
  }

  override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
    val dbTable: String = config.path
    // if contains splitter, then we will try to find dbname in dbMapping.
    // otherwize we will do nothing since elasticsearch use something like index/type
    // it will do no harm.
    val format: String = useFormat(config.config)
    val url: Option[String] = config.config.get("url")
    reader.options(config.config)

    val table: DataFrame = if (config.config.contains("query")) {
      reader.option("query", config.config("query"))

      reader.format(format).load()
    } else if (config.config.contains("prePtnArray")) {
      val prePtn: Array[String] = config.config("prePtnArray")
          .split(config.config.getOrElse("prePtnDelimiter", ","))

      reader.jdbc(url.get, dbTable, prePtn, new Properties())
    } else {
      reader.option("dbTable", dbTable)

      reader.format(format).load()
    }

    val columns: Array[String] = table.columns
    val colNames = new Array[String](columns.length)
    for (i <- columns.indices) {
      val (_, column) = parseTableAndColumnFromStr(columns(i))
      colNames(i) = column
    }
    val newDf: DataFrame = table.toDF(colNames: _*)
    newDf
  }

  override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
    val dbTable: String = config.path
    // if contains splitter, then we will try to find dbname in dbMapping.
    // otherwize we will do nothing since elasticsearch use something like index/type
    // it will do no harm.
    val format: String = useFormat(config.config)
    writer.mode(config.mode)
    //load configs should overwrite connect configs
    writer.options(config.config)
    config.config.get("partitionByCol").map { item: String =>
      writer.partitionBy(item.split(","): _*)
    }

    config.config.get("idCol").map { item: String =>
      import org.apache.spark.sql.jdbc.DataFrameWriterExtensions._
      val extraOptions: Map[String, String] = ScalaReflect.fromInstance[DataFrameWriter[Row]](writer)
          .method("extraOptions").invoke()
          .asInstanceOf[ {def toMap[T, U](implicit ev: _ <:< (T, U)): scala.collection.immutable.Map[T, U]}].toMap[String, String]
      val jdbcOptions = new JDBCOptions(extraOptions + ("dbtable" -> dbTable))
      writer.upsert(Option(item), jdbcOptions, config.df.get)
    }.getOrElse {
      writer.option("dbtable", dbTable)
      writer.format(format).save(dbTable)
    }
  }

  override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def skipFormat: Boolean = true

  override def fullFormat: String = "jdbc"

  override def shortFormat: String = fullFormat
}
