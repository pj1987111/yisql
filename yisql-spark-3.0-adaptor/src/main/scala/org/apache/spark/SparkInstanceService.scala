package org.apache.spark

import java.util.concurrent.atomic.AtomicInteger

import com.zhy.yisql.common.utils.reflect.ScalaReflect
import org.apache.spark.scheduler.cluster.{CoarseGrainedSchedulerBackend, StandaloneSchedulerBackend}
import org.apache.spark.scheduler.local.LocalSchedulerBackend
import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 22:30
  *  \* Description: 
  *  \*/
class SparkInstanceService(session: SparkSession) {

    def resources = {
        var totalTasks = 0l
        var totalUsedMemory = 0l
        var totalMemory = 0l
        session.sparkContext.statusTracker.getExecutorInfos.foreach { worker =>
            totalTasks += worker.numRunningTasks()
            totalUsedMemory += (worker.usedOnHeapStorageMemory() + worker.usedOffHeapStorageMemory())
            totalMemory += (worker.totalOnHeapStorageMemory() + worker.totalOffHeapStorageMemory())
        }
        val totalCores = session.sparkContext.schedulerBackend match {
            case sb if sb.isInstanceOf[CoarseGrainedSchedulerBackend] =>
                ScalaReflect.field(sb, "totalCoreCount").asInstanceOf[AtomicInteger].get()
            case sb if sb.isInstanceOf[LocalSchedulerBackend] =>
                //val k8sDetect = System.getenv().get("KUBERNETES_SERVICE_HOST")
                java.lang.Runtime.getRuntime.availableProcessors
            case sb if sb.isInstanceOf[StandaloneSchedulerBackend] => -1
        }
        SparkInstanceResource(totalCores.toLong, totalTasks, totalUsedMemory, totalMemory)
    }

}

case class SparkInstanceResource(totalCores: Long, totalTasks: Long, totalUsedMemory: Long, totalMemory: Long)

