package org.apache.spark

import org.apache.spark.sql.{SQLUtils, SparkSession}
import org.apache.spark.status.AppStatusStore
import org.apache.spark.status.api.v1
import org.apache.spark.status.api.v1.{ApplicationInfo, ExecutorSummary, JobData, StageData}

import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.{Buffer, ListBuffer}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-03
 *  \* Time: 14:32
 *  \* Description: 
 *  \ */
class SQLResource(spark: SparkSession, owner: String, getGroupId: String => String) {

  def resourceSummary(jobGroupId: String): SQLResourceRender = {
    val store: AppStatusStore = SQLUtils.getAppStatusStore(spark)
    val executorList: Seq[ExecutorSummary] = store.executorList(true)
    val totalExecutorList: Seq[ExecutorSummary] = store.executorList(false)
    val activeJobs: Seq[JobData] = store.jobsList(null).filter((f: JobData) => f.status == JobExecutionStatus.RUNNING)

    val finalJobGroupId: String = getGroupId(jobGroupId)

    def getNumActiveTaskByJob(job: v1.JobData): Int = {
      val (activeStages, _, _) = fetchStageByJob(job)
      activeStages.map((f: StageData) => f.numActiveTasks).sum
    }

    def getDiskBytesSpilled(job: v1.JobData): Long = {
      val (activeStages, _, _) = fetchStageByJob(job)
      activeStages.map((f: StageData) => f.diskBytesSpilled).sum
    }

    def getInputRecords(job: v1.JobData): Long = {
      val (activeStages, _, _) = fetchStageByJob(job)
      activeStages.map((f: StageData) => f.inputRecords).sum
    }

    def getMemoryBytesSpilled(job: v1.JobData): Long = {
      val (activeStages, _, _) = fetchStageByJob(job)
      activeStages.map((f: StageData) => f.memoryBytesSpilled).sum
    }


    val currentJobGroupActiveTasks: Int = if (jobGroupId == null) activeJobs.map(getNumActiveTaskByJob).sum
    else activeJobs.filter((f: JobData) => f.jobGroup.get == finalJobGroupId).map(getNumActiveTaskByJob).sum

    val currentDiskBytesSpilled: Long = if (jobGroupId == null) activeJobs.map(getDiskBytesSpilled).sum
    else activeJobs.filter((f: JobData) => f.jobGroup.get == finalJobGroupId).map(getDiskBytesSpilled).sum

    val currentInputRecords: Long = if (jobGroupId == null) activeJobs.map(getInputRecords).sum
    else activeJobs.filter((f: JobData) => f.jobGroup.get == finalJobGroupId).map(getInputRecords).sum

    val currentMemoryBytesSpilled: Long = if (jobGroupId == null) activeJobs.map(getMemoryBytesSpilled).sum
    else activeJobs.filter((f: JobData) => f.jobGroup.get == finalJobGroupId).map(getMemoryBytesSpilled).sum

    val shuffle: SQLShufflePerfRender = SQLShufflePerfRender(memoryBytesSpilled = currentMemoryBytesSpilled, diskBytesSpilled = currentDiskBytesSpilled, inputRecords = currentInputRecords)

    SQLResourceRender(
      currentJobGroupActiveTasks = currentJobGroupActiveTasks,
      activeTasks = executorList.map(_.activeTasks).sum,
      failedTasks = executorList.map(_.failedTasks).sum,
      completedTasks = executorList.map(_.completedTasks).sum,
      totalTasks = executorList.map(_.totalTasks).sum,
      taskTime = executorList.map(_.totalDuration).sum,
      gcTime = executorList.map(_.totalGCTime).sum,
      activeExecutorNum = executorList.size,
      totalExecutorNum = totalExecutorList.size,
      totalCores = executorList.map(_.totalCores).sum,
      usedMemory = executorList.map(_.memoryUsed).sum,
      totalMemory = totalExecutorList.map(_.maxMemory).sum,
      shuffleData = shuffle
    )
  }

  def fetchStageByJob(f: v1.JobData): (mutable.Buffer[StageData], mutable.Buffer[StageData], mutable.Buffer[StageData]) = {
    val store: AppStatusStore = SQLUtils.getAppStatusStore(spark)
    val stages: Seq[StageData] = f.stageIds.map { stageId: Int =>
      // This could be empty if the listener hasn't received information about the
      // stage or if the stage information has been garbage collected
      store.asOption(store.lastStageAttempt(stageId)).getOrElse {
        SQLUtils.createStage(stageId)
      }
    }

    val activeStages: mutable.Buffer[StageData] = mutable.Buffer[v1.StageData]()
    val completedStages: mutable.Buffer[StageData] = mutable.Buffer[v1.StageData]()
    // If the job is completed, then any pending stages are displayed as "skipped":
    val pendingOrSkippedStages: mutable.Buffer[StageData] = mutable.Buffer[v1.StageData]()
    val failedStages: mutable.Buffer[StageData] = mutable.Buffer[v1.StageData]()
    for (stage <- stages) {
      if (stage.submissionTime.isEmpty) {
        pendingOrSkippedStages += stage
      } else if (stage.completionTime.isDefined) {
        if (stage.status == v1.StageStatus.FAILED) {
          failedStages += stage
        } else {
          completedStages += stage
        }
      } else {
        activeStages += stage
      }
    }
    (activeStages, completedStages, failedStages)
  }

  def jobDetail2(jobGroupId: String): SQLScriptJobGroup = {
    val store: AppStatusStore = SQLUtils.getAppStatusStore(spark)
    val appInfo = store.applicationInfo()
    val finalJobGroupId: String = getGroupId(jobGroupId)
    val items: Seq[SQLScriptJob] = store.jobsList(null).filter((_: JobData).jobGroup.isDefined).filter((_: JobData).jobGroup.get == finalJobGroupId).map { f: JobData =>
      //val (activeStages, completedStages, failedStages) = fetchStageByJob(f)
      val endTime: Long = f.completionTime.map((date: Date) => date.getTime).getOrElse(System.currentTimeMillis())
      val duration: Long = f.submissionTime.map((date: Date) => endTime - date.getTime).getOrElse(0L)
      SQLScriptJob(
        f.jobId,
        f.submissionTime.map((date: Date) => new java.sql.Date(date.getTime)),
        f.completionTime.map((date: Date) => new java.sql.Date(date.getTime)),
        f.numTasks,
        f.numActiveTasks, //activeStages.map(f => f.numActiveTasks).sum,
        f.numCompletedTasks,
        f.numSkippedTasks,
        f.numFailedTasks,
        f.numKilledTasks,
        f.numCompletedIndices,
        f.numActiveStages,
        f.numCompletedStages,
        f.numSkippedStages,
        f.numFailedStages, duration
      )
    }

    SQLScriptJobGroup(
      finalJobGroupId, 0, 0, 0, items
    )
  }

  def jobDetail(jobGroupId: String): SQLScriptJobGroup = {
    val store: AppStatusStore = SQLUtils.getAppStatusStore(spark)
    val appInfo: ApplicationInfo = store.applicationInfo()
    val startTime = appInfo.attempts.head.startTime.getTime
    val endTime = appInfo.attempts.head.endTime.getTime

    val finalJobGroupId: String = getGroupId(jobGroupId)

    val activeJobs = new ListBuffer[v1.JobData]()
    val completedJobs = new ListBuffer[v1.JobData]()
    val failedJobs = new ListBuffer[v1.JobData]()
    store.jobsList(null).filter(_.jobGroup.isDefined).filter(_.jobGroup.get == finalJobGroupId).foreach { job: JobData =>
      job.status match {
        case JobExecutionStatus.SUCCEEDED =>
          completedJobs += job
        case JobExecutionStatus.FAILED =>
          failedJobs += job
        case _ =>
          activeJobs += job
      }
    }

    val yisqlActiveJobs: ListBuffer[SQLScriptJob] = activeJobs.map { f: JobData =>

      val (activeStages, _, _) = fetchStageByJob(f)

      SQLScriptJob(
        f.jobId,
        f.submissionTime.map((date: Date) => new java.sql.Date(date.getTime)),
        f.completionTime.map((date: Date) => new java.sql.Date(date.getTime)),
        f.numTasks,
        activeStages.map((_: StageData).numActiveTasks).sum,
        f.numCompletedTasks,
        f.numSkippedTasks,
        f.numFailedTasks,
        f.numKilledTasks,
        f.numCompletedIndices,
        f.numActiveStages,
        f.numCompletedStages,
        f.numSkippedStages,
        f.numFailedStages, 0
      )
    }
    SQLScriptJobGroup(
      jobGroupId, activeJobs.size, completedJobs.size, failedJobs.size, yisqlActiveJobs
    )
  }
}
