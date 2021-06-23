package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.datalake.DataLake
import com.zhy.yisql.core.datasource.{BaseMergeSource, DataSinkConfig, DataSourceConfig}
import com.zhy.yisql.core.execute.{ExecuteContext, SQLExecuteContext}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row, functions => F}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-08
  *  \* Time: 10:25
  *  \* Description: 
  *  \*/
class YiSQLDelta extends BaseMergeSource {
  override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val format: String = config.config.getOrElse("implClass", fullFormat)
    val owner: String = config.config.getOrElse("owner", context.owner)

    //timestampAsOf
    val parameters: Map[String, String] = config.config

    def paramValidate = {
      throw new RuntimeException("Both startingVersion,endingVersion are  required")
    }

    def buildDF(version: Long): DataFrame = {
      val newOpt: Map[_ <: String, String] = if (version > 0) Map("versionAsOf" -> version.toString) else Map()
      val reader: DataFrameReader = config.df.get.sparkSession.read

      val dataLake = new DataLake(config.df.get.sparkSession)
      val finalPath: String = if (dataLake.isEnable) {
        dataLake.identifyToPath(config.path)
      } else {
        resourceRealPath(context.execListener, Option(owner), config.path)
      }

      reader.options(config.config ++ newOpt).
          format(format).
          load(finalPath)
    }

    //todo 获取最大版本号
    (parameters.get("startingVersion").map(_.toLong), parameters.get("endingVersion").map(_.toLong)) match {
      case (None, None) => buildDF(-1)
      case (None, Some(_)) => throw paramValidate
      case (Some(_), None) => throw paramValidate
      case (Some(start), Some(end)) =>
        (start until end).map { version: Long =>
          buildDF(version).withColumn("__delta_version__", F.lit(version))
        }.reduce((total: DataFrame, cur: DataFrame) => total.union(cur))
    }
  }

  override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val format: String = config.config.getOrElse("implClass", fullFormat)
    val partitionByCol: Array[String] = config.config.getOrElse("partitionByCol", "").split(",").filterNot(_.isEmpty)
    if (partitionByCol.length > 0) {
      writer.partitionBy(partitionByCol: _*)
    }

    val dataLake = new DataLake(config.df.get.sparkSession)
    val finalPath: String = if (dataLake.isEnable) {
      dataLake.identifyToPath(config.path)
    } else {
      resourceRealPath(context.execListener, Option(context.owner), config.path)
    }
    writer.options(config.config).mode(config.mode).format(format).save(finalPath)
  }

  override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    val format: String = config.config.getOrElse("implClass", fullFormat)
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val owner: String = config.config.getOrElse("owner", context.owner)

    val dataLake = new DataLake(config.df.get.sparkSession)
    val finalPath: String = if (dataLake.isEnable) {
      dataLake.identifyToPath(config.path)
    } else {
      resolvePath(config.path, owner)
    }

    reader.options(config.config).format(format).load(finalPath)
  }

  def resolvePath(path: String, owner: String): String = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    resourceRealPath(context.execListener, Option(owner), path)
  }

  override def sSave(streamWriter: DataStreamWriter[Row], config: DataSinkConfig): Any = {
    val dataLake = new DataLake(config.df.get.sparkSession)
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val finalPath: String = if (dataLake.isEnable) {
      dataLake.identifyToPath(config.path)
    } else {
      resolvePath(config.path, context.owner)
    }
    //        val newConfig = config.copy(
    //            config = Map("path" -> config.path, "__path__" -> finalPath) ++ config.config ++ Map("dbtable" -> finalPath))
    val newConfig: DataSinkConfig = config.copy(
      config = Map("path" -> finalPath, "__path__" -> finalPath) ++ config.config ++ Map("dbtable" -> finalPath))
    super.sSave(streamWriter, newConfig)
  }

  override def fullFormat: String = "org.apache.spark.sql.delta.sources.MLSQLDeltaDataSource"

  override def shortFormat: String = "delta"
}
