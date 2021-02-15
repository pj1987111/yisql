package com.zhy.yisql.cmd.delta

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 19:26
  *  \* Description: 
  *  \*/
import org.apache.spark.sql.delta.DeltaLog


object DeltaUtils {
    def tableStat(deltaLog: DeltaLog) = {
        val s = deltaLog.snapshot
        TableStat(s.sizeInBytes, s.numOfFiles, s.numOfMetadata, s.numOfProtocol, s.numOfRemoves, s.numOfSetTransactions)
    }
}

case class TableStat(
                        sizeInBytes: Long,
                        numOfFiles: Long,
                        numOfMetadata: Long,
                        numOfProtocol: Long,
                        numOfRemoves: Long,
                        numOfSetTransactions: Long)
