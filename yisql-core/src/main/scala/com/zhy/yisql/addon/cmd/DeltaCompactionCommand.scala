package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.spark.sql.delta.actions.{Action, AddFile, RemoveFile}
import org.apache.spark.sql.delta.commands.CompactTableInDelta
import org.apache.spark.sql.delta.{DeltaLog, DeltaOptions}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-14
 *  \* Time: 22:47
 *  \* Description: 
 *  \ */
class DeltaCompactionCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {


    //        params.get(compactVersion.name).map { p =>
    //            params.put(compactVersion, p.toInt)
    //        }.getOrElse {
    //            throw new MLSQLException(s"${compactVersion.name} is required")
    //        }
    //
    //        params.get(compactNumFilePerDir.name).map { p =>
    //            set(compactNumFilePerDir, p.toInt)
    //        }.getOrElse {
    //            throw new MLSQLException(s"${compactNumFilePerDir.name} is required")
    //        }
    //
    //        params.get(compactRetryTimesForLock.name).map { p =>
    //            set(compactRetryTimesForLock, p.toInt)
    //        }.getOrElse {
    //            throw new MLSQLException(s"${compactRetryTimesForLock.name} is required")
    //        }

    val deltaLog: DeltaLog = DeltaLog.forTable(spark, path)

    val optimizeTableInDelta: CompactTableInDelta = CompactTableInDelta(deltaLog,
      new DeltaOptions(Map[String, String](), spark.sessionState.conf), Seq(), params)

    val items: Seq[Row] = optimizeTableInDelta.run(spark)

    val acitons: Seq[Action] = items.map((f: Row) => Action.fromJson(f.getString(0)))
    val newFilesSize: Int = acitons.count((f: Action) => f.isInstanceOf[AddFile])
    val removeFilesSize: Int = acitons.count((f: Action) => f.isInstanceOf[RemoveFile])

    import spark.implicits._
    spark.createDataset[(String, Int)](Seq(("addNewFiles", newFilesSize), ("removeFiles", removeFilesSize))).toDF("name", "value")
  }

  //    final val compactVersion: Param[Int] = new Param[Int](this, "compactVersion", "")
  //    final val compactNumFilePerDir: Param[Int] = new Param[Int](this, "compactNumFilePerDir", "default 1")
  //    final val compactRetryTimesForLock: Param[Int] = new Param[Int](this, "compactRetryTimesForLock", "default 0")
}
