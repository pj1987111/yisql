package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.ConnectMeta
import net.sf.json.JSONObject
import org.apache.spark.sql.{DataFrame, SparkSession}
import tech.mlsql.common.utils.serder.json.JSONTool

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-10
  *  \* Time: 21:28
  *  \* Description: 
  *  \*/
class JDBCCommand extends SQLCmd {
  def executeInDriver(options: Map[String, String]) = {
    val driver = options("driver")
    val url = options("url")
    Class.forName(driver)
    val connection = java.sql.DriverManager.getConnection(url, options("user"), options("password"))
    try {
      // 正则匹配-排序-执行
      val statements = options.filter(f =>"""driver\-statement\-[0-9]+""".r.findFirstMatchIn(f._1).nonEmpty).
          map(f => (f._1.split("-").last.toInt, f._2)).toSeq.sortBy(f => f._1).map(f => f._2).map { f =>
        logInfo(s"${getClass.getName} execute: ${f}")
        connection.prepareStatement(f)
      }

      statements.foreach { f =>
        f.execute()
        f.close()
      }
    } finally {
      if (connection != null)
        connection.close()
    }
  }

  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val commands = JSONTool.parseJson[List[String]](params("parameters")).toArray
    var fFormat = "jdbc"
    var _params = params
    commands match {
      case Array(format, "driver", driverName, "sql", sql) =>
        fFormat = format
        _params += ("driver" -> driverName)
        _params += ("driver-statement-0"-> sql)
      case Array("driver", driverName, "sql", sql) =>
        _params += ("driver" -> driverName)
        _params += ("driver-statement-0"-> sql)
      case Array("sql", sql) =>
        _params += ("driver-statement-0"-> sql)
    }

    ConnectMeta.presentThenCall(fFormat, options => {
      options._2.foreach { item =>
        _params += (item._1 -> item._2)
      }
    })
    executeInDriver(_params)
    emptyDataFrame(spark)
  }
}
