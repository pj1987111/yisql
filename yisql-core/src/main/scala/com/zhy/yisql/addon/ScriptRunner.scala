package com.zhy.yisql.addon

import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.execute.{ExecuteContext, SQLExecuteContext}
import com.zhy.yisql.core.job.{JobManager, JobType, SQLJobInfo}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.concurrent.{Callable, ExecutorService, Executors, Future}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-14
 *  \* Time: 16:43
 *  \* Description: 
 *  \ */
object ScriptRunner {
  private val executors: ExecutorService = Executors.newFixedThreadPool(10)

  def runSubJobAsync(code: String, fetchResult: (DataFrame) => Unit, spark: Option[SparkSession], reuseContext: Boolean, reuseExecListenerEnv: Boolean): Future[Option[DataFrame]] = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val finalSpark: SparkSession = spark.getOrElse(context.execListener.sparkSession)
    val jobInfo: SQLJobInfo = JobManager.getJobInfo(context.owner, JobType.SCRIPT, context.groupId, code, -1l)
    jobInfo.copy(jobName = jobInfo.jobName + ":" + jobInfo.groupId)
    val future: Future[Option[DataFrame]] = executors.submit(new Callable[Option[DataFrame]] {
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
                   reuseExecListenerEnv: Boolean): Unit = {

    JobManager.run(spark, jobInfo, () => {

      val newContext: ExecuteContext = if (!reuseContext) {
        val ssel: ScriptSQLExecListener = context.execListener.clone(spark)
        val newContext: ExecuteContext = ExecuteContext(ssel, context.owner, jobInfo.groupId, context.userDefinedParam)
        SQLExecuteContext.setContext(newContext)
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
      SQLExecuteContext.parse(code, newContext.execListener)
      context.execListener.getLastSelectTable() match {
        case Some(tableName) =>
          if (spark.catalog.tableExists(tableName)) {
            val df: DataFrame = spark.table(tableName)
            fetchResult(df)
            Option(df)
          }
          else None
        case None => None
      }
    })
  }

  def rubSubJob(code: String, fetchResult: (DataFrame) => Unit, spark: Option[SparkSession], reuseContext: Boolean, reuseExecListenerEnv: Boolean): Option[DataFrame] = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    val finalSpark: SparkSession = spark.getOrElse(context.execListener.sparkSession)

    val jobInfo: SQLJobInfo = JobManager.getJobInfo(context.owner, JobType.SCRIPT, context.groupId, code, -1l)
    jobInfo.copy(jobName = jobInfo.jobName + ":" + jobInfo.groupId)
    _run(code, context, jobInfo, finalSpark, fetchResult, reuseContext, reuseExecListenerEnv)
    context.execListener.getLastSelectTable() match {
      case Some(tableName) =>
        if (finalSpark.catalog.tableExists(tableName))
          if (finalSpark.catalog.tableExists(tableName)) {
            val df: DataFrame = finalSpark.table(tableName)
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
   * val jobInfo = JobManager.getJobInfo(context.owner, JobType.SCRIPT, "", code, timeout)
   * ScriptRunner.runJob(code, jobInfo, (df) => {
   *
   * })
   *
   */
  def runJob(code: String, jobInfo: SQLJobInfo, fetchResult: (DataFrame) => Unit): Unit = {
    val context: ExecuteContext = SQLExecuteContext.getContext()
    _run(code, context, jobInfo, context.execListener.sparkSession, fetchResult, reuseContext = true, reuseExecListenerEnv = true)

  }
}
