package com.zhy.yisql.rest

import com.zhy.yisql.common.utils.http.HttpClientCrawler
import com.zhy.yisql.common.utils.json.JSONTool
import org.apache.commons.httpclient.{HttpClient, HttpStatus}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.{DefaultHttpClient, HttpClients}
import org.apache.http.util.EntityUtils
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 13:21
  *  \* Description: 
  *  \*/
class SqlRunTest {
    @Test
    def test1(): Unit = {
        var params: Map[String, String] = Map()
        params += ("sql" ->
                """
                  |load kafka.`zhy` where
                  |kafka.bootstrap.servers="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
                  |and multiplyFactor="2"
                  |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
                  |as table1;
                  |
                  |--save append table1
                  |--as console.``;
                """.stripMargin)
        params += ("owner" -> "testzhy")
        val requestParams = JSONTool.toJsonStr(params)
        println(requestParams)
        val res = HttpClientCrawler.postJson(url = "http://127.0.0.1:9003/sql/run", params = params)
//        println(request("http://127.0.0.1:9003/sql/run", requestParams))
        println(res)
    }

    def request(url:String, jsonParam:String): String = {
        var res = ""
        try {
            val httpClient = HttpClients.createDefault
            val httpPost = new HttpPost(url)
            val se = new StringEntity(jsonParam, ContentType.APPLICATION_JSON)
            httpPost.setEntity(se)
            val response = httpClient.execute(httpPost)
            val statusCode = response.getStatusLine.getStatusCode
            if(statusCode == HttpStatus.SC_OK){
                val resEntity = response.getEntity
                res = EntityUtils.toString(resEntity)
            }
        } catch {
            case e: Exception =>
                e.printStackTrace()
        }
        res
    }

//    def request2(url:String, params: Map[String, String]): String = {
//        var res = ""
//        try {
//            val httpClient = HttpClients.createDefault
//            val httpPost = new HttpPost(url)
//            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"))
//
//            val se = new StringEntity(jsonParam, ContentType.APPLICATION_JSON)
//            httpPost.setEntity(se)
//            val response = httpClient.execute(httpPost)
//            val statusCode = response.getStatusLine.getStatusCode
//            if(statusCode == HttpStatus.SC_OK){
//                val resEntity = response.getEntity
//                res = EntityUtils.toString(resEntity)
//            }
//        } catch {
//            case e: Exception =>
//                e.printStackTrace()
//        }
//        res
//    }

}
