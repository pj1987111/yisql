package com.zhy.yisql.platform

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.{Map => JMap}

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 21:59
  *  \* Description: 
  *  \*/
class PlatformManager {
    //    val config = new AtomicReference[ParamsUtil]()

    val listeners = new ArrayBuffer[PlatformManagerListener]()

    def register(listener: PlatformManagerListener) = {
        listeners += listener
    }

    def unRegister(listener: PlatformManagerListener) = {
        listeners -= listener
    }

}

object PlatformManager {
    private val INSTANTIATION_LOCK = new Object()
    //SparkRuntime.RUNTIME_IS_READY.compareAndSet(false, true)
    val RUNTIME_IS_READY = new AtomicBoolean(false)

    /**
      * Reference to the last created SQLContext.
      */
    @transient private val lastInstantiatedContext = new AtomicReference[PlatformManager]()

    /**
      * Get the singleton SQLContext if it exists or create a new one using the given SparkContext.
      * This function can be used to create a singleton SQLContext object that can be shared across
      * the JVM.
      */
    def getOrCreate: PlatformManager = {
        INSTANTIATION_LOCK.synchronized {
            if (lastInstantiatedContext.get() == null) {
                new PlatformManager()
            }
        }
        lastInstantiatedContext.get()
    }

    private[platform] def clearLastInstantiatedContext(): Unit = {
        INSTANTIATION_LOCK.synchronized {
            lastInstantiatedContext.set(null)
        }
    }

    private[platform] def setLastInstantiatedContext(platformManager: PlatformManager): Unit = {
        INSTANTIATION_LOCK.synchronized {
            lastInstantiatedContext.set(platformManager)
        }
    }

    def createRuntimeByPlatform(name: String, tempParams: java.util.Map[Any, Any]) = {
        Class.forName(name).
                getMethod("getOrCreate", classOf[JMap[Any, Any]]).
                invoke(null, tempParams).asInstanceOf[StreamingRuntime]
    }

    def clear = {
        lastInstantiatedContext.set(null)
    }

    //    def getRuntime: StreamingRuntime = {
    //        val params: JMap[String, String] = getOrCreate.config.get().getParamsMap
    //        val tempParams: JMap[Any, Any] = new util.HashMap[Any, Any]()
    //        params.asScala.map(f => tempParams.put(f._1.asInstanceOf[Any], f._2.asInstanceOf[Any]))
    //
    //        val platformName = params.get("streaming.platform")
    //        val runtime = createRuntimeByPlatform(platformNameMapping(platformName), tempParams)
    //
    //        runtime
    //    }

    def SPAKR_STREAMING = "spark_streaming"
    def SPAKR_STRUCTURED_STREAMING = "spark_structured_streaming"
    def SPAKR_S_S = "ss"
    def SPARK = "spark"
    def FLINK_STREAMING = "flink_streaming"

    def platformNameMapping = Map[String, String](
        SPAKR_S_S -> "streaming.core.strategy.platform.SparkStructuredStreamingRuntime",
        SPAKR_STRUCTURED_STREAMING -> "streaming.core.strategy.platform.SparkStructuredStreamingRuntime",
        FLINK_STREAMING -> "streaming.core.strategy.platform.FlinkStreamingRuntime",
        SPAKR_STREAMING -> "streaming.core.strategy.platform.SparkStreamingRuntime",
        SPARK -> "streaming.core.strategy.platform.SparkRuntime"
    )
}
