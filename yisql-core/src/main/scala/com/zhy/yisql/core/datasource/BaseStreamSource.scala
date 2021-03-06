package com.zhy.yisql.core.datasource

import java.util.concurrent.TimeUnit

import com.zhy.yisql.core.dsl.adaptor.DslTool
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter, Trigger}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 14:37
  *  \* Description: 
  *  \*/
trait BaseStreamSource extends StreamSource with StreamSink with Registry with DslTool {

    override def sLoad(streamReader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        val format = config.config.getOrElse("implClass", fullFormat)
        streamReader.options(config.config).format(format).load()
    }

    override def sSave(streamWriter: DataStreamWriter[Row], config: DataSinkConfig): Any = {
        var option = config.config
        val duration = option("duration").toInt
        option -= "duration"

        val format = config.config.getOrElse("implClass", fullFormat)

        val writer: DataStreamWriter[Row] = streamWriter.format(format)

        if(!skipFormat)
            writer.format(format)
        writer.queryName(config.jobName.get)
        writer.outputMode(config.mode).options(option)

        foreachBatchCallback(writer, config)

        writer.trigger(Trigger.ProcessingTime(duration, TimeUnit.SECONDS)).start()
    }

    def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
        //do nothing by default
    }

    def skipFormat: Boolean = {
        false
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