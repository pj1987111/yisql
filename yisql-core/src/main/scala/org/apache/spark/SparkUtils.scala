package org.apache.spark

import org.apache.spark.deploy.SparkHadoopUtil

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-05
  *  \* Time: 18:36
  *  \* Description: 
  *  \*/
object SparkUtils {
    def rpcEnv() = {
        SparkEnv.get.rpcEnv
    }

    def blockManager = {
        SparkEnv.get.blockManager
    }

    def sparkHadoopUtil = {
        SparkHadoopUtil.get
    }
}
