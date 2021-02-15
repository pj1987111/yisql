package com.zhy.yisql.runner

import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 17:23
  *  \* Description: 
  *  \*/
object JobManager {
//    def run(session: SparkSession, job: JobInfo, f: () => Unit): Unit = {
//
//        val context = ScriptSQLExec.getContext()
//        try {
//            _jobListeners.foreach { f => f.onJobStarted(new JobStartedEvent(job.groupId)) }
//            if (_jobManager == null) {
//                f()
//            } else {
//                session.sparkContext.setJobGroup(job.groupId, job.jobName, true)
//                _jobManager.groupIdToMLSQLJobInfo.put(job.groupId, job)
//                f()
//            }
//
//        } finally {
//            handleJobDone(job.groupId)
//            session.sparkContext.clearJobGroup()
//            _jobListeners.foreach { f => f.onJobFinished(new JobFinishedEvent(job.groupId)) }
//        }
//    }

    def run(f: () => Unit): Unit = {
        f()
    }
}

class JobManager {

}
