package org.apache.spark

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 14:36
  *  \* Description: 
  *  \*/
case class SQLScriptJobGroup(groupId: String,
                             activeJobsNum: Int,
                             completedJobsNum: Int,
                             failedJobsNum: Int,
                             activeJobs: Seq[SQLScriptJob]
                               )

case class SQLScriptJob(
                                 val jobId: Int,
                                 val submissionTime: Option[java.sql.Date],
                                 val completionTime: Option[java.sql.Date],
                                 val numTasks: Int,
                                 val numActiveTasks: Int,
                                 val numCompletedTasks: Int,
                                 val numSkippedTasks: Int,
                                 val numFailedTasks: Int,
                                 val numKilledTasks: Int,
                                 val numCompletedIndices: Int,
                                 val numActiveStages: Int,
                                 val numCompletedStages: Int,
                                 val numSkippedStages: Int,
                                 val numFailedStages: Int,
                                 val duration: Long
                         )

case class SQLResourceRender(
                                      currentJobGroupActiveTasks: Int,
                                      activeTasks: Int,
                                      failedTasks: Int,
                                      completedTasks: Int,
                                      totalTasks: Int,
                                      taskTime: Double,
                                      gcTime: Double,
                                      activeExecutorNum: Int,
                                      totalExecutorNum: Int,
                                      totalCores: Int,
                                      usedMemory: Double,
                                      totalMemory: Double,
                                      shuffleData: SQLShufflePerfRender
                              )

case class SQLShufflePerfRender(
                                         memoryBytesSpilled: Long,
                                         diskBytesSpilled: Long,
                                         inputRecords: Long

                                 )