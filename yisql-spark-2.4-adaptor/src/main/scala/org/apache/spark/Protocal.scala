package org.apache.spark

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-03
 *  \* Time: 14:36
 *  \* Description: 
 *  \ */
case class SQLScriptJobGroup(groupId: String,
                             activeJobsNum: Int,
                             completedJobsNum: Int,
                             failedJobsNum: Int,
                             activeJobs: Seq[SQLScriptJob]
                            )

case class SQLScriptJob(
                         jobId: Int,
                         submissionTime: Option[java.sql.Date],
                         completionTime: Option[java.sql.Date],
                         numTasks: Int,
                         numActiveTasks: Int,
                         numCompletedTasks: Int,
                         numSkippedTasks: Int,
                         numFailedTasks: Int,
                         numKilledTasks: Int,
                         numCompletedIndices: Int,
                         numActiveStages: Int,
                         numCompletedStages: Int,
                         numSkippedStages: Int,
                         numFailedStages: Int,
                         duration: Long
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