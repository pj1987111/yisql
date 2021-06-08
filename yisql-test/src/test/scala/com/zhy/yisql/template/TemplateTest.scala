package com.zhy.yisql.template

import com.zhy.yisql.core.dsl.template.TemplateMerge
import org.junit.Test

class TemplateTest {

  @Test
  def testTemplate1(): Unit = {
    var text =
      """
        |select __value__,get_json_object("__value__", "$.clusterId") as clusterId,get_json_object("__value__", "$.userId")
        |from loghub_store_out as json_out1;
        |""".stripMargin
    text = TemplateMerge.merge(text, Map[String, String]())
    val chunks = text.split("\\s+")
    val tableName = chunks.last.replace(";", "")
    val sql = try {
      text.replaceAll(s"((?i)as)[\\s|\\n]+${tableName}\\s*\\n*$$", "")
    } catch {
      case e: Exception =>
        text.split("(?i)as").dropRight(1).mkString("as")
    }
    println(text)
    println(sql)
  }
}
