package com.zhy.yisql.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.job.JobManager
import com.zhy.yisql.core.util.SQLJobCollect
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 14:17
  *  \* Description: 
  *  \*/
class SQLYiSQLJobExt extends SQLCmd {
    override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
        val groupId = new SQLJobCollect(spark, null).getGroupId(path)
        val owner = SQLExecuteContext.getContext().owner

        if (!JobManager.getJobInfo.contains(groupId)) {
            logWarning(s"You can not kill the job $path cause it not exist any more")
            import spark.implicits._
            return Seq(("", s"You can not kill the job $path cause it not exist any more")).toDF("param", "description")
        }

        val isOwnerTheSame = JobManager.getJobInfo.filter(f => f._2.groupId == groupId).filter(f => f._2.owner == owner).size == 1
        if (!isOwnerTheSame) {
            logWarning(s"You can not kill the job $path cause you are not the owner")
            import spark.implicits._
            return Seq(("", s"You can not kill the job $path cause you are not the owner")).toDF("param", "description")
        }
        try{
            JobManager.killJob(spark, groupId)
        } catch {
            case e:Exception =>
        }
        import spark.implicits._
        Seq.empty[(String, String)].toDF("param", "description")
    }
}
