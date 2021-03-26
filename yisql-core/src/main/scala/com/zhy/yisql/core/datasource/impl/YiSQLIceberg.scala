package com.zhy.yisql.core.datasource.impl

import com.zhy.yisql.core.datasource.{BaseBatchSource, BaseStreamSource}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-23
  *  \* Time: 10:21
  *  \* Description: 
  *  \*/
class YiSQLIceberg extends BaseStreamSource with BaseBatchSource {
  override def fullFormat: String = "iceberg"

  override def shortFormat: String = fullFormat
}
