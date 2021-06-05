package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.spark.sql.delta.actions.{Action, AddFile, RemoveFile}
import org.apache.spark.sql.delta.commands.CompactTableInDelta
import org.apache.spark.sql.delta.{DeltaLog, DeltaOptions}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 22:47
  *  \* Description: 
  *  \*/
class DeltaCompactionCommand extends SQLCmd{
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

        val deltaLog = DeltaLog.forTable(spark, path)

        val optimizeTableInDelta = CompactTableInDelta(deltaLog,
            new DeltaOptions(Map[String, String](), spark.sessionState.conf), Seq(), params)

        val items = optimizeTableInDelta.run(spark)

        val acitons = items.map(f => Action.fromJson(f.getString(0)))
        val newFilesSize = acitons.filter(f => f.isInstanceOf[AddFile]).size
        val removeFilesSize = acitons.filter(f => f.isInstanceOf[RemoveFile]).size

        import spark.implicits._
        spark.createDataset[(String, Int)](Seq(("addNewFiles", newFilesSize), ("removeFiles", removeFilesSize))).toDF("name", "value")
    }

//    final val compactVersion: Param[Int] = new Param[Int](this, "compactVersion", "")
//    final val compactNumFilePerDir: Param[Int] = new Param[Int](this, "compactNumFilePerDir", "default 1")
//    final val compactRetryTimesForLock: Param[Int] = new Param[Int](this, "compactRetryTimesForLock", "default 0")
}
