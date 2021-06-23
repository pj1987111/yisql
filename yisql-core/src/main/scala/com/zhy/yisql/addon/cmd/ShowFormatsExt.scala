package com.zhy.yisql.addon.cmd

import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.ConnectMeta
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-03
 *  \* Time: 19:04
 *  \* Description: 
 *  \ */
class ShowFormatsExt extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    if (StringUtils.isBlank(path)) {
      import spark.implicits._
      Seq(JSONTool.toJsonStr(ConnectMeta.toMap)).toDF("description")
    } else {
      import spark.implicits._
      Seq(JSONTool.toJsonStr(ConnectMeta.toMap.getOrElse(path, ""))).toDF("description")
    }
  }
}
