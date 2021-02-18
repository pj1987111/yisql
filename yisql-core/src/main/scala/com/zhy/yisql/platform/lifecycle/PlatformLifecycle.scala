package com.zhy.yisql.platform.lifecycle

import com.zhy.yisql.platform.runtime.StreamingRuntime

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 11:08
  *  \* Description: 
  *  \*/
trait PlatformLifecycle {
    def beforeRuntime(params: Map[String, String]): Unit

    def afterRuntime(runtime: StreamingRuntime, params: Map[String, String]): Unit

    def beforeDispatcher(runtime: StreamingRuntime, params: Map[String, String]): Unit

    def afterDispatcher(runtime: StreamingRuntime, params: Map[String, String]): Unit
}
