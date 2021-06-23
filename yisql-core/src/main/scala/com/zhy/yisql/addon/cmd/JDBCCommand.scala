package com.zhy.yisql.addon.cmd

import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.ConnectMeta
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.sql.{Connection, PreparedStatement}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-10
 *  \* Time: 21:28
 *  \* Description: 
 *  \ */
class JDBCCommand extends SQLCmd {
  def executeInDriver(options: Map[String, String]): Unit = {
    val driver: String = options("driver")
    val url: String = options("url")
    Class.forName(driver)
    val connection: Connection = java.sql.DriverManager.getConnection(url, options("user"), options("password"))
    try {
      // 正则匹配-排序-执行
      val statements: Seq[PreparedStatement] = options.filter((f: (String, String)) => """driver-statement-[0-9]+""".r.findFirstMatchIn(f._1).nonEmpty).
        map((f: (String, String)) => (f._1.split("-").last.toInt, f._2)).toSeq.sortBy((f: (Int, String)) => f._1).map((f: (Int, String)) => f._2)
        .map { f: String =>
          logInfo(s"${getClass.getName} execute: $f")
          connection.prepareStatement(f)
        }

      statements.foreach { f: PreparedStatement =>
        f.execute()
        f.close()
      }
    } finally {
      if (connection != null)
        connection.close()
    }
  }

  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val commands: Array[String] = JSONTool.parseJson[List[String]](params("parameters")).toArray
    var fFormat = "jdbc"
    var _params: Map[String, String] = params
    commands match {
      case Array(format, "driver", driverName, "sql", sql) =>
        fFormat = format
        _params += ("driver" -> driverName)
        _params += ("driver-statement-0" -> sql)
      case Array("driver", driverName, "sql", sql) =>
        _params += ("driver" -> driverName)
        _params += ("driver-statement-0" -> sql)
      case Array("sql", sql) =>
        _params += ("driver-statement-0" -> sql)
    }

    ConnectMeta.presentThenCall(fFormat, (options: (String, Map[String, String])) => {
      options._2.foreach { item: (String, String) =>
        _params += (item._1 -> item._2)
      }
    })
    executeInDriver(_params)
    emptyDataFrame(spark)
  }
}
