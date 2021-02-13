package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataLake, DataSinkConfig, DataSourceConfig}
import com.zhy.yisql.core.dsl.ScriptSQLExec
import org.apache.spark.sql.delta.sources.DeltaDataSource
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row, functions => F}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-08
  *  \* Time: 10:25
  *  \* Description: 
  *  \*/
class SQLDelta extends BaseStreamSource with BaseBatchSource {
    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val context = ScriptSQLExec.getContext()
        val format = config.config.getOrElse("implClass", fullFormat)
        val owner = config.config.get("owner").getOrElse(context.owner)

        //timestampAsOf
        val parameters = config.config

        def paramValidate = {
            throw new RuntimeException("Both startingVersion,endingVersion are  required")
        }

        def buildDF(version: Long) = {
            val newOpt = if (version > 0) Map("versionAsOf" -> version.toString) else Map()
            val reader = config.df.get.sparkSession.read

            val dataLake = new DataLake(config.df.get.sparkSession)
            val finalPath = if (dataLake.isEnable) {
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
                (start until end).map { version =>
                    buildDF(version).withColumn("__delta_version__", F.lit(version))
                }.reduce((total, cur) => total.union(cur))
        }
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        val context = ScriptSQLExec.getContext()
        val format = config.config.getOrElse("implClass", fullFormat)
        val partitionByCol = config.config.getOrElse("partitionByCol", "").split(",").filterNot(_.isEmpty)
        if (partitionByCol.length > 0) {
            writer.partitionBy(partitionByCol: _*)
        }

        val dataLake = new DataLake(config.df.get.sparkSession)
        val finalPath = if (dataLake.isEnable) {
            dataLake.identifyToPath(config.path)
        } else {
            resourceRealPath(context.execListener, Option(context.owner), config.path)
        }
        writer.options(config.config).mode(config.mode).format(format).save(finalPath)
    }

    override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        val format = config.config.getOrElse("implClass", fullFormat)
        val context = ScriptSQLExec.getContext()
        val owner = config.config.get("owner").getOrElse(context.owner)

        val dataLake = new DataLake(config.df.get.sparkSession)
        val finalPath = if (dataLake.isEnable) {
            dataLake.identifyToPath(config.path)
        } else {
            resolvePath(config.path, owner)
        }

        reader.options(config.config).format(format).load(finalPath)
    }

    def resolvePath(path: String, owner: String): String = {
        val context = ScriptSQLExec.getContext()
        resourceRealPath(context.execListener, Option(owner), path)
    }

    override def sSave(streamWriter: DataStreamWriter[Row], config: DataSinkConfig): Any = {
        val dataLake = new DataLake(config.df.get.sparkSession)
        val context = ScriptSQLExec.getContext()
        val finalPath = if (dataLake.isEnable) {
            dataLake.identifyToPath(config.path)
        } else {
            resolvePath(config.path, context.owner)
        }
//        val newConfig = config.copy(
//            config = Map("path" -> config.path, "__path__" -> finalPath) ++ config.config ++ Map("dbtable" -> finalPath))
        val newConfig = config.copy(
            config = Map("path" -> finalPath, "__path__" -> finalPath) ++ config.config ++ Map("dbtable" -> finalPath))
        super.sSave(streamWriter, newConfig)
    }

    override def fullFormat: String = "org.apache.spark.sql.delta.sources.MLSQLDeltaDataSource"

    override def shortFormat: String = "delta"
}
