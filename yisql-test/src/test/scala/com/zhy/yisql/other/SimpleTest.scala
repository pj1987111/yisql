package com.zhy.yisql.other

import java.io.DataOutputStream
import java.net.Socket

import com.zhy.yisql.common.utils.bean.BeanUtils
import org.apache.commons.lang3.StringUtils
import tech.mlsql.common.utils.base.Templates
//import com.zhy.yisql.rest.entity.SQLRunEntity
import org.junit.Test

import org.apache.spark.util.kvstore._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 21:08
  *  \* Description: 
  *  \*/
class SimpleTest {
  //    @Test
  //    def beanTest(): Unit = {
  //        val entity = new SQLRunEntity
  ////        entity.setCallback()
  //        println(entity)
  //        val mapV = BeanUtils.getCCParams(entity)
  //        println(mapV)
  //    }

  @Test
  def mapTest(): Unit = {
    var params = Map[String, String]()
    params += ("123" -> null)
    params += ("123" -> "123")
    val v1 = params.getOrElse("123", "123")
    val v2 = params.getOrElse("1234", "123")

    var params2 = Map[String, String]()
    params2 += ("123" -> "23")
    params2 += ("1234" -> "234")
    params2 += ("12345" -> "2345")

    val params3 =  mergeOptions(params, params2)

    println(params)
  }

  @Test
  def testSocket(): Unit = {
    val socket = new Socket("127.0.0.1", 6049)
    val dOut = new DataOutputStream(socket.getOutputStream)
    for (a <- 1 to 20) {
      println(a)
      dOut.write("123456\n".getBytes())
      dOut.flush()
    }
    //    dOut.close()
  }

  def mergeOptions(optionTemplate: Map[String, String], thisOption: Map[String, String]): Map[String, String] = {
    var params = optionTemplate
    for (params2_entry <- thisOption) {
      params += (params2_entry._1 -> params2_entry._2)
    }
    params
  }

  @Test
  def simple(): Unit = {
    val partionSpec = "partition(date=20210112,version=1)"
    val dsV =StringUtils.substringBetween(StringUtils.substringAfter(partionSpec, "partition"), "(", ")").split(",")
    println(1)
  }

  @Test
  def templateTest(): Unit = {
    val str = "run command as PythonCommand.`` where parameters='''{:all}''' as {-1:next(named,uuid())}"
    val seq = Seq("on", "orginal_text_corpus",
      "\ndata = context.fetch_once_as_rows()\ndef process(data):\n    for row in data:\n        new_row = { }\n        new_row[\"content\"] = \"---\" + row[\"content\"]+\"---\"\n        yield new_row\n\ncontext.build_result(process(data))",
      "named", "mlsql_temp_table")
    val txt = Templates.evaluate(str, seq)
    println(txt)
  }
}
