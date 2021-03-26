package com.zhy.yisql.core.platform

import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}
import java.util.{Map => JMap}

import com.zhy.yisql.common.utils.base.TryTool
import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.common.utils.param.ParamsUtil
import com.zhy.yisql.core.platform.lifecycle.PlatformLifecycle
import com.zhy.yisql.core.platform.runtime.{PlatformManagerListener, StreamingRuntime}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 21:59
  *  \* Description: 
  *  \*/
class PlatformManager extends Logging {
    self =>
    val config = new AtomicReference[ParamsUtil]()
    val listeners = new ArrayBuffer[PlatformManagerListener]()
    val lifeCycleCallback = ArrayBuffer[PlatformLifecycle]()
    //    var zk: ZKClient = _

    def register(listener: PlatformManagerListener) = {
        listeners += listener
    }

    def unRegister(listener: PlatformManagerListener) = {
        listeners -= listener
    }

    def registerPlatformLifecycle(listener: PlatformLifecycle) = {
        lifeCycleCallback += listener
    }

    def unRegisterPlatformLifecycle(listener: PlatformLifecycle) = {
        lifeCycleCallback -= listener
    }

    def startRestServer(runtime: StreamingRuntime) = {
        runtime.startHttpServer
    }

    def startThriftServer(runtime: StreamingRuntime) = {
        runtime.startThriftServer
    }

    //    def registerToZk(params: ParamsUtil) = {
    //        zk = ZkRegister.registerToZk(params)
    //    }


    def run(_params: ParamsUtil, reRun: Boolean = false) = {

        if (!reRun) {
            config.set(_params)
        }

        val params = config.get()

        val lastStreamingRuntimeInfo = if (reRun) {
            val tempRuntime = PlatformManager.getRuntime
            tempRuntime.getClass.getMethod("clearLastInstantiatedContext").invoke(null)
            Some(tempRuntime.streamingRuntimeInfo)
        } else None


        val tempParams = new java.util.HashMap[Any, Any]()
        params.getParamsMap.asScala.filter(f => f._1.startsWith("streaming.")).foreach { f => tempParams.put(f._1, f._2) }


        TryTool.tryLogNonFatalError {
            lifeCycleCallback.foreach(f => f.beforeRuntime(params.getParamsMap.asScala.toMap))
        }

        val runtime = PlatformManager.getRuntime

        TryTool.tryLogNonFatalError {
            lifeCycleCallback.foreach(f => f.afterRuntime(runtime, params.getParamsMap.asScala.toMap))
        }


        //        val dispatcher = findDispatcher
        //
        //        var jobs: Array[String] = dispatcher.strategies.asScala.filter(f => f._2.isInstanceOf[JobStrategy]).keys.toArray
        //
        //        if (params.hasParam("streaming.jobs"))
        //            jobs = params.getParam("streaming.jobs").split(",")
        //
        //
        //        lastStreamingRuntimeInfo match {
        //            case Some(ssri) =>
        //                runtime.configureStreamingRuntimeInfo(ssri)
        //                runtime.resetRuntimeOperator(null)
        //            case None =>
        //        }

        if (params.getBooleanParam("streaming.rest", true) && !reRun) {
            startRestServer(runtime)
        }

        if (params.getBooleanParam("streaming.thrift", false) && !reRun) {
            startThriftServer(runtime)
        }

        //        if (params.hasParam("streaming.zk.conf_root_dir") && !reRun) {
        //            registerToZk(params)
        //        }


        /*
            Once streaming.mode.application.fails_all is set true,
            Any job fails will result the others not be executed.
         */
        //        val failsAll = params.getBooleanParam("streaming.mode.application.fails_all", false)
        //        StrategyDispatcher.throwsException = failsAll

        val jobCounter = new AtomicInteger(0)

        TryTool.tryLogNonFatalError {
            lifeCycleCallback.foreach(f => f.beforeDispatcher(runtime, params.getParamsMap.asScala.toMap))
        }


        //        jobs.foreach {
        //            jobName =>
        //                /*
        //                todo: We should check if it runs on Yarn, it true, then
        //                      convert the exception to Yarn exception otherwise the
        //                      Yarn will show the status success even there are exceptions thrown
        //                 */
        //                dispatcher.dispatch(Dispatcher.contextParams(jobName))
        //                val index = jobCounter.get()
        //
        //                listeners.foreach { listener =>
        //                    listener.processEvent(JobFlowGenerate(jobName, index, dispatcher.findStrategies(jobName).get.head))
        //                }
        //                jobCounter.incrementAndGet()
        //        }

        TryTool.tryLogNonFatalError {
            lifeCycleCallback.foreach(f => f.afterDispatcher(runtime, params.getParamsMap.asScala.toMap))
        }


        if (params.getBooleanParam("streaming.unitest.startRuntime", true)) {
            runtime.startRuntime
        }
        PlatformManager.RUNTIME_IS_READY.compareAndSet(false, true)
        if (params.getBooleanParam("streaming.unitest.awaitTermination", true)) {
            runtime.awaitTermination
        }
    }
    PlatformManager.setLastInstantiatedContext(self)
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

    def getRuntime: StreamingRuntime = {
        val params: JMap[String, String] = getOrCreate.config.get().getParamsMap
        val tempParams: JMap[Any, Any] = new util.HashMap[Any, Any]()
        params.asScala.map(f => tempParams.put(f._1.asInstanceOf[Any], f._2.asInstanceOf[Any]))

        val platformName = params.getOrDefault("streaming.platform", SPARK)
        val runtime = createRuntimeByPlatform(platformNameMapping(platformName), tempParams)

        runtime
    }

    def SPAKR_STREAMING = "spark_streaming"

    def SPAKR_STRUCTURED_STREAMING = "spark_structured_streaming"

    def SPAKR_S_S = "ss"

    def SPARK = "spark"

    def FLINK_STREAMING = "flink_streaming"

    val RUNTIME_PREFIX = "com.zhy.yisql.core.platform.runtime."

    def platformNameMapping = Map[String, String](
        SPAKR_S_S -> s"${RUNTIME_PREFIX}SparkStructuredStreamingRuntime",
        SPAKR_STRUCTURED_STREAMING -> s"${RUNTIME_PREFIX}SparkStructuredStreamingRuntime",
        FLINK_STREAMING -> s"${RUNTIME_PREFIX}FlinkStreamingRuntime",
        SPAKR_STREAMING -> s"${RUNTIME_PREFIX}SparkStreamingRuntime",
        SPARK -> s"${RUNTIME_PREFIX}SparkRuntime"
    )
}
