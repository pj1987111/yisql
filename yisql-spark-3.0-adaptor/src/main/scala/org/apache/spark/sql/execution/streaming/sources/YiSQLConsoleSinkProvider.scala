package org.apache.spark.sql.execution.streaming.sources

import java.io.DataOutputStream
import java.net.Socket

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.execution.streaming.Sink
import org.apache.spark.sql.sources.StreamSinkProvider
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-04
  *  \* Time: 22:46
  *  \* Description: 
  *  \*/
class YiSQLConsoleSinkProvider extends StreamSinkProvider with Serializable with Logging {
  override def createSink(sqlContext: SQLContext,
                          parameters: Map[String, String],
                          partitionColumns: Seq[String],
                          outputMode: OutputMode): Sink = {
    new YiSQLConsoleSink(sqlContext, parameters)
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

    val dOut = createWriteStream(parameters)
    dOut.write(format("-------------------------------------------"))
    dOut.write(format(s"Batch: $batchId"))
    dOut.write(format("-------------------------------------------"))

    val value = df.showString(numRowsToShow, 20, isTruncated)
    value.split("\n").foreach { line =>
      dOut.write(format(line))
    }
    dOut.flush()
    //df.foreachPartition用法2.12有问题，需要改成 df.rdd.foreachPartition
    //    df.rdd.foreachPartition(iter => {
    //      iter.foreach(row => {
    //        println(row)
    //        val dOut = createWriteStream(parameters)
    //        dOut.write(format("-------------------------------------------"))
    //        dOut.write(format(s"Batch: $batchId"))
    //        dOut.write(format("-------------------------------------------"))
    //
    //
    //        dOut.write(format(row.toString()))
    //
    //      })
    //    })

  }

  def format(str: String) = {
    //    val prefix = if (options.get("LogPrefix").isPresent) {
    //      options.get("LogPrefix").get()
    //    } else ""
    s"${str}\n".getBytes
  }

  def createWriteStream(parameters: Map[String, String]): DataOutputStream = {
    val host = parameters.getOrElse("host", "127.0.0.1")
    val port = parameters.getOrElse("port", "6049")
    SocketCache.getStream(host, port.toInt).get
  }

  override def toString(): String = {
    new String(format(s"YiSQLConsoleSink[numRows=$numRowsToShow, truncate=$isTruncated]"))
  }
}

case class SerialStream(dOut: DataOutputStream) extends Serializable

object SocketCache extends Logging {
  private val socketStore = new java.util.concurrent.ConcurrentHashMap[(String, Int), DataOutputStream]()

  def addStream(host: String, port: Int) = {
    val socket = new Socket(host, port.toInt)
    val outStream = new DataOutputStream(socket.getOutputStream)
    socketStore.put((host, port), outStream)
    Some(outStream)
  }

  def removeStream(host: String, port: Int) = {
    val outStream = socketStore.remove((host, port))
    outStream.close()
  }

  def getStream(host: String, port: Int) = {
    socketStore.asScala.get((host, port)).orElse(addStream(host, port))
  }

  def close = {
    socketStore.asScala.foreach {
      case (_, outStream) =>
        outStream.close()
      case _ => // ignore
    }
    socketStore.clear()
  }
}