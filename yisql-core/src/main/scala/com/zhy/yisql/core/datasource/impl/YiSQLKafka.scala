package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseMergeSource, DataSinkConfig, DataSourceConfig}
import com.zhy.yisql.core.util.SparkSchemaJsonParser
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:18
  *  \* Description: 
  *  \*/
class YiSQLKafka extends BaseMergeSource {

    //valueSchema="st(field(id,string),field(name,string),field(messgae,string),field(date,string),field(version,integer))"
    override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        val option: Map[String, String] = config.config
        val format: String = option.getOrElse("implClass", fullFormat)
        var loadTable: DataFrame = streamReader.options(rewriteKafkaConfig(option, getSubscribe, getLoadUrl, config.path)).format(format).load()
        if (option.contains("valueSchema")) {
            loadTable = SparkSchemaJsonParser.parseDataFrame(
                loadTable.selectExpr("CAST(value AS STRING) as value"),
                option("valueSchema"),
                option.getOrElse("containRaw", "true").toBoolean)
        }
        loadTable
    }

    override def sSave(streamWriter: DataStreamWriter[Row], config: DataSinkConfig): Any = {
        super.sSave(streamWriter, config.copy(config = rewriteKafkaConfig(config.config, getWriteTopic, getSaveUrl, config.path)))
    }

    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        val option: Map[String, String] = config.config
        val format: String = config.config.getOrElse("implClass", fullFormat)
        var loadTable: DataFrame = reader.options(rewriteKafkaConfig(config.config, getSubscribe, getLoadUrl, config.path)).format(format).load()
        if (option.contains("valueSchema")) {
            loadTable = SparkSchemaJsonParser.parseDataFrame(
                loadTable.selectExpr("CAST(value AS STRING) as value"),
                option("valueSchema"),
                option.getOrElse("containRaw", "true").toBoolean)
        }
        loadTable
    }

    override def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any = {
        writer.options(rewriteKafkaConfig(config.config, getWriteTopic, getSaveUrl, config.path)).format(fullFormat).save()
    }

    def getSubscribe: String = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "topics"
        } else "subscribe"
    }

    def getLoadUrl: String = {
        "kafka.bootstrap.servers"
    }

    def getSaveUrl: String = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "metadata.broker.list"
        } else "kafka.bootstrap.servers"
    }

    def getKafkaBrokers(config: Map[String, String], url: String): (String, String) = {
        url -> config.getOrElse("metadata.broker.list", config("kafka.bootstrap.servers"))
    }

    def getWriteTopic: String = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "topics"
        } else "topic"
    }


    def rewriteKafkaConfig(config: Map[String, String], topicKey: String, url: String, path: String): Map[String, String] = {
        var temp: Map[String, String] = (config - "metadata.broker.list" - "kafka.bootstrap.servers") ++ Map(
            getKafkaBrokers(config, url)
        )
        if (path != null && path.nonEmpty) {
            temp = temp ++ Map(topicKey -> path)
        }
        temp
    }

    override def foreachBatchCallbackStreamEnable = false

    override def fullFormat: String = "kafka"

    override def shortFormat: String = "kafka"
}
