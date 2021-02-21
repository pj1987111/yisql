package com.zhy.yisql.core.datasource

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.DataFrame
import tech.mlsql.common.utils.reflect.ClassPath

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-04
  *  \* Time: 19:27
  *  \* Description: 注册数据交换reader和writer
  *  \*/
object DataSourceRegistry extends Logging {

    private val registry = new java.util.concurrent.ConcurrentHashMap[String, DataSource]()

    def register(name: String, obj: DataSource) = {
        registry.put(name, obj)
    }

    def unRegister(name: String) = {
        registry.remove(name)
    }


    def allSourceNames = {
        registry.asScala.map(f => f._2.shortFormat).toSeq
    }

    def fetch(name: String, option: Map[String, String] = Map()): Option[DataSource] = {
        if (registry.containsKey(name)) {
            Option(registry.get(name))
        } else None
    }

    def findAllNames(name: String): Option[Seq[String]] = {
        registry.asScala.find(f => f.equals(name)) match {
            case Some(item) => Option(Seq(item._2.shortFormat, item._2.fullFormat))
            case None => None
        }
    }

    private def registerFromPackage(name: String) = {
        ClassPath.from(getClass.getClassLoader).getTopLevelClasses(name).asScala.foreach { clzz =>
            val dataSource = Class.forName(clzz.getName).newInstance()
            if (dataSource.isInstanceOf[Registry]) {
                dataSource.asInstanceOf[Registry].register()
            } else {
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

case class DataSinkConfig(path: String, config: Map[String, String], mode: String, df: Option[DataFrame] = None) {
    def cloneWithNewMode(newMode: String): DataSinkConfig = {
        DataSinkConfig(path, config, newMode, df)
    }
}
