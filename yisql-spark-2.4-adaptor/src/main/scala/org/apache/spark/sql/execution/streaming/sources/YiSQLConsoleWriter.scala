package org.apache.spark.sql.execution.streaming.sources

import java.io.DataOutputStream
import java.net.Socket

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.{DFVisitor, Dataset, SparkSession}
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.plans.logical.LocalRelation
import org.apache.spark.sql.sources.v2.DataSourceOptions
import org.apache.spark.sql.sources.v2.writer.{DataWriterFactory, WriterCommitMessage}
import org.apache.spark.sql.sources.v2.writer.streaming.StreamWriter
import org.apache.spark.sql.types.StructType

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-21
  *  \* Time: 23:46
  *  \* Description: 
  *  \*/
class YiSQLConsoleWriter(schema: StructType, options: DataSourceOptions, dOut: DataOutputStream)
    extends StreamWriter with Logging {

  // Number of rows to display, by default 20 rows
  protected val numRowsToShow = options.getInt("numRows", 20)

  // Truncate the displayed data if it is too long, by default it is true
  protected val isTruncated = options.getBoolean("truncate", true)

  assert(SparkSession.getActiveSession.isDefined)
  protected val spark = SparkSession.getActiveSession.get

  override def commit(epochId: Long, messages: Array[WriterCommitMessage]): Unit = {
    printRows(messages, schema, s"Batch: $epochId")
  }

  override def abort(epochId: Long, messages: Array[WriterCommitMessage]): Unit = {}

  override def createWriterFactory(): DataWriterFactory[InternalRow] = PackedRowWriterFactory

  protected def printRows(
                             commitMessages: Array[WriterCommitMessage],
                             schema: StructType,
                             printMessage: String): Unit = {
    val rows = commitMessages.collect {
      case PackedRowCommitMessage(rs) => rs
    }.flatten

//    val dOut = createWriteStream()
    // scalastyle:off println
    dOut.write(format("-------------------------------------------"))
    dOut.write(format(printMessage))
    dOut.write(format("-------------------------------------------"))
    // scalastyle:off println

    val newdata = Dataset.ofRows(spark, LocalRelation(schema.toAttributes, rows))
    val value = DFVisitor.showString(newdata, numRowsToShow, 20, isTruncated)
    value.split("\n").foreach { line =>
      dOut.write(format(line))
    }
    dOut.flush()
  }

  def format(str: String) = {
    val prefix = if (options.get("LogPrefix").isPresent) {
      options.get("LogPrefix").get()
    } else ""
    s"${prefix} ${str}\n".getBytes
  }

//  def createWriteStream(): DataOutputStream ={
//    val host = options.get("host").orElse("127.0.0.1")
//    val port = options.get("port").orElse("6049")
//    val socket = new Socket(host, port.toInt)
//    val dOut = new DataOutputStream(socket.getOutputStream)
//    dOut
//  }

  override def toString(): String = {
    new String(format(s"ConsoleWriter[numRows=$numRowsToShow, truncate=$isTruncated]"))
  }
}

object SocketCache {

}