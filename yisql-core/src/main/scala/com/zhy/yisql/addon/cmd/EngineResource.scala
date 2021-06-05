package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.spark.SparkConf
import org.apache.spark.scheduler.cluster.{ResourceStatus, SparkDynamicControlExecutors, SparkInnerExecutors}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-17
  *  \* Time: 20:18
  *  \* Description: 
  *  \*/
class EngineResource extends SQLCmd {
  final val action: String = "action"
  final val cpus: String = "cpus"
  final val timeout: String = "timeout"

  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val executorInfo = new SparkInnerExecutors(spark)
    val resourceControl = new SparkDynamicControlExecutors(spark)

    def isLocalMaster(conf: SparkConf): Boolean = {
      val master = conf.get("spark.master", "")
      master == "local" || master.startsWith("local[")
    }

    if (isLocalMaster(spark.sparkContext.getConf)) {
      throw new RuntimeException("Local mode not support this action");
    }

    if (!params.contains(action) || params(action).isEmpty) {
      import spark.implicits._
      return spark.createDataset[ResourceStatus](Seq(executorInfo.status)).toDF()
    }

    val _action = params.getOrElse(action, "")
    val _cpus = parseCores(params.getOrElse(cpus, ""))
    val _timeout = params.getOrElse(timeout, "60 * 1000").toLong

    //需要增减的executor cpu数/每个executor核数
    val executorsShouldAddOrRemove = Math.floor(_cpus / executorInfo.executorCores).toInt
    //当前executor数
    val currentExecutorNum = executorInfo.executorDataMap.size

    def tooMuchWithOneTime(cpusOrExecutorNum: Int) = {
      if (cpusOrExecutorNum > 20) {
        throw new RuntimeException("Too many cpus added at one time. Please add them with multi times.")
      }
    }

    parseAction(_action) match {
      case Action.+ | Action.ADD =>
        logInfo(s"Adding cpus; currentExecutorNum:${currentExecutorNum} targetExecutorNum:${currentExecutorNum + executorsShouldAddOrRemove}")
        tooMuchWithOneTime(_cpus)
        resourceControl.requestTotalExecutors(currentExecutorNum + executorsShouldAddOrRemove, _timeout)
      case Action.- | Action.REMOVE =>
        logInfo(s"Removing cpus; currentExecutorNum:${currentExecutorNum} targetExecutorNum:${currentExecutorNum - executorsShouldAddOrRemove}")
        tooMuchWithOneTime(_cpus)
        resourceControl.killExecutors(executorsShouldAddOrRemove, _timeout)
      case Action.SET =>

        val diff = executorsShouldAddOrRemove - currentExecutorNum
        if (diff < 0) {
          tooMuchWithOneTime(-diff)
          logInfo(s"Adding cpus; currentExecutorNum:${currentExecutorNum} targetExecutorNum:${currentExecutorNum + diff}")
          resourceControl.killExecutors(-diff, _timeout)
        }

        if (diff > 0) {
          tooMuchWithOneTime(diff)
          logInfo(s"Removing cpus; currentExecutorNum:${currentExecutorNum} targetExecutorNum:${executorsShouldAddOrRemove}")
          resourceControl.requestTotalExecutors(executorsShouldAddOrRemove, _timeout)
        }
    }

    import spark.implicits._
    spark.createDataset[ResourceStatus](Seq(executorInfo.status)).toDF()
  }

  def parseCores(str: String) = {
    if (str.toLowerCase.endsWith("c")) {
      str.toLowerCase.stripSuffix("c").toInt
    } else {
      str.toInt
    }
  }

  def parseAction(str: String) = {
    Action.withName(str)
  }

  object Action extends Enumeration {
    type name = Value
    val ADD = Value("add")
    val REMOVE = Value("remove")
    val + = Value("+")
    val - = Value("-")
    val SET = Value("set")
  }

}
