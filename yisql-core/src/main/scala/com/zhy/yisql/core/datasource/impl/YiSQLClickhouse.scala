package com.zhy.yisql.core.datasource.impl

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-06
 *  \* Time: 12:39
 *  \* Description: 
 *  \ */
class YiSQLClickhouse extends YiSQLJDBC {

  override def forceUseFormat: String = super.fullFormat

  override def fullFormat: String = "ck"

  override def shortFormat: String = fullFormat
}
