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

    //rest测试或直接执行
    val restMode = true
//    val restUrl = "http://127.0.0.1:9003/sql/run"
    val restUrl = "http://cdh217:9003/sql/run"

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

    @Before
    def startPlatform(): Unit = {
        //        val args = Array(
        //            "-streaming.master local[*]",
        //            "-streaming.name test",
        //            "-streaming.enableHiveSupport true",
        //            "-streaming.spark.service true",
        //            "-streaming.rest true")

        if(!restMode) {
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
