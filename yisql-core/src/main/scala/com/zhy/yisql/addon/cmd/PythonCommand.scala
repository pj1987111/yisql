package com.zhy.yisql.addon.cmd

import com.zhy.yisql.addon.cmd.python.PythonExecutor.batchExecute
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.{ExecuteContext, SQLExecuteContext}
import com.zhy.yisql.core.session.SetSession
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-28
 *  \* Time: 19:52
 *  \* Description: 
 *  \ */
class PythonCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    //user级别session
    val envSession = new SetSession(spark, context.owner)


    val command: Array[String] = JSONTool.parseJson[List[String]](params("parameters")).toArray

    val newdf: DataFrame = command match {

      case Array("env", kv) => // !python env a=b
        val Array(k, v) = kv.split("=", 2)
        envSession.set(k, v, Map(SetSession.__YISQL_CL__ -> SetSession.PYTHON_ENV_CL))
        envSession.fetchPythonEnv.get.toDF()

      case Array("conf", kv) => // !python conf a=b
        val Array(k, v) = kv.split("=", 2)
        envSession.set(k, v, Map(SetSession.__YISQL_CL__ -> SetSession.PYTHON_RUNNER_CONF_CL))
        envSession.fetchPythonRunnerConf.get.toDF()

      case Array("on", tableName, code) =>
        batchExecute(spark, code, tableName)

      case Array("on", tableName, code, "named", targetTable) =>
        val resDf: DataFrame = batchExecute(spark, code, tableName)
        resDf.createOrReplaceTempView(targetTable)
        resDf
    }
    newdf
  }

}
