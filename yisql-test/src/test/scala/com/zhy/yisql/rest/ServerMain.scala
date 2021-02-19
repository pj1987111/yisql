package com.zhy.yisql.rest

import com.zhy.yisql.StreamApp
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 13:00
  *  \* Description: 
  *  \*/
class ServerMain {
    @Test
    def run(): Unit = {
        val args = Array(
            "-streaming.master local[*]",
            "-streaming.name test",
            "-streaming.enableHiveSupport true",
            "-streaming.spark.service true",
            "-streaming.rest true",
            "-streaming.driver.port 9003")
        StreamApp.main(args)
    }
}
