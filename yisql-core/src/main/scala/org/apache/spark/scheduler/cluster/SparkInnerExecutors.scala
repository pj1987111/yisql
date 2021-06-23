package org.apache.spark.scheduler.cluster

import org.apache.spark.ExecutorAllocationClient
import org.apache.spark.scheduler.local.LocalSchedulerBackend
import org.apache.spark.sql.SparkSession

import java.lang.reflect.Field
import scala.collection.mutable

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-17
 *  \* Time: 20:21
 *  \* Description: 
 * 获取spark运行cores，executors，memorys等关键参数
 *  \ */
class SparkInnerExecutors(session: SparkSession) {
  def executorMemory: Int = {
    session.sparkContext.executorMemory
  }

  def executorCores: Int = {
    val items: collection.Map[String, ExecutorData] = executorDataMap
    if (items.nonEmpty) {
      items.head._2.totalCores
    } else {
      java.lang.Runtime.getRuntime.availableProcessors
    }
  }

  def executorDataMap: collection.Map[String, ExecutorData] = {
    executorAllocationClient match {
      case Some(eac) =>
        val item: CoarseGrainedSchedulerBackend = eac.asInstanceOf[CoarseGrainedSchedulerBackend]
        val field: Field = classOf[CoarseGrainedSchedulerBackend].
          getDeclaredField("org$apache$spark$scheduler$cluster$CoarseGrainedSchedulerBackend$$executorDataMap")
        field.setAccessible(true)
        val executors: mutable.Map[String, ExecutorData] = field.get(item).asInstanceOf[mutable.Map[String, ExecutorData]]
        executors
      case None => Map[String, ExecutorData]()
    }
  }

  def executorAllocationClient: Option[ExecutorAllocationClient] = {
    session.sparkContext.schedulerBackend match {
      case sb if sb.isInstanceOf[CoarseGrainedSchedulerBackend] =>
        Option(sb.asInstanceOf[ExecutorAllocationClient])
      case sb if sb.isInstanceOf[LocalSchedulerBackend] =>
        None
    }
  }

  def status: ResourceStatus = {
    val totalCores: Int = executorDataMap.map((f: (String, ExecutorData)) => f._2.totalCores).sum
    val totalMemory: Int = executorDataMap.map((_: (String, ExecutorData)) => executorMemory).sum
    val executorNum: Int = executorDataMap.size
    ResourceStatus(totalCores, totalMemory, executorNum)
  }
}

case class ResourceStatus(totalCores: Int, totalMemory: Long, executorNum: Int)

class SparkDynamicControlExecutors(session: SparkSession) {
  private[this] val sparkInnerExecutors = new SparkInnerExecutors(session)

  private def changeExecutors(num: Int, timeout: Long, isAdd: Boolean, f: () => Unit): Unit = {
    val currentSize: Int = sparkInnerExecutors.executorDataMap.size
    val targetSize: Int = if (isAdd) num else currentSize - num
    f()
    var count = 0
    var success = false
    while (!success && count < timeout / 1000) {
      val _currentSize: Int = sparkInnerExecutors.executorDataMap.size
      success = (_currentSize == targetSize)
      Thread.sleep(1000)
      count += 1
    }
    if (count >= timeout / 1000) {
      throw new RuntimeException(
        s"""
           |Resource Info:
           |
           |current_executor_num: ${currentSize}
           |target_executor_num: ${targetSize}
           |
           |Please check the status manually, maybe the cluster is too busy and we can not
           |allocate/deallocate executors.
        """.stripMargin)
    }
  }

  def requestTotalExecutors(num: Int, timeout: Long): Unit = {
    changeExecutors(num, timeout, isAdd = true, () => {
      session.sparkContext.requestTotalExecutors(num, 0, Map.empty)
    })
  }

  def killExecutors(num: Int, timeout: Long): Unit = {
    val items: Iterable[String] = sparkInnerExecutors.executorDataMap.keys.take(num)
    changeExecutors(num, timeout, isAdd = false, () => {
      session.sparkContext.killExecutors(items.toSeq)
    })
  }
}