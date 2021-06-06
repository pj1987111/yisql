package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseMergeSource, DataSourceConfig}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.DataStreamReader

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-07
  *  \* Time: 15:19
  *  \* Description: 
  *  \*/
class YiSQLElasticSearch extends BaseMergeSource {

  override def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame = {
    throw new RuntimeException(s"stream load is not support with ${shortFormat} ")
  }

  override def skipFormat: Boolean = true

  override def fullFormat: String = "org.elasticsearch.spark.sql"

  override def shortFormat: String = "es"

  override def dbSplitter: String = "/"

}
