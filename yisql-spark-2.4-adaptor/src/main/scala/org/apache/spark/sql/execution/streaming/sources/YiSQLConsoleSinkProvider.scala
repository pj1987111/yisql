package org.apache.spark.sql.execution.streaming.sources

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.execution.streaming.Sink
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, StreamSinkProvider}
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}

import java.io.DataOutputStream
import java.net.Socket
import scala.collection.JavaConverters._
/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-04
  *  \* Time: 22:46
  *  \* Description: 
  *  \*/
class YiSQLConsoleSinkProvider extends StreamSinkProvider
    with CreatableRelationProvider
    with Serializable with Logging {
  override def createSink(sqlContext: SQLContext,
                          parameters: Map[String, String],
                          partitionColumns: Seq[String],
                          outputMode: OutputMode): Sink = {
    new YiSQLConsoleSink(sqlContext, parameters)
  }

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], df: DataFrame): BaseRelation = {
    val dOut = ConsoleUtil.createWriteStream(parameters)
    val numRowsToShow = parameters.getOrElse("numRows", "20").toInt
    val truncateLength = parameters.getOrElse("truncateLength", "20").toInt

    val value = df.showString(numRowsToShow, truncateLength, true)
    value.split("\n").foreach { line =>
      dOut.write(ConsoleUtil.format(line))
    }
    dOut.flush()
    null
  }
}

class YiSQLConsoleSink(sqlContext: SQLContext, parameters: Map[String, String])
    extends Sink with Serializable with Logging {

  protected val numRowsToShow = parameters.getOrElse("numRows", "20").toInt
  protected val isTruncated = parameters.getOrElse("truncate", "true").toBoolean

  override def addBatch(batchId: Long, data: DataFrame): Unit = {

    val query = data.queryExecution
    val rdd = query.toRdd
    val df = sqlContext.internalCreateDataFrame(rdd, data.schema)
//    df.show(false)

    val dOut = ConsoleUtil.createWriteStream(parameters)
    dOut.write(ConsoleUtil.format("-------------------------------------------"))
    dOut.write(ConsoleUtil.format(s"Batch: $batchId"))
    dOut.write(ConsoleUtil.format("-------------------------------------------"))

    val value = df.showString(numRowsToShow, 20, isTruncated)
    value.split("\n").foreach { line =>
      dOut.write(ConsoleUtil.format(line))
    }
    dOut.flush()
    //df.foreachPartition用法2.12有问题，需要改成 df.rdd.foreachPartition
    //    df.rdd.foreachPartition(iter => {
    //      iter.foreach(row => {
    //        println(row)
    //        val dOut = createWriteStream(parameters)
    //        dOut.write(ConsoleUtil.format("-------------------------------------------"))
    //        dOut.write(ConsoleUtil.format(s"Batch: $batchId"))
    //        dOut.write(ConsoleUtil.format("-------------------------------------------"))
    //
    //
    //        dOut.write(ConsoleUtil.format(row.toString()))
    //
    //      })
    //    })

  }

  override def toString(): String = {
    new String(ConsoleUtil.format(s"YiSQLConsoleSink[numRows=$numRowsToShow, truncate=$isTruncated]"))
  }
}

object ConsoleUtil {
  def createWriteStream(parameters: Map[String, String]): DataOutputStream = {
    val host = parameters.getOrElse("host", "127.0.0.1")
    val port = parameters.getOrElse("port", "6049")
    val socket = SocketCache.getSocket(host, port.toInt)
    new DataOutputStream(socket.getOutputStream)
  }

  def format(str: String) = {
    //    val prefix = if (options.get("LogPrefix").isPresent) {
    //      options.get("LogPrefix").get()
    //    } else ""
    s"${str}\n".getBytes
  }
}

object SocketCache extends Logging {
  private val socketStore = new java.util.concurrent.ConcurrentHashMap[(String, Int), Socket]()

  def addSocket(host: String, port: Int) = {
    val socket = new Socket(host, port)
//    val outStream = new DataOutputStream(socket.getOutputStream)
    socketStore.put((host, port), socket)
    Some(socket)
  }

  def removeSocket(host: String, port: Int) = {
    val outStream = socketStore.remove((host, port))
    outStream.close()
  }

  def getSocket(host: String, port: Int) = {
    var socketCache = socketStore.asScala.get((host, port)).orElse(addSocket(host, port)).get
    if(socketCache.isClosed) {
      socketCache = addSocket(host, port).get
    }
    socketCache
  }

  def close = {
    socketStore.asScala.foreach {
      case (_, socket) =>
        socket.close()
      case _ => // ignore
    }
    socketStore.clear()
  }
}