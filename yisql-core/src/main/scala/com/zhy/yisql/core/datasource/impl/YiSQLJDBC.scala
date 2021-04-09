package com.zhy.yisql.core.datasource.impl

import java.util.Properties

import com.zhy.yisql.common.utils.reflect.ScalaReflect
import com.zhy.yisql.core.datasource._
import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter, ForeachBatchRunner}
import org.apache.spark.sql._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-08
  *  \* Time: 10:22
  *  \* Description: 
  *  \*/
class YiSQLJDBC extends BaseStreamSource with BaseBatchSource {

  def forceUseFormat: String = ""

  def useFormat(config: Map[String, String]): String = {
    val format = if(forceUseFormat.length>0) {
      forceUseFormat
    } else {
      config.getOrElse("implClass", fullFormat)
    }
    format
  }

  override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
    var dbtable = config.path
    // if contains splitter, then we will try to find dbname in dbMapping.
    // otherwize we will do nothing since elasticsearch use something like index/type
    // it will do no harm.
    val format = useFormat(config.config)
    var url = config.config.get("url")
    reader.options(config.config)

    val table = if (config.config.contains("query")) {
      reader.option("query", config.config("query"))

      reader.format(format).load()
    } else if (config.config.contains("prePtnArray")) {
      val prePtn = config.config("prePtnArray")
          .split(config.config.getOrElse("prePtnDelimiter", ","))

      reader.jdbc(url.get, dbtable, prePtn, new Properties())
    } else {
      reader.option("dbtable", dbtable)

      reader.format(format).load()
    }

    val columns = table.columns
    val colNames = new Array[String](columns.length)
    for (i <- columns.indices) {
      val (dbtable, column) = parseTableAndColumnFromStr(columns(i))
      colNames(i) = column
    }
    val newdf = table.toDF(colNames: _*)
    newdf
  }

  override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
    var dbtable = config.path
    // if contains splitter, then we will try to find dbname in dbMapping.
    // otherwize we will do nothing since elasticsearch use something like index/type
    // it will do no harm.
    val format = useFormat(config.config)
    writer.mode(config.mode)
    //load configs should overwrite connect configs
    writer.options(config.config)
    config.config.get("partitionByCol").map { item =>
      writer.partitionBy(item.split(","): _*)
    }

    config.config.get("idCol").map { item =>
      import org.apache.spark.sql.jdbc.DataFrameWriterExtensions._
      val extraOptions = ScalaReflect.fromInstance[DataFrameWriter[Row]](writer)
          .method("extraOptions").invoke()
          .asInstanceOf[ {def toMap[T, U](implicit ev: _ <:< (T, U)): scala.collection.immutable.Map[T, U]}].toMap[String, String]
      val jdbcOptions = new JDBCOptions(extraOptions + ("dbtable" -> dbtable))
      writer.upsert(Option(item), jdbcOptions, config.df.get)
    }.getOrElse {
      writer.option("dbtable", dbtable)
      writer.format(format).save(dbtable)
    }
  }

  override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
    val newConfig = config.cloneWithNewMode("append")
    ForeachBatchRunner.run(dataStreamWriter, (batch: Dataset[Row], batchId: Long) => {
      bSave(batch.write, newConfig)
    })
  }

  override def skipFormat: Boolean = true

  override def fullFormat: String = "jdbc"

  override def shortFormat: String = fullFormat
}
