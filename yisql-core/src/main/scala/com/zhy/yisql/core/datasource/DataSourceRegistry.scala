package com.zhy.yisql.core.datasource

import com.google.common.reflect.ClassPath
import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.JavaConverters._

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-04
 *  \* Time: 19:27
 *  \* Description: 注册数据交换reader和writer
 *  \ */
object DataSourceRegistry extends Logging {

  private val registry = new java.util.concurrent.ConcurrentHashMap[String, DataSource]()

  def register(name: String, obj: DataSource): DataSource = {
    registry.put(name, obj)
  }

  def unRegister(name: String): DataSource = {
    registry.remove(name)
  }


  def allSourceNames: Seq[String] = {
    registry.asScala.map(f => f._2.shortFormat).toSeq
  }

  def fetch(name: String, option: Map[String, String] = Map()): Option[DataSource] = {
    if (registry.containsKey(name)) {
      Option(registry.get(name))
    } else None
  }

  def findAllNames(name: String): Option[Seq[String]] = {
    registry.asScala.find(f => f._1.equals(name)) match {
      case Some(item) => Option(Seq(item._2.shortFormat, item._2.fullFormat))
      case None => None
    }
  }

  private def registerFromPackage(name: String): Unit = {
    ClassPath.from(getClass.getClassLoader).getTopLevelClasses(name).asScala.foreach { clzz =>
      val dataSource: Any = Class.forName(clzz.getName).newInstance()
      dataSource match {
        case registry: Registry =>
          registry.register()
        case _ =>
          logWarning(
            s"""
               |${clzz.getName} does not implement YiSQLRegistry,
               |we cannot register it automatically.
                         """.stripMargin)
      }
    }
  }

  registerFromPackage("com.zhy.yisql.core.datasource.impl")
}

trait Registry {
  def register(): Unit

  def unRegister(): Unit = {}
}


case class DataSourceConfig(path: String, config: Map[String, String], df: Option[DataFrame] = None)

/**
 *
 * @param path    存储路径
 * @param config  写入参数
 * @param mode    写入模式
 * @param df
 * @param jobName 任务名，用于流任务注册
 */
case class DataSinkConfig(path: String, config: Map[String, String], mode: String, df: Option[DataFrame] = None, jobName: Option[String], spark: SparkSession) {
  def cloneWithNewMode(newMode: String): DataSinkConfig = {
    DataSinkConfig(path, config, newMode, df, jobName, spark)
  }
}
