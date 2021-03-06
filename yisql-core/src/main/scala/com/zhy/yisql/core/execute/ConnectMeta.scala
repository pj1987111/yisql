package com.zhy.yisql.core.execute

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-08
  *  \* Time: 23:51
  *  \* Description: 保存全局connect信息
  *  \*/
object ConnectMeta {
  //user defined format -> (format->jdbc,url->....)
  private val formatMapping = new ConcurrentHashMap[String, (String, Map[String, String])]()

  def options(formatAlias: String, format: String, _options: Map[String, String]) = {
    formatMapping.put(formatAlias, (format, _options))
  }

  def options(key: String) = {
    if (formatMapping.containsKey(key)) {
      Option(formatMapping.get(key))
    } else None
  }

  def presentThenCall(key: String, f: ((String, Map[String, String])) => Unit) = {
    if (formatMapping.containsKey(key)) {
      val item = formatMapping.get(key)
      f(item)
      Option(item)
    } else None
  }

  def toMap = {
    formatMapping.asScala.toMap
  }

  def removeFormatAlias(formatAlias: String) = {
    formatMapping.remove(formatAlias)
  }
}