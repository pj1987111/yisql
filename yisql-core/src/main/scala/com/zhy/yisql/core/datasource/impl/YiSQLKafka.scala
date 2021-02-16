package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.common.utils.json.SparkSchemaJsonParser
import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:18
  *  \* Description: 
  *  \*/
class YiSQLKafka extends BaseStreamSource with BaseBatchSource {

    //valueSchema="st(field(id,string),field(name,string),field(messgae,string),field(date,string),field(version,integer))"
    override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        val option = config.config
        val format = option.getOrElse("implClass", fullFormat)
        var loadTable = streamReader.options(rewriteKafkaConfig(option, getSubscribe, getLoadUrl, config.path)).format(format).load()
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
        val option = config.config
        val format = config.config.getOrElse("implClass", fullFormat)
        var loadTable = reader.options(rewriteKafkaConfig(config.config, getSubscribe, getLoadUrl, config.path)).format(format).load()
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

    def getSubscribe = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "topics"
        } else "subscribe"
    }

    def getLoadUrl = {
        "kafka.bootstrap.servers"
    }

    def getSaveUrl = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "metadata.broker.list"
        } else "kafka.bootstrap.servers"
    }

    def getKafkaBrokers(config: Map[String, String], url: String) = {
        url -> config.getOrElse("metadata.broker.list", config.get("kafka.bootstrap.servers").get)
    }

    def getWriteTopic = {
        if (shortFormat == "kafka8" || shortFormat == "kafka9") {
            "topics"
        } else "topic"
    }


    def rewriteKafkaConfig(config: Map[String, String], topicKey: String, url: String, path: String): Map[String, String] = {
        var temp = (config - "metadata.broker.list" - "kafka.bootstrap.servers") ++ Map(
            getKafkaBrokers(config, url)
        )
        if (path != null && !path.isEmpty) {
            temp = temp ++ Map(topicKey -> path)
        }
        temp
    }

    override def fullFormat: String = "kafka"

    override def shortFormat: String = "kafka"
}
