package com.zhy.yisql.rest

import org.apache.spark.sql.{SQLContext, SparkSession}
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 22:17
  *  \* Description: 
  *  \*/
class RestTest {
    def getSimpleSession = {
        SparkSession
                .builder()
                .master("local[*]")
                .appName("RunScriptExecutor")
                .enableHiveSupport()
                .getOrCreate()
    }

    @Test
    def testStartServer(): Unit = {
        val restClass = Class.forName("com.zhy.yisql.rest.Application")
        val method = restClass.getMethod("main", classOf[Array[String]])
        method.invoke(null, Array("--server.port=9090"))
        Thread.currentThread().join()
    }

    @Test
    def startThriftServer: Unit = {
        val tsClass = Class.forName("org.apache.spark.sql.hive.thriftserver.HiveThriftServer2$")
        val method = tsClass.getMethod("startWithContext", classOf[SQLContext])
        val instance = tsClass.getField("MODULE$").get(null)
        method.invoke(instance, getSimpleSession.sqlContext)
    }
}
