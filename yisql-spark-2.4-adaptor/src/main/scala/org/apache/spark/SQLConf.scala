package org.apache.spark

import org.apache.spark.internal.config.{ConfigBuilder, ConfigEntry, ConfigProvider, ConfigReader, OptionalConfigEntry}

import java.util
import java.util.concurrent.TimeUnit
import java.util.{HashMap, Map => JMap}
import scala.collection.JavaConverters._

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-18
 *  \* Time: 21:07
 *  \* Description: 
 *  \ */
object SQLConf {
  private[this] val sqlConfEntries = new util.HashMap[String, ConfigEntry[_]]()

  def register(entry: ConfigEntry[_]): Unit = {
    require(!sqlConfEntries.containsKey(entry.key),
      s"Duplicate SQLConfigEntry. ${entry.key} has been registered")
    sqlConfEntries.put(entry.key, entry)
  }

  def entries: util.HashMap[String, ConfigEntry[_]] = sqlConfEntries

  private[this] object SQLConfigBuilder {
    def apply(key: String): ConfigBuilder = ConfigBuilder(key).onCreate(register)
  }

  val SQL_PLATFORM: OptionalConfigEntry[String] = SQLConfigBuilder("streaming.platform")
    .doc("for now only supports spark,spark_streaming,flink").stringConf.checkValues(Set("spark", "spark_streaming", "flink")).createOptional

  val SQL_ENABLE_REST: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.rest").doc(
    """
      |enable rest api
        """.stripMargin).booleanConf.createWithDefault(false)

  val SQL_DRIVER_PORT: ConfigEntry[Int] = SQLConfigBuilder("streaming.driver.port").doc(
    """
      | The port of rest api
        """.stripMargin).intConf.createWithDefault(9003)


  val SQL_MASTER: OptionalConfigEntry[String] = SQLConfigBuilder("streaming.master")
    .doc("the same with spark master").stringConf.createOptional

  val SQL_NAME: ConfigEntry[String] = SQLConfigBuilder("streaming.name")
    .doc("The name will showed in yarn cluster and spark ui").stringConf.createWithDefault("yisql")

  val SQL_BIGDL_ENABLE: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.bigdl.enable")
    .doc(
      """
        |Enable embedded deep learning support.
        |if set true, then we will automatically add spark conf like following:
        |
        |  conf.setIfMissing("spark.shuffle.reduceLocality.enabled", "false")
        |  conf.setIfMissing("spark.shuffle.blockTransferService", "nio")
        |  conf.setIfMissing("spark.scheduler.minRegisteredResourcesRatio", "1.0")
        |  conf.setIfMissing("spark.speculation", "false")
                """.stripMargin).booleanConf.createWithDefault(false)

  val SQL_CLUSTER_PS_ENABLE: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.ps.cluster.enable").doc(
    """
      |supports directly communicating with executor if you set this true.
        """.stripMargin).booleanConf.createWithDefault(true)

  val SQL_CLUSTER_PS_DRIVER_PORT: ConfigEntry[Int] = SQLConfigBuilder("spark.ps.cluster.driver.port").doc(
    """
      |ps driver port
        """.stripMargin).intConf.createWithDefault(7777)

  val SQL_PS_ASK_TIMEOUT: ConfigEntry[Long] = SQLConfigBuilder("streaming.ps.ask.timeout").doc(
    s"""
       |control how long distributing resource/python env take then timeout. unit: seconds
     """.stripMargin).longConf.createWithDefault(3600)

  val SQL_PS_NETWORK_TIMEOUT: ConfigEntry[Long] = SQLConfigBuilder("streaming.ps.network.timeout").doc(
    s"""
       |set spark.network.timeout
     """.stripMargin).longConf.createWithDefault(60 * 60 * 8)

  val SQL_HIVE_CONNECTION: ConfigEntry[String] = SQLConfigBuilder("streaming.hive.javax.jdo.option.ConnectionURL").doc(
    """
      |Use this to configure `hive.javax.jdo.option.ConnectionURL`
        """.stripMargin).stringConf.createWithDefault("")

  val SQL_ENABLE_HIVE_SUPPORT: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.enableHiveSupport").doc(
    """
      |enable hive
        """.stripMargin).booleanConf.createWithDefault(false)

  val SQL_ENABLE_CARBONDATA_SUPPORT: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.enableCarbonDataSupport").doc(
    """
      |carbondata support. If set true, please configure `streaming.carbondata.store` and `streaming.carbondata.meta`
        """.stripMargin).booleanConf.createWithDefault(false)


  val SQL_CARBONDATA_STORE: OptionalConfigEntry[String] = SQLConfigBuilder("streaming.carbondata.store").doc(
    """
      |
    """.stripMargin).stringConf.createOptional

  val SQL_CARBONDATA_META: OptionalConfigEntry[String] = SQLConfigBuilder("streaming.carbondata.meta").doc(
    """
      |
    """.stripMargin).stringConf.createOptional

  val SQL_DEPLOY_REST_API: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.deploy.rest.api").doc(
    """
      |If you deploy as predict service, please enable this.
        """.stripMargin).booleanConf.createWithDefault(false)

  val SQL_SPARK_SERVICE: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.spark.service").doc(
    """
      |Run as service and without quit.
        """.stripMargin).booleanConf.createWithDefault(false)


  val SQL_DISABLE_SPARK_LOG: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.disableSparkLog").doc(
    """
      |Sometimes there are too much spark job info, you can disable them.
        """.stripMargin).booleanConf.createWithDefault(false)

  val SQL_UDF_CLZZNAMES: OptionalConfigEntry[String] = SQLConfigBuilder("streaming.udf.clzznames").doc(
    """
      |register udf class
        """.stripMargin).stringConf.createOptional

  val SESSION_IDLE_TIMEOUT: ConfigEntry[Long] =
    SQLConfigBuilder("spark.yisql.session.idle.timeout")
      .doc("SparkSession timeout")
      .timeConf(TimeUnit.MILLISECONDS)
      .createWithDefault(TimeUnit.MINUTES.toMillis(30L))

  val SESSION_CHECK_INTERVAL: ConfigEntry[Long] =
    SQLConfigBuilder("spark.yisql.session.check.interval")
      .doc("The check interval for backend session a.k.a SparkSession timeout")
      .timeConf(TimeUnit.MILLISECONDS)
      .createWithDefault(TimeUnit.MINUTES.toMillis(5L))

  val BIND_HOST: ConfigEntry[String] =
    SQLConfigBuilder("spark.yisql.bind.host")
      .doc("Bind host on which to run.")
      .stringConf
      .createWithDefault(SQLSparkConst.localHostName())

  val SESSION_WAIT_OTHER_TIMES: ConfigEntry[Int] =
    SQLConfigBuilder("spark.yisql.session.wait.other.times")
      .doc("How many times to check when another session with the same user is initializing " +
        "SparkContext. Total Time will be times by " +
        "`spark.yisql.session.wait.other.interval`")
      .intConf
      .createWithDefault(60)

  val SESSION_WAIT_OTHER_INTERVAL: ConfigEntry[Long] =
    SQLConfigBuilder("spark.yisql.session.wait.other.interval")
      .doc("The interval for checking whether other thread with the same user has completed" +
        " SparkContext instantiation.")
      .timeConf(TimeUnit.MILLISECONDS)
      .createWithDefault(TimeUnit.SECONDS.toMillis(1L))

  val SESSTION_INIT_TIMEOUT: ConfigEntry[Long] =
    SQLConfigBuilder("spark.yisql.session.init.timeout")
      .doc("How long we suggest the server to give up instantiating SparkContext")
      .timeConf(TimeUnit.SECONDS)
      .createWithDefault(TimeUnit.SECONDS.toSeconds(60L))

  val ENABLE_MAX_RESULT_SIZE: ConfigEntry[Boolean] =
    SQLConfigBuilder("spark.yisql.enable.max.result.limit")
      .doc("enable restful max result size limitation. when you enable this configuration." +
        " you should pass `maxResultSize` for your rest request." +
        " if not, you take only max 1000 record.")
      .booleanConf
      .createWithDefault(false)

  val RESTFUL_API_MAX_RESULT_SIZE: ConfigEntry[Long] =
    SQLConfigBuilder("spark.yisql.restful.api.max.result.size")
      .doc("the max size of restful api result.")
      .longConf
      .createWithDefault(1000)

  val SQL_LOG: ConfigEntry[Boolean] = SQLConfigBuilder("streaming.executor.log.in.driver")
    .doc("Executor send log msg to driver.")
    .booleanConf
    .createWithDefault(true)


  def getAllDefaults: Map[String, String] = {
    entries.entrySet().asScala.map { kv =>
      (kv.getKey, kv.getValue.defaultValueString)
    }.toMap
  }

  def createConfigReader(settings: JMap[String, String]): ConfigReader = {
    val reader = new ConfigReader(new SQLConfigProvider(settings))
    reader
  }
}

private[spark] class SQLConfigProvider(conf: JMap[String, String]) extends ConfigProvider {

  override def get(key: String): Option[String] = {
    if (key.startsWith("streaming.")) {
      Option(conf.get(key)).orElse(SparkConf.getDeprecatedConfig(key, conf))
    } else {
      None
    }
  }

}