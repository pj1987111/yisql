package com.zhy.yisql.addon.cmd

import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 19:00
  *  \* Description: 
  *  \*/
class ShowSchemaExt extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    var schemaString = "no schema..."
    if (StringUtils.isNotBlank(path)) {
      val loadTable = spark.table(path)
      schemaString = loadTable.schema.treeString
    }
    import spark.implicits._
    Seq(schemaString).toDF("schema")
  }
}
