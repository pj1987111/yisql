package com.zhy.yisql.addon.cmd

import com.zhy.yisql.addon.ScriptRunner
import com.zhy.yisql.addon.cmd.delta.{DeltaUtils, TableStat}
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.path.PathFun
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.datasource.datalake.DataLake
import io.delta.tables.execution.VacuumTableCommand
import org.apache.spark.sql.delta.DeltaLog
import org.apache.spark.sql.delta.actions.CommitInfo
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-14
 *  \* Time: 15:02
 *  \* Description: 
 *  \ */
class DeltaCommandWrapper extends SQLCmd {

  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    import spark.implicits._

    def resolveRealPath(dataPath: String) = {
      val dataLake = new DataLake(spark)
      if (dataLake.isEnable) {
        dataLake.identifyToPath(dataPath)
      } else {
        PathFun(path).add(dataPath).toPath
      }
    }

    // !delta compact /data/table1 20 1 in background
    val command: Seq[String] = JSONTool.parseJson[List[String]](params("parameters"))
    command match {
      case Seq("compact", dataPath, version, numFile, _*) =>

        val code: String =
          s"""
             |run command as DeltaCompactionCommand.`${resolveRealPath(dataPath)}`
             |where compactVersion="${version}"
             |and compactRetryTimesForLock="10"
             |and compactNumFilePerDir="${numFile}"
             |and background="false"
             |;
      """.stripMargin

        val runInBackGround: Boolean = command.last == "background"

        var df: DataFrame = null
        if (runInBackGround) {
          ScriptRunner.runSubJobAsync(
            code, (_: DataFrame) => {}, Option(spark), reuseContext = false, reuseExecListenerEnv = false)
        } else {
          df = ScriptRunner.rubSubJob(
            code, (_: DataFrame) => {}, Option(spark), reuseContext = true, reuseExecListenerEnv = false).get
        }

        if (runInBackGround) spark.createDataset[String](Seq(s"Compact ${path} in background")).toDF("value") else {
          df
        }


      case Seq("history", dataPath, _*) =>

        val deltaLog: DeltaLog = DeltaLog.forTable(spark, resolveRealPath(dataPath))
        val history: Seq[CommitInfo] = deltaLog.history.getHistory(Option(1000))
        spark.createDataset[CommitInfo](history).toDF()


      case Seq("info", dataPath, _*) =>
        val deltaLog: DeltaLog = DeltaLog.forTable(spark, resolveRealPath(dataPath))
        val info: TableStat = DeltaUtils.tableStat(deltaLog)
        spark.createDataset[TableStat](Seq(info)).toDF()


      case Seq("show", "tables") =>
        val dataLake = new DataLake(spark)
        if (!dataLake.isEnable) {
          throw new RuntimeException("datalake mode is not enabled.")
        } else {
          spark.createDataset(dataLake.listTables).toDF()
        }

      case Seq("vacuum", dataPath, _*) =>
        VacuumTableCommand(Some(resolveRealPath(dataPath)), None, None, dryRun = false).run(spark)
        spark.createDataset[String](Seq(s"vacuum ${path} in background")).toDF("value")

      case Seq("help", _ *) =>
        spark.createDataset[String](Seq(
          """
            |!delta compact [tablePath] [compactVersion] [compactNumFilePerDir] [in background];
            |
            |`in background` is optional, and the other parameters is required.
            |
            |!delta history [tablePath];
            |!delta show tables;
                    """.stripMargin)).toDF("value")
      case _ => throw new RuntimeException(
        """
          |please use `!delta help;` to get the usage.
                """.stripMargin)
    }
  }
}
