package com.zhy.yisql.core

import com.zhy.yisql.common.utils.http.HttpClientCrawler
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.param.ParamsUtil
import com.zhy.yisql.core.execute.SQLExecute
import com.zhy.yisql.core.platform.PlatformManager
import org.junit.Before

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 21:59
  *  \* Description: 
  *  \*/
class BaseTest {
//        val rootUrl = "cdh217:9003"
    val rootUrl = "127.0.0.1:9003"
    //rest测试或直接执行
    val restMode = true
    val restUrl = s"http://$rootUrl/sql/run"
    val jobListUrl = s"http://$rootUrl/job/list"
    val jobKillUrl = s"http://$rootUrl/job/kill"

    val exeMap = Map(
        "defaultPathPrefix" -> "/user/datacompute/export",
        "owner" -> "testzhy")

    def sqlParseInner(sql: String): Unit = {
        val sqlExeMap = exeMap + ("sql" -> sql)
        val res = if (restMode) {
            val requestParams = JSONTool.toJsonStr(sqlExeMap)
            HttpClientCrawler.requestByMethod(url = restUrl, method = "POST", params = sqlExeMap)
        } else {
            val executor = new SQLExecute(sqlExeMap)
            executor.sql(sql)
            executor.simpleExecute()
        }
        println(res)
    }

    def jobList(url: String) = {
        val res = if (restMode) {
            HttpClientCrawler.requestByMethod(url = url, method = "POST", Map[String, String]())
        }
        println(res)
    }

    def jobKill(url: String, gId: Option[String], jobName: Option[String]) = {
        var killMap = Map[String, String]()
        if(gId.isDefined)
            killMap+=("groupId"->gId.get)
        if(jobName.isDefined)
            killMap+=("jobName"->jobName.get)
        val res = if (restMode) {
            HttpClientCrawler.requestByMethod(url = url, method = "POST", killMap)
        }
        println(res)
    }

    @Before
    def startPlatform(): Unit = {
        //        val args = Array(
        //            "-streaming.master local[*]",
        //            "-streaming.name test",
        //            "-streaming.enableHiveSupport true",
        //            "-streaming.spark.service true",
        //            "-streaming.rest true")

        if (!restMode) {
            val args = Array(
                "-streaming.master local[*]",
                "-streaming.name test",
                "-streaming.enableHiveSupport true",
                "-streaming.spark.service true",
                "-streaming.unitest.awaitTermination false")
            val params = new ParamsUtil(args)
            require(params.hasParam("streaming.name"), "Application name should be set")
            val platform = PlatformManager.getOrCreate
            platform.run(params)
        }
        //restmode需要启动ServerMain方法的rest服务
    }
}
