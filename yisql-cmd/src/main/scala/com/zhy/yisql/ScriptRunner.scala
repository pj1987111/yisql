package com.zhy.yisql

import java.util.concurrent.{Callable, Executors}

import com.zhy.yisql.runner.{ExecuteContext, JobManager, SQLJobInfo, SQLJobType, ScriptSQLExec}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 16:43
  *  \* Description: 
  *  \*/
object ScriptRunner {
    private val executors = Executors.newFixedThreadPool(10)

    def runSubJobAsync(code: String, fetchResult: (DataFrame) => Unit, spark: Option[SparkSession], reuseContext: Boolean, reuseExecListenerEnv: Boolean) = {
        val context = ScriptSQLExec.getContext()
        val finalSpark = spark.getOrElse(context.execListener.sparkSession)
        val jobInfo = JobManager.getJobInfo(context.owner, SQLJobType.SCRIPT, context.groupId, code, -1l)
        jobInfo.copy(jobName = jobInfo.jobName + ":" + jobInfo.groupId)
        val future = executors.submit(new Callable[Option[DataFrame]] {
            override def call(): Option[DataFrame] = {
                _run(code, context, jobInfo, finalSpark, fetchResult, reuseContext, reuseExecListenerEnv)
                context.execListener.getLastSelectTable() match {
                    case Some(tableName) => Option(finalSpark.table(tableName))
                    case None => None
                }
            }
        })
        future
    }

    private def _run(code: String,
                     context: ExecuteContext,
                     jobInfo: SQLJobInfo,
                     spark: SparkSession,
                     fetchResult: (DataFrame) => Unit,
                     reuseContext: Boolean,
                     reuseExecListenerEnv: Boolean) = {

        JobManager.run(spark, jobInfo,() => {

            val newContext = if (!reuseContext) {
                val ssel = context.execListener.clone(spark)
                val newContext = ExecuteContext(ssel, context.owner, jobInfo.groupId, context.userDefinedParam)
                ScriptSQLExec.setContext(newContext)
                if (!reuseExecListenerEnv) {
                    newContext.execListener.env().clear()
                }
                List("SKIP_AUTH", "HOME", "OWNER").foreach { item =>
                    newContext.execListener.env().put(item, context.execListener.env()(item))
                }
                newContext
            } else context
            val skipAuth = newContext.execListener.env().getOrElse("SKIP_AUTH", "false").toBoolean
            val skipPhysical = newContext.execListener.env().getOrElse("SKIP_PHYSICAL", "false").toBoolean
            ScriptSQLExec.parse(code, newContext.execListener)
            context.execListener.getLastSelectTable() match {
                case Some(tableName) =>
                    if (spark.catalog.tableExists(tableName)) {
                        val df = spark.table(tableName)
                        fetchResult(df)
                        Option(df)
                    }
                    else None
                case None => None
            }
        })
    }

    def rubSubJob(code: String, fetchResult: (DataFrame) => Unit, spark: Option[SparkSession], reuseContext: Boolean, reuseExecListenerEnv: Boolean) = {
        val context = ScriptSQLExec.getContext()
        val finalSpark = spark.getOrElse(context.execListener.sparkSession)

        val jobInfo = JobManager.getJobInfo(context.owner, SQLJobType.SCRIPT, context.groupId, code, -1l)
        jobInfo.copy(jobName = jobInfo.jobName + ":" + jobInfo.groupId)
        _run(code, context, jobInfo, finalSpark, fetchResult, reuseContext, reuseExecListenerEnv)
        context.execListener.getLastSelectTable() match {
            case Some(tableName) =>
                if (finalSpark.catalog.tableExists(tableName))
                    if (finalSpark.catalog.tableExists(tableName)) {
                        val df = finalSpark.table(tableName)
                        fetchResult(df)
                        Option(df)
                    }
                    else None
                else None
            case None => None
        }
    }

    /**
      * Example:
      *
      * val timeout = JobManager.getJobInfo.get(context.groupId).get.timeout
      * val code =
      * """
      * |
      * """.stripMargin
      * val jobInfo = JobManager.getJobInfo(context.owner, MLSQLJobType.SCRIPT, "", code, timeout)
      *         ScriptRunner.runJob(code, jobInfo, (df) => {
      *
      * })
      *
      */
    def runJob(code: String, jobInfo: SQLJobInfo, fetchResult: (DataFrame) => Unit) = {
        val context = ScriptSQLExec.getContext()
        _run(code, context, jobInfo, context.execListener.sparkSession, fetchResult, true, true)

    }
}
