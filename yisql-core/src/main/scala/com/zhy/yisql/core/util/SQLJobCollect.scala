package com.zhy.yisql.core.util

import com.zhy.yisql.core.job.{JobManager, SQLJobInfo}
import org.apache.spark.{SQLResource, SQLResourceRender, SQLScriptJobGroup}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.{StreamingQuery, StreamingQueryProgress}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 14:40
  *  \* Description: 
  *  \*/
class SQLJobCollect (spark: SparkSession, owner: String) {
    val resource = new SQLResource(spark, owner, getGroupId)

    def jobs: Seq[SQLJobInfo] = {
        val infoMap: Map[String, SQLJobInfo] = JobManager.getJobInfo
        val data: Seq[SQLJobInfo] = infoMap.toSeq.map(_._2).filter(_.owner == owner)
        data
    }

    def getJob(jobName: String): Seq[SQLJobInfo] = {
        val infoMap: Map[String, SQLJobInfo] = JobManager.getJobInfo
        val data: Seq[SQLJobInfo] = infoMap.toSeq.map(_._2).filter(_.owner == owner).filter(_.groupId == getGroupId(jobName))
        data
    }

    def getGroupId(jobNameOrGroupId: String): String = {
        JobManager.getJobInfo.find((f: (String, SQLJobInfo)) => f._2.jobName == jobNameOrGroupId) match {
            case Some(item) => item._2.groupId
            case None => jobNameOrGroupId
        }
    }

    def resourceSummary(jobGroupId: String): SQLResourceRender = {
        resource.resourceSummary(jobGroupId)
    }


    def jobDetail(jobGroupId: String, version: Int = 1): SQLScriptJobGroup = {
        if (version == 1) {
            resource.jobDetail(jobGroupId)
        }
        else {
            resource.jobDetail2(jobGroupId)
        }

    }

    def jobProgress(jobGroupId: String): Array[String] = {
        val finalJobGroupId: String = getGroupId(jobGroupId)
        val stream: StreamingQuery = spark.streams.get(finalJobGroupId)
        if (stream != null) {
            stream.recentProgress.map { f: StreamingQueryProgress =>
                f.json
            }
        } else {
            Array[String]()
        }
    }
}
