package com.zhy.yisql.core.job

import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.job.listener.JobListener
import com.zhy.yisql.core.job.listener.JobListener.{JobFinishedEvent, JobStartedEvent}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.session.{SessionIdentifier, SparkSessionCacheManager}

import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, Executors, TimeUnit}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 17:23
  *  \* Description:
  * 任务执行 
  *  \*/
object JobManager extends Logging {
    private[this] var _jobManager: JobManager = _
    private[this] val _executor = Executors.newFixedThreadPool(100)
    private[this] val _jobListeners = ArrayBuffer[JobListener]()

    def addJobListener(listener: JobListener) = {
        _jobListeners += listener
    }

    def removeJobListener(listener: JobListener) = {
        _jobListeners -= listener
    }

    def shutdown = {
        logInfo(s"JobManager is shutdown....")
        _executor.shutdownNow()
        _jobManager.shutdown
        _jobManager = null
        _jobListeners.clear()
    }

    def init(spark: SparkSession, initialDelay: Long = 30, checkTimeInterval: Long = 5) = {
        synchronized {
            if (_jobManager == null) {
                logInfo(s"JobManager started with initialDelay=${initialDelay} checkTimeInterval=${checkTimeInterval}")
                _jobManager = new JobManager(spark, initialDelay, checkTimeInterval)
                //                _jobListeners += new CleanCacheListener
                //                _jobListeners += new EngineMDCLogListener
                _jobManager.run
            }
        }
    }

    def run(session: SparkSession, job: SQLJobInfo, f: () => Unit): Unit = {

        val context = SQLExecuteContext.getContext()
        //添加任务进度listener
        context.execListener.addJobProgressListener(new DefaultSQLJobProgressListener())

        try {
            _jobListeners.foreach { f => f.onJobStarted(new JobStartedEvent(job.groupId)) }
            if (_jobManager == null) {
                f()
            } else {
                session.sparkContext.setJobGroup(job.groupId, job.jobName, true)
                addJobManually(job)
                f()
            }

        } finally {
            handleJobDone(job.groupId)
            session.sparkContext.clearJobGroup()
            _jobListeners.foreach { f => f.onJobFinished(new JobFinishedEvent(job.groupId)) }
        }
    }

    def asyncRun(session: SparkSession, job: SQLJobInfo, f: () => Unit) = {
        // TODO: (fchen) 改成callback
        val context = SQLExecuteContext.getContext()
        _executor.execute(new Runnable {
            override def run(): Unit = {
                SQLExecuteContext.setContext(context)
                try {
                    JobManager.run(session, job, f)
                    context.execListener.addEnv("__MarkAsyncRunFinish__", "true")
                } catch {
                    case e: Exception =>
                        logInfo("Async Job Exception", e)
                } finally {
                    //                RequestCleanerManager.call()
                    context.execListener.env.remove("__MarkAsyncRunFinish__")
                    SQLExecuteContext.unset
                    SparkSession.clearActiveSession()
                }

            }
        })
    }

    def getJobInfo(owner: String,
                   jobType: String,
                   jobName: String,
                   jobContent: String,
                   timeout: Long): SQLJobInfo = {
        val startTime = System.currentTimeMillis()
        val groupId = _jobManager.nextGroupId
        SQLJobInfo(owner, jobType, jobName, jobContent, groupId, SQLJobProgress(), startTime, timeout)
    }

    def getJobInfo: Map[String, SQLJobInfo] =
        _jobManager.groupIdJobInfo.asScala.toMap

    def addJobManually(job: SQLJobInfo) = {
        _jobManager.groupIdJobInfo.put(job.groupId, job)
    }

    def removeJobManually(groupId: String) = {
        handleJobDone(groupId)
    }

    def killJob(session: SparkSession, groupId: String): Unit = {
        _jobManager.cancelJobGroup(session, groupId)
    }

    private def handleJobDone(groupId: String): Unit = {
        _jobManager.groupIdJobInfo.remove(groupId)
    }
}

class JobManager(_spark: SparkSession, initialDelay: Long, checkTimeInterval: Long) extends Logging {
    val groupIdJobInfo = new ConcurrentHashMap[String, SQLJobInfo]()

    def nextGroupId = UUID.randomUUID().toString

    val executor = Executors.newSingleThreadScheduledExecutor()

    def run = {
        executor.scheduleWithFixedDelay(new Runnable {
            override def run(): Unit = {
                groupIdJobInfo.foreach { f =>
                    try {
                        val elapseTime = System.currentTimeMillis() - f._2.startTime
                        if (f._2.timeout > 0 && elapseTime >= f._2.timeout) {

                            // At rest controller, we will clone the session,and this clone session is not
                            // saved in  SparkSessionCacheManager. But this do no harm to this scheduler,
                            // since cancel job depends `groupId` and sparkContext. The exception is stream job (which is connected with spark session),
                            // however, the stream job will not use `clone spark session`
                            val tempSession = SparkSessionCacheManager.getSessionManagerOption match {
                                case Some(sessionManager) =>
                                    sessionManager.getSessionOption(SessionIdentifier(f._2.owner))
                                case None => None
                            }
                            val session = tempSession.map(f => f.sparkSession).getOrElse(_spark)
                            cancelJobGroup(session, f._1, true)
                        }
                    } catch {
                        case e: Exception => logError(s"Kill job ${f._1} fails", e)
                    }
                }
            }
        }, initialDelay, checkTimeInterval, TimeUnit.SECONDS)
    }

    def cancelJobGroup(spark: SparkSession, groupId: String, ignoreStreamJob: Boolean = false): Unit = {
        logInfo("JobManager Timer cancel job group " + groupId)
        val job = groupIdJobInfo.get(groupId)

        def killStreamJob = {
            spark.streams.active.filter(f => f.id.toString == job.groupId).map(f => f.runId.toString).headOption match {
                case Some(_) =>
                    logInfo(s"Try to kill stream job: ${job.groupId}, name:${job.jobName} ")
                    spark.streams.get(job.groupId).stop()
                case None => logWarning(s"the stream job: ${job.groupId}, name:${job.jobName} is not in spark.streams.")
            }
        }

        def killBatchJob = {
            spark.sparkContext.cancelJobGroup(groupId)
            groupIdJobInfo.remove(groupId)
        }

        if (job != null && !ignoreStreamJob && job.jobType == JobType.STREAM) {
            killStreamJob
        }

        if (job.jobType != JobType.STREAM) {
            killBatchJob
        }
    }

    def shutdown = {
        executor.shutdownNow()
    }
}

case object JobType {
    val SCRIPT = "script"
    val SQL = "sql"
    val STREAM = "stream"
}

trait JobProgressListener {
    def before(name: String, sql: String): Unit

    def after(name: String, sql: String): Unit
}

class DefaultSQLJobProgressListener extends JobProgressListener with Logging {

    val actionSet = Set("save", "insert", "train", "run", "predict")
    var counter = 0

    override def before(name: String, sql: String): Unit = {
        counter += 1
        val context = SQLExecuteContext.getContext()
        val job = JobManager.getJobInfo.filter(f => f._1 == context.groupId).head._2
        // only save/insert will trigger execution

        def getHead(str: String) = {
            str.trim.toLowerCase().split("\\s+").head
        }

        val statements = context.execListener.cmdParserListener.get.statements

        val actions = statements.filter { statement =>
            actionSet.contains(getHead(statement))
        }

        var finalSize = actions.size
        if (!actionSet.contains(getHead(statements.last))) {
            finalSize += 1
        }
        var shouldLog = false

        if (actionSet.contains(name)) {
            job.progress.increment
            job.progress.script = sql
            shouldLog = true
        }
        job.progress.totalJob = finalSize

        if (counter == statements.size && !actionSet.contains(name)) {
            job.progress.currentJobIndex = job.progress.totalJob
            job.progress.script = sql
            shouldLog = true
        }
        if (shouldLog && !job.progress.script.startsWith("load _yisql_.")) {
            logInfo(s"Total jobs: ${job.progress.totalJob} current job:${job.progress.currentJobIndex} job script:${job.progress.script} ")
        }


    }

    override def after(name: String, sql: String): Unit = {

    }
}


/**
  *
  * @param owner      job所属用户
  * @param jobType    job类型 script,sql,stream
  * @param jobName    job名称
  * @param jobContent job内容
  * @param groupId    jobid
  * @param progress
  * @param startTime  任务开始时间
  * @param timeout    任务允许超时时间 -1不超时
  */
case class SQLJobInfo(
                             owner: String,
                             jobType: String,
                             jobName: String,
                             jobContent: String,
                             groupId: String,
                             progress: SQLJobProgress,
                             startTime: Long,
                             timeout: Long
                     )

case class SQLJobProgress(var totalJob: Long = 0, var currentJobIndex: Long = 0, var script: String = "") {
    def increment = currentJobIndex += 1

    def setTotal(total: Long) = {
        totalJob = total
    }
}
