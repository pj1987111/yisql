package com.zhy.yisql.platform

import java.util.concurrent.atomic.AtomicReference
import java.util.{Map => JMap}

import org.apache.log4j.LogManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._
import scala.collection.mutable
/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 22:20
  *  \* Description: 
  *  \*/
class SparkRuntime(_params: JMap[Any, Any]) extends StreamingRuntime with PlatformManagerListener  {
    @transient private lazy val logger = LogManager.getLogger(classOf[SparkRuntime])

    def name = "SPARK"
    var sparkSession: SparkSession = createRuntime

    override def params: JMap[Any, Any] = _params
    private val streamingParams = mutable.HashMap[String, String]()

    //    def getSession(owner: String) = {
    //        sessionManager.getSession(SessionIdentifier(owner)).sparkSession
    //    }

    def createRuntime = {
        logger.info("create Runtime...")

        val conf = new SparkConf()
        params.filter(f =>
            f._1.toString.startsWith("spark.") ||
                    f._1.toString.startsWith("hive.")
        ).foreach { f =>
            conf.set(f._1.toString, f._2.toString)
        }

        val sparkSession = SparkSession.builder().config(conf)


        params.filter(f =>
            f._1.toString.startsWith("streaming.")
        ).foreach( f=>
            streamingParams.put(f._1.toString, f._2.toString)
        )

        if (streamingParams.getOrDefault("streaming.enableHiveSupport", "false").equals("true")) {
            sparkSession.enableHiveSupport()
        }
        val ss = sparkSession.getOrCreate()
        ss
    }

    override def startRuntime: StreamingRuntime = {
        this
    }
    override def destroyRuntime(stopGraceful: Boolean, stopContext: Boolean): Boolean = {
        sparkSession.stop()
        SparkRuntime.clearLastInstantiatedContext()
        true
    }

    override def streamingRuntimeInfo: StreamingRuntimeInfo = null

    override def configureStreamingRuntimeInfo(streamingRuntimeInfo: StreamingRuntimeInfo): Unit = {}

    override def awaitTermination: Unit = {}

    override def startThriftServer: Unit = {}

    override def startHttpServer: Unit = {}

    override def processEvent(event: Event): Unit = {}
}

object SparkRuntime {

    private val INSTANTIATION_LOCK = new Object()

    /**
      * Reference to the last created SQLContext.
      */
    @transient private val lastInstantiatedContext = new AtomicReference[SparkRuntime]()

    /**
      * Get the singleton SQLContext if it exists or create a new one using the given SparkContext.
      * This function can be used to create a singleton SQLContext object that can be shared across
      * the JVM.
      */
    def getOrCreate(params: JMap[Any, Any]): SparkRuntime = {
        INSTANTIATION_LOCK.synchronized {
            if (lastInstantiatedContext.get() == null) {
                new SparkRuntime(params)
            }
        }
        PlatformManager.getOrCreate.register(lastInstantiatedContext.get())
        lastInstantiatedContext.get()
    }

    private[platform] def clearLastInstantiatedContext(): Unit = {
        INSTANTIATION_LOCK.synchronized {
            PlatformManager.getOrCreate.unRegister(lastInstantiatedContext.get())
            lastInstantiatedContext.set(null)
        }
    }

    private[platform] def setLastInstantiatedContext(sparkRuntime: SparkRuntime): Unit = {
        INSTANTIATION_LOCK.synchronized {
            lastInstantiatedContext.set(sparkRuntime)
        }
    }
}
