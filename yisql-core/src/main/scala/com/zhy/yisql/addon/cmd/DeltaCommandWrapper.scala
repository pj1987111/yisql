package com.zhy.yisql.addon.cmd

import com.zhy.yisql.addon.ScriptRunner
import com.zhy.yisql.addon.cmd.delta.{DeltaUtils, TableStat}
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.datasource.datalake.DataLake
import io.delta.tables.execution.VacuumTableCommand
import org.apache.spark.sql.delta.DeltaLog
import org.apache.spark.sql.delta.actions.CommitInfo
import org.apache.spark.sql.{DataFrame, SparkSession}
import tech.mlsql.common.PathFun
import tech.mlsql.common.utils.serder.json.JSONTool

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 15:02
  *  \* Description: 
  *  \*/
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
        val command = JSONTool.parseJson[List[String]](params("parameters"))
        command match {
            case Seq("compact", dataPath, version, numFile, _*) =>

                val code =
                    s"""
                       |run command as DeltaCompactionCommand.`${resolveRealPath(dataPath)}`
                       |where compactVersion="${version}"
                       |and compactRetryTimesForLock="10"
                       |and compactNumFilePerDir="${numFile}"
                       |and background="false"
                       |;
      """.stripMargin

                val runInBackGround = command.last == "background"

                var df: DataFrame = null
                if (runInBackGround) {
                    ScriptRunner.runSubJobAsync(
                        code, (df) => {}, Option(spark), false, false)
                } else {
                    df = ScriptRunner.rubSubJob(
                        code, (df) => {}, Option(spark), true, false).get
                }

                if (runInBackGround) spark.createDataset[String](Seq(s"Compact ${path} in background")).toDF("value") else {
                    df
                }


            case Seq("history", dataPath, _*) =>

                val deltaLog = DeltaLog.forTable(spark, resolveRealPath(dataPath))
                val history = deltaLog.history.getHistory(Option(1000))
                spark.createDataset[CommitInfo](history).toDF()


            case Seq("info", dataPath, _*) =>
                val deltaLog = DeltaLog.forTable(spark, resolveRealPath(dataPath))
                val info = DeltaUtils.tableStat(deltaLog)
                spark.createDataset[TableStat](Seq(info)).toDF()


            case Seq("show", "tables") =>
                val dataLake = new DataLake(spark)
                if (!dataLake.isEnable) {
                    throw new RuntimeException("datalake mode is not enabled.")
                } else {
                    spark.createDataset(dataLake.listTables).toDF()
                }

            case Seq("vacuum", dataPath, _*) =>
                VacuumTableCommand(Some(resolveRealPath(dataPath)), None, None, false).run(spark)
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
