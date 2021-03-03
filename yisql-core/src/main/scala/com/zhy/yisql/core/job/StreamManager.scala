package com.zhy.yisql.core.job

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.StreamingQueryListener

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-02
  *  \* Time: 19:54
  *  \* Description: 
  *  \*/
object StreamManager extends Logging {
    //流任务存储 groupid-任务信息
    private val store = new java.util.concurrent.ConcurrentHashMap[String, SQLJobInfo]()
    //流管理监听器
    private val listenerStore = new java.util.concurrent.ConcurrentHashMap[String, ArrayBuffer[YiSQLExternalStreamListener]]()

    def addStore(job: SQLJobInfo) = {
        store.put(job.groupId, job)
    }

    def removeStore(groupId: String) = {
        store.remove(groupId)
    }

    def getJob(groupId: String) = {
        store.asScala.get(groupId)
    }

    def close = {
        store.clear()
    }

    def listeners() = {
        listenerStore
    }

    def runEvent(eventName: StreamEventName.eventName, streamName: String, callback: (YiSQLExternalStreamListener) => Unit) = {
        listenerStore.asScala.foreach { case (user, items) =>
            items.filter(p => p.item.eventName == eventName && p.item.streamName == streamName).foreach { p =>
                callback(p)
            }
        }
    }

    def addListener(name: String, item: YiSQLExternalStreamListener) = {
        synchronized {
            val buffer = listenerStore.getOrDefault(name, ArrayBuffer())
            buffer += item
            listenerStore.put(name, buffer)
        }
    }


    def removeListener(uuid: String) = {
        listenerStore.asScala.foreach { items =>
            items._2.find(f => f.item.uuid == uuid) match {
                case Some(removeItem) => items._2.remove(items._2.indexOf(removeItem))
                case None =>
            }
        }
    }

    def start(sparkSession: SparkSession) = {
        logInfo("Start streaming job monitor....")
        sparkSession.streams.addListener(new YiSQLStreamingQueryListener)
    }
}

case class StreamListenerItem(uuid: String, user: String, streamName: String, eventName: StreamEventName.eventName, handleHttpUrl: String, method: String, params: Map[String, String])

object StreamEventName extends Enumeration {
    type eventName = Value
    val started = Value("started")
    val progress = Value("progress")
    val terminated = Value("terminated")
}

trait YiSQLExternalStreamListener extends Logging {
    def send(newParams: Map[String, String]): Unit

    def item: StreamListenerItem
}

class YiSQLStreamingQueryListener extends StreamingQueryListener with Logging {

    def sync(name: String, id: String) = {
        // first we should check by name, since before the stream is really started, we have record the name in
        // StreamingproJobManager
        JobManager.getJobInfo.find(f => f._2.jobType == JobType.STREAM
                && (f._2.jobName == name)) match {
            case Some(job) =>
                if (job._2.groupId != id) {
                    logInfo(s"""
                           |JobManager:${job._2.jobName}
                           |Spark streams: ${name}
                           |Action: sync
                           |Reason:: Job is not synced before.
             """.stripMargin)
                    //onQueryStarted is stared before we acquire info from StreamingQuery
                    JobManager.addJobManually(job._2.copy(groupId = id))
                }
            case None =>
                // we only care when stream is restore from ck without MLSQL instance restart
                // restore from  StreamManager.store
                StreamManager.getJob(id) match {
                    case Some(job) =>
                        logInfo(s"""
                               |JobManager:${job.jobName}
                               |Spark streams: ${name}
                               |Action: sync
                               |Reason:: Job is not in JobManager but in StreamManager.
             """.stripMargin)
                        JobManager.addJobManually(job)
                    case None =>
                        // this  should not happen,throw exception
                        throw new RuntimeException(s"YiSQL have unsync stream: ${name}")
                }
        }
    }

    def getJob(groupId: String) = {
        JobManager.getJobInfo.filter(f => f._2.groupId == groupId).values.headOption
    }

    override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {
        sync(event.name, event.id.toString)
        getJob(event.id.toString) match {
            case Some(job) =>
                StreamManager.runEvent(StreamEventName.started, job.jobName, p => {
                    p.send(Map("streamName" -> job.jobName, "jsonContent" -> "{}"))
                })
            case None => logError(s"Stream job [${event.id.toString}] is started. But we can not found it in JobManager.")
        }
    }

    override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
        val id = event.progress.id.toString
        sync(event.progress.name, id)

        getJob(event.progress.id.toString) match {
            case Some(job) =>
                StreamManager.runEvent(StreamEventName.progress, job.jobName, p => {
                    p.send(Map("streamName" -> job.jobName, "jsonContent" -> event.progress.json))
                })
            case None => logError(s"Stream job [${id}] is running. But we can not found it in JobManager.")
        }
    }

    override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {
        val id = event.id.toString
        var uuids = new ArrayBuffer[String]()
        getJob(id) match {
            case Some(job) =>
                StreamManager.runEvent(StreamEventName.terminated, job.jobName, p => {
                    p.send(Map("streamName" -> job.jobName, "jsonContent" -> "{}"))
                })
                StreamManager.listeners().asScala.get(job.owner).map { items =>
                    items.filter(p => p.item.streamName == job.jobName).map(
                        f => uuids += f.item.uuid
                    )
                }
            case None => logError(s"Stream job [${id}] is terminated. But we can not found it in JobManager.")
        }

        uuids.foreach(StreamManager.removeListener)

        StreamManager.removeStore(id)
        JobManager.getJobInfo.find(f => f._2.jobType == JobType.STREAM
                && f._2.groupId == id) match {
            case Some(job) =>
                JobManager.removeJobManually(job._1)
            case None =>
        }
    }
}