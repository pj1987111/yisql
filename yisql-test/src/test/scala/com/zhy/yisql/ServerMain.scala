package com.zhy.yisql

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 13:00
  *  \* Description: 
  *  \*/
object ServerMain {
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","admin")
        val args = Array(
            "-streaming.master local[*]",
            //            "-spark.yarn.jars /Volumes/workspace/install/spark/spark-2.4.6-bin-hadoop2.6/jars/*.jar",
            //            "-spark.yarn.jars /user/yarn_jars/yisql/*.jar",
            //            "-streaming.master yarn",
            "-streaming.name test",
            "-streaming.enableHiveSupport true",
            "-streaming.spark.service true",
            "-streaming.rest true",
            "-streaming.driver.port 9003")
        StreamApp.main(args)
    }
}
