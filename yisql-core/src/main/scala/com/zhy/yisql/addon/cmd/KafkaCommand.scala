package com.zhy.yisql.addon.cmd

import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.path.PathFun
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.util.HDFSOperatorV2
import org.apache.hadoop.fs.{FileStatus, Path}
import org.apache.spark.sql.kafka010.KafkaOffsetInfo
import org.apache.spark.sql.{DataFrame, DataFrameReader, Dataset, SparkSession}
import org.apache.spark.unsafe.types.UTF8String

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-15
 *  \* Time: 13:35
 *  \* Description: 
 *  \ */
class KafkaCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    if (path != "kafka") {
      throw new RuntimeException("KafkaCommand only support Kafka broker version 0.10.0 or higher")
    }

    val command: Seq[String] = JSONTool.parseJson[List[String]](params("parameters"))

    import spark.implicits._

    // !kafka streamOffset ck
    command match {
      case List("streamOffset", ckPath, _*) =>
        val offsetPath: String = PathFun(ckPath).add("offsets").toPath
        val lastFile: Path = HDFSOperatorV2.listFiles(offsetPath)
          .filterNot((_: FileStatus).getPath.getName.endsWith(".tmp.crc"))
          .map { fileName: FileStatus =>
            (fileName.getPath.getName.split("/").last.toInt, fileName.getPath)
          }
          .maxBy((f: (Int, Path)) => f._1)._2

        val content: String = HDFSOperatorV2.readFile(lastFile.toString)
        val offsets: String = content.split("\n").last
        val desc: String =
          """
            |-- the stream name, should be uniq.
            |set streamName="kafkaStreamExample";
            |
            |!kafka registerSchema 2 records from "127.0.0.1:9092" hello;
            |
            |load kafka.`wow` options
            |startingOffsets='''
            | PUT THE OFFSET JSON HERE
            |'''
            |and kafka.bootstrap.servers="127.0.0.1:9092"
            |as newkafkatable1;
                    """.stripMargin
        return spark.createDataset[(String, String)](Seq((offsets, desc))).toDF("offsets", "doc")
      case _ =>
    }

    //action: sampleData,schemaInfer
    val action: String = command.head

    val parameters = Map("sampleNum" -> command(1), "subscribe" -> command(3), "kafka.bootstrap.servers" -> command(2))
    val (startOffset, endOffset) = KafkaOffsetInfo.getKafkaInfo(spark, parameters)


    var reader: DataFrameReader = spark
      .read
      .format("kafka")
      .options(parameters)
    if (startOffset != null) {
      reader = reader.option("startingOffsets", startOffset.json)
    }
    if (endOffset != null) {
      reader = reader.option("endingOffsets", endOffset.json)
    }
    val newDF: DataFrame = reader.option("failOnDataLoss", "false").load()
    val res: Dataset[(String, String)] = newDF.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]

    val sampleNum: Long = params.getOrElse("sampleNum", "100").toLong

    action match {
      case "sampleData" => res.limit(sampleNum.toInt).toDF()
      case "schemaInfer" =>
        val schemaStr: String = SchemaInferCommand.inferSchema(res.collect().map((f: (String, String)) => UTF8String.fromString(f._2)).toSeq, spark)
        logInfo(s"Infer schema: ${schemaStr}")
        Seq(Seq(schemaStr)).toDF("value")
      case "registerSchema" =>
        val schemaStr: String = SchemaInferCommand.inferSchema(res.collect().map((f: (String, String)) => UTF8String.fromString(f._2)).toSeq, spark)
        logInfo(s"Infer schema: ${schemaStr}")
        SQLExecuteContext.getContext().execListener.addEnv("context_kafka_schema", schemaStr)
        Seq(Seq(schemaStr)).toDF("value")
    }
  }

}
