package com.zhy.yisql.core.platform.runtime

import java.util.{Map => JMap}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 22:01
  *  \* Description: 
  *  \*/
trait StreamingRuntime {
    def startRuntime: StreamingRuntime

    def destroyRuntime(stopGraceful: Boolean, stopContext: Boolean = false): Boolean

    def streamingRuntimeInfo: StreamingRuntimeInfo

    def configureStreamingRuntimeInfo(streamingRuntimeInfo: StreamingRuntimeInfo)

    def awaitTermination

    def startThriftServer

    def startHttpServer

    def params: JMap[Any, Any]
}


trait StreamingRuntimeInfo

trait Event

trait PlatformManagerListener {
    def processEvent(event: Event)
}
