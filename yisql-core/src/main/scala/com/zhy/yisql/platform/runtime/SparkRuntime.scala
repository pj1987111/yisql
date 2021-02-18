package com.zhy.yisql.platform.runtime

import java.lang.reflect.Modifier
import java.util.concurrent.atomic.AtomicReference
import java.util.{Map => JMap}

import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.common.utils.reflect.{ClassLoaderTool, ScalaReflect}
import com.zhy.yisql.platform.PlatformManager
import org.apache.log4j.LogManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.session.{SessionIdentifier, SessionManager}
import org.apache.spark.sql.{SQLContext, SparkSession}

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 22:20
  *  \* Description: 
  *  \*/
class SparkRuntime(_params: JMap[Any, Any]) extends StreamingRuntime with PlatformManagerListener with Logging {

    def name = "SPARK"

    var sparkSession: SparkSession = createRuntime

    var sessionManager = new SessionManager(sparkSession)
    sessionManager.start()

    override def params: JMap[Any, Any] = _params

    private val streamingParams = mutable.HashMap[String, String]()

    initUDF()
    SparkRuntime.setLastInstantiatedContext(this)

    def getSession(owner: String) = {
        sessionManager.getSession(SessionIdentifier(owner)).sparkSession
    }

    def getSQLSession(owner: String) = {
        sessionManager.getSession(SessionIdentifier(owner))
    }

    def createRuntime = {
        logInfo("create Runtime...")

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
        ).foreach(f =>
            streamingParams.put(f._1.toString, f._2.toString)
        )

        if (streamingParams.getOrDefault("streaming.enableHiveSupport", "false").equals("true")) {
            sparkSession.enableHiveSupport()
        }
        val ss = sparkSession.getOrCreate()
        ss
    }

    def initUDF() = {
        params.put("_session_", sparkSession)
//        registerUDF("streaming.core.compositor.spark.udf.Functions")
//        registerUDF("tech.mlsql.crawler.udf.Functions")
        registerUDF("com.zhy.yisql.udf.Functions")
        //        if (params.containsKey(MLSQLConf.MLSQL_UDF_CLZZNAMES.key)) {
        //            MLSQLConf.MLSQL_UDF_CLZZNAMES.readFrom(configReader).get.split(",").foreach { clzz =>
        //                registerUDF(clzz)
        //            }
        //        }
        //        MLSQLStreamManager.start(sparkSession)
        createTables
    }

    def createTables = {
        sparkSession.sql("select 1 as a").createOrReplaceTempView("command")
    }

    def registerUDF(clzz: String) = {
        logInfo("register functions.....")
        ClassLoaderTool.classForName(clzz).getMethods.foreach { f =>
            try {
                if (Modifier.isStatic(f.getModifiers)) {
                    f.invoke(null, sparkSession.udf)
                }
            } catch {
                case e: Exception =>
                    e.printStackTrace()
            }
        }
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

    override def awaitTermination: Unit = {
        Thread.currentThread().join()
    }

    override def startThriftServer: Unit = {
        val (clzz, instance) = ScalaReflect.findObjectMethod("org.apache.spark.sql.hive.thriftserver.HiveThriftServer2")
        val method = clzz.getMethod("startWithContext", classOf[SQLContext])
        method.invoke(instance, sparkSession.sqlContext)
    }

    override def startHttpServer: Unit = {
        val restClass = Class.forName("com.zhy.yisql.rest.Application")
        val method = restClass.getMethod("main", classOf[Array[String]])
        method.invoke(null, Array(""))
    }

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
