package com.zhy.yisql.core.util

import com.zhy.yisql.core.job.JobManager
import org.apache.spark.SQLResource
import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 14:40
  *  \* Description: 
  *  \*/
class SQLJobCollect (spark: SparkSession, owner: String) {
    val resource = new SQLResource(spark, owner, getGroupId)

    def jobs = {
        val infoMap = JobManager.getJobInfo
        val data = infoMap.toSeq.map(_._2).filter(_.owner == owner)
        data
    }

    def getJob(jobName: String) = {
        val infoMap = JobManager.getJobInfo
        val data = infoMap.toSeq.map(_._2).filter(_.owner == owner).filter(_.groupId == getGroupId(jobName))
        data
    }

    def getGroupId(jobNameOrGroupId: String) = {
        JobManager.getJobInfo.find(f => f._2.jobName == jobNameOrGroupId) match {
            case Some(item) => item._2.groupId
            case None => jobNameOrGroupId
        }
    }

    def resourceSummary(jobGroupId: String) = {
        resource.resourceSummary(jobGroupId)
    }


    def jobDetail(jobGroupId: String, version: Int = 1) = {
        if (version == 1) {
            resource.jobDetail(jobGroupId)
        }
        else {
            resource.jobDetail2(jobGroupId)
        }

    }

    def jobProgress(jobGroupId: String) = {
        val finalJobGroupId = getGroupId(jobGroupId)
        val stream = spark.streams.get(finalJobGroupId)
        if (stream != null) {
            stream.recentProgress.map { f =>
                f.json
            }
        } else {
            Array[String]()
        }
    }
}
