package com.zhy.yisql.core.datasource.impl
import com.zhy.yisql.core.datasource.DataSourceConfig
import org.apache.spark.sql.{DataFrame, DataFrameReader}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-13
  *  \* Time: 11:18
  *  \* Description: 
  *  \*/
class SQLAdhocKafka extends SQLKafka {

    override def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame = {
        super.bLoad(reader, config)
    }

    override def fullFormat: String = "org.apache.spark.sql.kafka010.AdHocKafkaSourceProvider"

    override def shortFormat: String = "adHocKafka"
}
