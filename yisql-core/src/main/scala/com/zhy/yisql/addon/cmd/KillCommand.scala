package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.{ConnectMeta, SQLExecuteContext}
import com.zhy.yisql.core.job.{JobManager, SQLJobInfo}
import com.zhy.yisql.core.util.SQLJobCollect
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-03
 *  \* Time: 14:17
 *  \* Description: 
 *  \ */
class KillCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    def cleanPath: Array[String] = {
      path.split("/").filterNot((f: String) => f.isEmpty)
    }

    import spark.implicits._
    val newPath: Array[String] = cleanPath
    newPath match {
      case Array("job", killVal) =>
        if (killVal.isEmpty) {
          return Seq(("", s"value must be set....")).toDF("param", "description")
        }

        val groupId: String = new SQLJobCollect(spark, null).getGroupId(killVal)
        val owner: String = SQLExecuteContext.getContext().owner

        if (!JobManager.getJobInfo.contains(groupId)) {
          logWarning(s"You can not kill the job ${killVal} cause it not exist any more")
          return Seq(("", s"You can not kill the job ${killVal} cause it not exist any more")).toDF("param", "description")
        }

        val isOwnerTheSame: Boolean = JobManager.getJobInfo.filter((f: (String, SQLJobInfo)) => f._2.groupId == groupId)
          .count((f: (String, SQLJobInfo)) => f._2.owner == owner) == 1
        if (!isOwnerTheSame) {
          logWarning(s"You can not kill the job ${killVal} cause you are not the owner")
          return Seq(("", s"You can not kill the job ${killVal} cause you are not the owner")).toDF("param", "description")
        }
        try {
          JobManager.killJob(spark, groupId)
        } catch {
          case _: Exception =>
        }
        Seq.empty[(String, String)].toDF("param", "description")
      case Array("format", killVal) =>
        if (killVal.isEmpty) {
          return Seq(("", s"value must be set....")).toDF("param", "description")
        }
        ConnectMeta.removeFormatAlias(killVal)
        Seq.empty[(String, String)].toDF("param", "description")
      case _ =>
        Seq(("", s"type must be job or connect. !kill job/format value")).toDF("param", "description")
    }
  }
}
