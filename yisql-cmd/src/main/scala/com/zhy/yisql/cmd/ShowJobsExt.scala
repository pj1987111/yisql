package com.zhy.yisql.cmd

import org.apache.spark.utils.json.JsonUtils
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.job.JobManager
import com.zhy.yisql.core.util.SQLJobCollect
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 19:04
  *  \* Description: 
  *  \*/
class ShowJobsExt extends SQLCmd {
    override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
        if (StringUtils.isBlank(path)) {
            import spark.implicits._
            Seq(JsonUtils.toPrettyJson(JobManager.getJobInfo)).toDF("description")
        } else {
            val groupId = new SQLJobCollect(spark, null).getGroupId(path)
            import spark.implicits._
            Seq(JsonUtils.toPrettyJson(new SQLJobCollect(spark, null).resourceSummary(groupId))).toDF("description")
        }
    }
}
