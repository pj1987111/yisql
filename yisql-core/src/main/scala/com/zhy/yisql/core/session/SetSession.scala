package com.zhy.yisql.core.session

import com.zhy.yisql.common.utils.hash.HashUtils
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-02
  *  \* Time: 09:14
  *  \* Description: 
  *  \*/
class SetSession(spark: SparkSession, owner: String) {

  import spark.implicits._

  def envTableName: String = HashUtils.md5Hash(owner)

  private def isTheSame(oldItem: SetItem, newItem: SetItem): Boolean = {
    (newItem.k, newItem.config(SetSession.__YISQL_CL__)) == (oldItem.k, oldItem.config(SetSession.__YISQL_CL__))
  }

  def set(k: String, v: String, config: Map[String, String]): Unit = {
    if (envTableExists()) {
      val oldItems: Seq[SetItem] = spark.table(envTableName).as[SetItem].collect().toList
      val newItem: SetItem = SetItem(k, v,
        Map(SetSession.__YISQL_CL__ -> SetSession.SET_STATEMENT_CL) ++ config
      )
      val newItems: Seq[SetItem] = oldItems.filterNot { oldItem: SetItem =>
        isTheSame(oldItem, newItem)
      } ++ List(newItem)
      spark.createDataset[SetItem](newItems).
          createOrReplaceTempView(envTableName)
    } else {
      spark.createDataset[SetItem](Seq(SetItem(k, v, config))).
          createOrReplaceTempView(envTableName)
    }
  }

  private[session] def envTableExists(): Boolean = {
    spark.catalog.tableExists(envTableName)
  }

  def envTable: Option[DataFrame] = {
    if (envTableExists()) Option(spark.table(envTableName))
    else None
  }

  def filterEnvTable(f: (SetItem) => Boolean): Option[Dataset[SetItem]] = {
    if (envTableExists()) Option(spark.table(envTableName).as[SetItem].filter((item: SetItem) => f(item)))
    else None
  }

  def fetchPythonEnv: Option[Dataset[SetItem]] = {
    filterEnvTable((item: SetItem) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.PYTHON_ENV_CL
    })
  }

  def fetchPythonRunnerConf: Option[Dataset[SetItem]] = {
    filterEnvTable((item: SetItem) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.PYTHON_RUNNER_CONF_CL
    })
  }

  def fetchSetStatement: Option[Dataset[SetItem]] = {
    filterEnvTable((item: SetItem) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.SET_STATEMENT_CL
    })
  }


  def clearAll(): Unit = {
    envTable match {
      case Some(_) =>
        spark.createDataset[SetItem](Seq()).createOrReplaceTempView(envTableName)
      case None =>
    }
  }

}

object SetSession {
  val PYTHON_ENV_CL = "python_env_cl"
  val PYTHON_RUNNER_CONF_CL = "python_runner_conf_cl"
  val SET_STATEMENT_CL = "set_statement_cl"
  val __YISQL_CL__ = "__yisql_cl__"
}

case class SetItem(k: String, v: String, config: Map[String, String])