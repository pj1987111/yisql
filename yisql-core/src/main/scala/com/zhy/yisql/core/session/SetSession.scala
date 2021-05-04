package com.zhy.yisql.core.session

import com.zhy.yisql.common.utils.hash.HashUtils
import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-02
  *  \* Time: 09:14
  *  \* Description: 
  *  \*/
class SetSession(spark: SparkSession, owner: String) {

  import spark.implicits._

  def envTableName = HashUtils.md5Hash(owner)

  private def isTheSame(oldItem: SetItem, newItem: SetItem) = {
    (newItem.k, newItem.config(SetSession.__YISQL_CL__)) == (oldItem.k, oldItem.config(SetSession.__YISQL_CL__))
  }

  def set(k: String, v: String, config: Map[String, String]) = {
    if (envTableExists) {
      val oldItems = spark.table(envTableName).as[SetItem].collect().toList
      val newItem = SetItem(k, v,
        Map(SetSession.__YISQL_CL__ -> SetSession.SET_STATEMENT_CL) ++ config
      )
      val newItems = oldItems.filterNot { oldItem =>
        isTheSame(oldItem, newItem)
      } ++ List(newItem)
      spark.createDataset[SetItem](newItems.toSeq).
          createOrReplaceTempView(envTableName)
    } else {
      spark.createDataset[SetItem](Seq(SetItem(k, v, config))).
          createOrReplaceTempView(envTableName)
    }
  }

  private[session] def envTableExists() = {
    spark.catalog.tableExists(envTableName)
  }

  def envTable = {
    if (envTableExists()) Option(spark.table(envTableName))
    else None
  }

  def filterEnvTable(f: (SetItem) => Boolean) = {
    if (envTableExists()) Option(spark.table(envTableName).as[SetItem].filter(item => f(item)))
    else None
  }

  def fetchPythonEnv = {
    filterEnvTable((item) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.PYTHON_ENV_CL
    })
  }

  def fetchPythonRunnerConf = {
    filterEnvTable((item) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.PYTHON_RUNNER_CONF_CL
    })
  }

  def fetchSetStatement = {
    filterEnvTable((item) => {
      item.config(SetSession.__YISQL_CL__) == SetSession.SET_STATEMENT_CL
    })
  }


  def clearAll = {
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
