package com.zhy.yisql

import com.zhy.yisql.common.utils.base.TryTool
import com.zhy.yisql.common.utils.param.ParamsUtil
import com.zhy.yisql.platform.PlatformManager
import com.zhy.yisql.platform.lifecycle.PlatformLifecycle

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 14:43
  *  \* Description: 
  *  \*/
object StreamApp {
    def main(args: Array[String]): Unit = {
        val params = new ParamsUtil(args)
        require(params.hasParam("streaming.name"), "Application name should be set")
        val platform = PlatformManager.getOrCreate
        TryTool.tryOrExit {
            List("tech.mlsql.runtime.LogFileHook", "tech.mlsql.runtime.PluginHook").foreach { className =>
                platform.registerPlatformLifecycle(
                    Class.forName(className).
                            newInstance().asInstanceOf[PlatformLifecycle])
            }
        }

        platform.run(params)
    }
}

class StreamApp
