package com.zhy.yisql.core

import com.zhy.yisql.core.execute.SQLExecute
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-10
  *  \* Time: 14:37
  *  \* Description: 
  *  \*/
class CommandTest {
    @Test
    def deltaSelect(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new SQLExecute(Map("defaultPathPrefix"->"/user/datacompute/export"))
        //        val executor = new RunScriptExecutor(Map())
        //todo 路径合并
        executor.sql(
            """
              |--!delta history /user/datacompute/export/tmp/delta/table11;
              |
              |--!delta show tables;
              |
              |--!delta info /tmp/delta/table11;
              |
              |--!delta compact /user/datacompute/export/tmp/delta/table11 10 1;
              |
              |!delta vacuum /user/datacompute/export/tmp/delta/table11;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def kafkaCommand(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new SQLExecute(Map("defaultPathPrefix"->"/user/datacompute/export"))
        //        val executor = new RunScriptExecutor(Map())
        executor.sql(
            """
              |--查看kafka checkpoint offset
              |--!kafka streamOffset /tmp/s-cpl6;
              |
              |--采样数据推测类型，并存储环境变量
              |--!kafka registerSchema 2 "10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092" zhy;
              |
              |--采样数据推测类型
              |!kafka schemaInfer 20 "10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092" zhy;
              |
              |--采样数据
              |--!kafka sampleData 20 "10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092" zhy;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

}
