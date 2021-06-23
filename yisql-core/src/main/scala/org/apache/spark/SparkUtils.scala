package org.apache.spark

import org.apache.spark.deploy.SparkHadoopUtil
import org.apache.spark.rpc.RpcEnv
import org.apache.spark.storage.BlockManager

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-05
 *  \* Time: 18:36
 *  \* Description: 
 *  \ */
object SparkUtils {
  def rpcEnv(): RpcEnv = {
    SparkEnv.get.rpcEnv
  }

  def blockManager: BlockManager = {
    SparkEnv.get.blockManager
  }

  def sparkHadoopUtil: SparkHadoopUtil = {
    SparkHadoopUtil.get
  }
}
