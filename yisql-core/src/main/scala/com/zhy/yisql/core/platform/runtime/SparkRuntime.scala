package com.zhy.yisql.core.platform.runtime

import java.lang.reflect.Modifier
import java.net.URL
import java.util.concurrent.atomic.AtomicReference
import java.util.{Map => JMap}

import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.common.utils.reflect.{ClassLoaderTool, ScalaReflect}
import com.zhy.yisql.core.datasource.datalake.DataLake
import com.zhy.yisql.core.job.{JobManager, StreamManager}
import com.zhy.yisql.core.platform.PlatformManager
import org.apache.spark.sql.session.{SessionIdentifier, SessionManager}
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SQLConf, SparkConf}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-02
  *  \* Time: 22:20
  *  \* Description: 
  *  \*/
class SparkRuntime(_params: JMap[Any, Any]) extends StreamingRuntime with PlatformManagerListener with Logging {

    val configReader = SQLConf.createConfigReader(params.map(f => (f._1.toString, f._2.toString)))

    def name = "SPARK"

    var sparkSession: SparkSession = createRuntime

    var sessionManager = new SessionManager(sparkSession)
    sessionManager.start()

    override def params: JMap[Any, Any] = _params

    startHttpServer

    initUDF()
    StreamManager.start(sparkSession)

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
        if (SQLConf.SQL_MASTER.readFrom(configReader).isDefined) {
            conf.setMaster(SQLConf.SQL_MASTER.readFrom(configReader).get)
        }

        conf.setAppName(SQLConf.SQL_NAME.readFrom(configReader))

        if (params.containsKey(DataLake.STREAMING_DL_PATH)) {
            conf.set(DataLake.SPARK_DL_PATH, params.get(DataLake.STREAMING_DL_PATH).toString)
        }

//        registerLifeCyleCallback("tech.mlsql.runtime.MetaStoreService")
//        lifeCyleCallback.foreach { callback =>
//            callback.beforeRuntimeStarted(params.map(f => (f._1.toString, f._2.toString)).toMap, conf)
//        }

        val sparkSession = SparkSession.builder().config(conf)

        def setHiveConnectionURL = {
            val url = SQLConf.SQL_HIVE_CONNECTION.readFrom(configReader)
            if (!url.isEmpty) {
                logInfo("set hive javax.jdo.option.ConnectionURL=" + url)
                sparkSession.config("javax.jdo.option.ConnectionURL", url)
            }
        }

        if (SQLConf.SQL_ENABLE_HIVE_SUPPORT.readFrom(configReader)) {
            setHiveConnectionURL
            sparkSession.enableHiveSupport()
        }

        val ss = sparkSession.getOrCreate()
        ss

//        lifeCyleCallback.foreach { callback =>
//            callback.afterRuntimeStarted(params.map(f => (f._1.toString, f._2.toString)).toMap, conf, ss)
//        }

        if (SQLConf.SQL_SPARK_SERVICE.readFrom(configReader)) {
            JobManager.init(ss)
        }

        show(params.asScala.map(kv => (kv._1.toString, kv._2.toString)).toMap)
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
        if (SQLConf.SQL_SPARK_SERVICE.readFrom(configReader)) {
            Thread.currentThread().join()
        }
    }

    override def startThriftServer: Unit = {
        val (clzz, instance) = ScalaReflect.findObjectMethod("org.apache.spark.sql.hive.thriftserver.HiveThriftServer2")
        val method = clzz.getMethod("startWithContext", classOf[SQLContext])
        method.invoke(instance, sparkSession.sqlContext)
    }

    override def startHttpServer: Unit = {
//        val httpServerPort = SQLConf.SQL_DRIVER_PORT.readFrom(configReader)
//        Application.main(Array(s"--server.port=$httpServerPort"))
        val restClass = Class.forName("com.zhy.yisql.rest.Application")
        val method = restClass.getMethod("main", classOf[Array[String]])
        val httpServerPort = SQLConf.SQL_DRIVER_PORT.readFrom(configReader)
        method.invoke(null, Array(s"--server.port=$httpServerPort"))

        import java.lang.reflect.Field
        val factoryField = classOf[URL].getDeclaredField("factory")
//        val factoryField = URL.class.getDeclaredField("factory")
        factoryField.setAccessible(true)
        factoryField.set(null, null)
    }

    override def processEvent(event: Event): Unit = {}

    private def show(conf: Map[String, String]) {
        val keyLength = conf.keys.map(_.size).max
        val valueLength = conf.values.map(_.size).max
        val header = "-" * (keyLength + valueLength + 3)
        logInfo("yisql server start with configuration!")
        logInfo(header)
        conf.map {
            case (key, value) =>
                val keyStr = key + (" " * (keyLength - key.size))
                val valueStr = value + (" " * (valueLength - value.size))
                s"|${keyStr}|${valueStr}|"
        }.foreach(line => {
            logInfo(line)
        })
        logInfo(header)
    }
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
