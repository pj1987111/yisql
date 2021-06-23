package com.zhy.yisql.core.util

import com.zhy.yisql.core.execute.SQLExecuteContext

import scala.collection.mutable

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-02
  *  \* Time: 08:22
  *  \* Description: 
  *  \*/
object ScriptEnvDecode {

  /**
    * 将a:b这种 b的值如果在环境变量中已经配置就替换
    *
    * @param code
    * @return
    */
  def decode(code: String): String = {

    if (code == null || code.isEmpty) return code

    val envMap: mutable.Map[String, String] = SQLExecuteContext.getContext().execListener.env()

    val codes: Array[String] = code.split(" ")

    for (i <- codes.indices if codes(i).nonEmpty) {
      val tempCode: String = codes(i)
      if (tempCode.contains(":")) {
        val index: Int = tempCode.indexOf(":")
        val key: String = tempCode.substring(index + 1)
        val value: Option[String] = envMap.get(key)
        if (value.isDefined) {
          codes(i) = tempCode.replaceAll(s":$key", value.get)
        }
      }
    }
    codes.mkString(" ")
  }
}

