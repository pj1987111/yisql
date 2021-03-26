package com.zhy.yisql.other

import com.zhy.yisql.common.utils.http.HttpClientCrawler
import com.zhy.yisql.common.utils.json.JSONTool
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-23
  *  \* Time: 22:43
  *  \* Description: 
  *  \*/
class HttpTest {
  @Test
  def test10(): Unit = {
    val restUrl = "http://localhost:9003/test"
    val res = HttpClientCrawler.postJson(url = restUrl, Map[String,String]())
    println(res)
  }

  @Test
  def test1(): Unit = {
    val restUrl = "http://localhost:2006/users"
    val exeMap = Map(
      "id" -> "1",
      "name" -> "zhy",
      "age" -> "33")

    val res = HttpClientCrawler.postJson(url = restUrl, params = exeMap)
//    val res = HttpClientCrawler.requestByMethod(url = restUrl, method = "POST", params = exeMap)
    println(res)
  }

  @Test
  def test2(): Unit = {
    val restUrl = "http://localhost:2006/users/2"

    val res = HttpClientCrawler.postJson(url = restUrl, params = Map[String, String]())
    //    val res = HttpClientCrawler.requestByMethod(url = restUrl, method = "POST", params = exeMap)
    println(res)
  }


}
