package com.zhy.yisql.addon.cmd

import com.zhy.yisql.addon.cmd.hdfs.WowFsShell
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.core.cmds.SQLCmd
import org.apache.hadoop.util.ToolRunner
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-17
  *  \* Time: 21:42
  *  \* Description: 
  *  \*/
class HDFSCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val conf = spark.sessionState.newHadoopConf()
    val args = JSONTool.parseJson[List[String]](params("parameters"))
    conf.setQuietMode(false)
    var output = ""
    val fsShell = new WowFsShell(conf, path)
    try {
      ToolRunner.run(fsShell, args.toArray)
      output = fsShell.getError

      if (output == null || output.isEmpty) {
        output = fsShell.getOut
      }
    }
    finally {
      fsShell.close()
    }
    import spark.implicits._
    if (args.contains("-F")) {
      val ds = spark.createDataset(output.split("\n").toSeq)
      spark.read.json(ds)
    } else {
      spark.createDataset[String](Seq(output)).toDF("fileSystem")
    }
  }
}
