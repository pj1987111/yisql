package com.zhy.yisql.addon.cmd

import com.zhy.yisql.addon.cmd.hive.{HiveUtils, MergeConfig}
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.catalyst.catalog.CatalogTable
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util
import java.util.LinkedList

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-13
 *  \* Time: 14:04
 *  \* Description: 
 *  \ */
class HiveCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val commands: Array[String] = JSONTool.parseJson[List[String]](params("parameters")).toArray
    commands match {
      /**
       * merge table dbTable partition partionSpec
       * dbTable db.table
       * partionSpec "ds=1,ds=2"
       *
       */
      case Array("merge", "table", dbTable, "partition", partitionSpec) =>
        val catlogT: CatalogTable = HiveUtils.parseDBAndTableFromStr(dbTable, spark)
        val dsV: Array[String] = partitionSpec.split(",")
        val dsList = new util.LinkedList[String]()

        for (ds <- dsV) {
          var Array(key, value) = ds.split("=")
          if (value.startsWith("'") && value.endsWith("'"))
            value = StringUtils.substringBetween(value, "'")
          else if (value.startsWith("\"") && value.endsWith("\""))
            value = StringUtils.substringBetween(value, "\"")
          dsList.add(key + "=" + value)
        }
        val location: String = catlogT.location.getPath + "/" + StringUtils.join(dsList, "/")
        HiveUtils.mergePath(spark, catlogT, location)
        emptyDataFrame(spark)

      /**
       * merge table dbTable
       * dbTable db.table
       */
      case Array("merge", "table", dbTable) =>
        val catlogT: CatalogTable = HiveUtils.parseDBAndTableFromStr(dbTable, spark)
        val recursive: String = spark.sparkContext.getConf.get(MergeConfig.sparkMergeRecursive, "true")
        if (recursive.equalsIgnoreCase("true")) {
          logInfo(s"merge table $dbTable recursively")
          HiveUtils.mergeRecursive(spark, catlogT, catlogT.location.getPath)
        } else {
          HiveUtils.mergePath(spark, catlogT, catlogT.location.getPath)
        }
        emptyDataFrame(spark)
      case Array("sql", sql) =>
        spark.sql(sql)
      case _ =>
        emptyDataFrame(spark)
    }
  }
}
