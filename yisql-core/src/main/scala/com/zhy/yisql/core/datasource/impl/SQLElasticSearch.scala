package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource, DataSinkConfig, DataSourceConfig, ForeachBatchRunner}
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.streaming.{DataStreamReader, DataStreamWriter}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:19
  *  \* Description: 
  *  \*/
class SQLElasticSearch extends BaseStreamSource with BaseBatchSource {

    override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
        throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
    }

    override def foreachBatchCallback(dataStreamWriter: DataStreamWriter[Row], config: DataSinkConfig): Unit = {
        val newConfig = config.cloneWithNewMode("append")
        ForeachBatchRunner.run(dataStreamWriter, (batch:Dataset[Row], batchId:Long) => {
            bSave(batch.write, newConfig)
        })
    }

    override def skipFormat: Boolean = true

    override def fullFormat: String = "org.elasticsearch.spark.sql"

    override def shortFormat: String = "es"

    override def dbSplitter: String = "/"

}
