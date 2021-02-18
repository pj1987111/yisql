package com.zhy.yisql.core

import com.zhy.yisql.common.utils.param.ParamsUtil
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

    @Before
    def startPlatform(): Unit = {
        val args = Array(
            "-streaming.master local[*]",
            "-streaming.name test",
            "-streaming.enableHiveSupport true",
            "-streaming.spark.service true",
            "-streaming.rest true")

        //        val args = Array(
        //            "-streaming.master local[*]",
        //            "-streaming.name test",
        //            "-streaming.enableHiveSupport true",
        //            "-streaming.spark.service true",
        //            "-streaming.unitest.awaitTermination false")
        val params = new ParamsUtil(args)
        require(params.hasParam("streaming.name"), "Application name should be set")
        val platform = PlatformManager.getOrCreate
        platform.run(params)
    }
}
