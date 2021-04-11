package com.zhy.yisql.core.cmds

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 14:42
  *  \* Description: 
  *  \*/
trait SQLCmd extends Serializable with Logging {
    def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame

    def explainParams(sparkSession: SparkSession): DataFrame = {
        import sparkSession.implicits._
        Seq.empty[(String, String)].toDF("param", "description")
    }

    def emptyDataFrame()(implicit df: DataFrame) = {
        import df.sparkSession.implicits._
        Seq.empty[String].toDF("name")
    }

    def emptyDataFrame(spark: SparkSession, name : String = "name") = {
        import spark.implicits._
        Seq.empty[String].toDF(name)
    }
}
