package org.apache.spark.sql.execution.streaming.sources

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-21
  *  \* Time: 23:19
  *  \* Description: 
  *  \*/
import java.io.DataOutputStream
import java.net.Socket

import com.zhy.yisql.common.utils.log.Logging
//import com.zhy.yisql.core.execute.SQLExecuteContext
import org.apache.spark.sql._
import org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleWriter
import org.apache.spark.sql.sources.v2.writer.streaming.StreamWriter
import org.apache.spark.sql.sources.v2.{DataSourceOptions, DataSourceV2, StreamWriteSupport}
import org.apache.spark.sql.sources.{BaseRelation, CreatableRelationProvider, DataSourceRegister}
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.StructType

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

case class YiSQLConsoleRelation(override val sqlContext: SQLContext, data: DataFrame)
    extends BaseRelation {
  override def schema: StructType = data.schema
}

class YiSQLConsoleSinkProvider extends DataSourceV2
    with StreamWriteSupport
    with DataSourceRegister
    with CreatableRelationProvider with Logging {

//  val context = SQLExecuteContext.getContext()

  override def createStreamWriter(queryId: String, schema: StructType, mode: OutputMode, options: DataSourceOptions): StreamWriter = {
    logInfo("in YiSQLConsoleSinkProvider createStreamWriter...")
//    SQLExecuteContext.setContext(context)

    new YiSQLConsoleWriter(schema, options, createWriteStream(options))
  }

  override def shortName(): String = "yisql_console"

  override def createRelation(sqlContext: SQLContext, mode: SaveMode, parameters: Map[String, String], data: DataFrame): BaseRelation = {
    logInfo("in YiSQLConsoleSinkProvider createRelation...")
    // Number of rows to display, by default 20 rows
    val numRowsToShow = parameters.get("numRows").map(_.toInt).getOrElse(20)

    // Truncate the displayed data if it is too long, by default it is true
    val isTruncated = parameters.get("truncate").map(_.toBoolean).getOrElse(true)

    val value = DFVisitor.showString(data, numRowsToShow, 20, isTruncated)
    value.split("\n").foreach { line =>
      logInfo(line)
    }
    YiSQLConsoleRelation(sqlContext, data)
  }

  def createWriteStream(options: DataSourceOptions): DataOutputStream = {
    val host = options.get("host").orElse("127.0.0.1")
    val port = options.get("port").orElse("6049")
    SocketCache.getStream(host, port.toInt).get
  }
}

object SocketCache extends Logging {
  private val socketStore = new java.util.concurrent.ConcurrentHashMap[(String, Int), DataOutputStream]()

  def addStream(host:String, port:Int) = {
    val socket = new Socket(host, port.toInt)
    val outStream = new DataOutputStream(socket.getOutputStream)
    socketStore.put((host, port), outStream)
    Some(outStream)
  }

  def removeStream(host:String, port:Int) = {
    val outStream = socketStore.remove((host, port))
    outStream.close()
  }

  def getStream(host:String, port:Int) = {
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
