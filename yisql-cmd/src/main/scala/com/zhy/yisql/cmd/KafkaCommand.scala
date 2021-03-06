package com.zhy.yisql.cmd

import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.path.PathFun
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.util.HDFSOperatorV2
import org.apache.spark.sql.kafka010.KafkaOffsetInfo
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.unsafe.types.UTF8String

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 13:35
  *  \* Description: 
  *  \*/
class KafkaCommand extends SQLCmd {
    override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
        if (path != "kafka") {
            throw new RuntimeException("KafkaCommand only support Kafka broker version 0.10.0 or higher")
        }

        val command = JSONTool.parseJson[List[String]](params("parameters"))

        import spark.implicits._

        // !kafka streamOffset ck
        command match {
            case List("streamOffset", ckPath, _*) =>
                val offsetPath = PathFun(ckPath).add("offsets").toPath
                val lastFile = HDFSOperatorV2.listFiles(offsetPath)
                        .filterNot(_.getPath.getName.endsWith(".tmp.crc"))
                        .map { fileName =>
                            (fileName.getPath.getName.split("/").last.toInt, fileName.getPath)
                        }
                        .sortBy(f => f._1).last._2

                val content = HDFSOperatorV2.readFile(lastFile.toString)
                val offsets = content.split("\n").last
                val desc =
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
        val action = command(0)

        val parameters = Map("sampleNum" -> command(1), "subscribe" -> command(3), "kafka.bootstrap.servers" -> command(2))
        val (startOffset, endOffset) = KafkaOffsetInfo.getKafkaInfo(spark, parameters)


        val newdf = spark
                .read
                .format("kafka")
                .options(parameters)
                .option("startingOffsets", startOffset.json)
                .option("endingOffsets", endOffset.json)
                .option("failOnDataLoss", "false")
                .load()
        val res = newdf.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .as[(String, String)]

        val sampleNum = params.getOrElse("sampleNum", "100").toLong

        action match {
            case "sampleData" => res.limit(sampleNum.toInt).toDF()
            case "schemaInfer" =>
                val schemaStr = SchemaInferCommand.inferSchema(res.collect().map(f => UTF8String.fromString(f._2)).toSeq, spark)
                logInfo(s"Infer schema: ${schemaStr}")
                Seq(Seq(schemaStr)).toDF("value")
            case "registerSchema" =>
                val schemaStr = SchemaInferCommand.inferSchema(res.collect().map(f => UTF8String.fromString(f._2)).toSeq, spark)
                logInfo(s"Infer schema: ${schemaStr}")
                SQLExecuteContext.getContext().execListener.addEnv("context_kafka_schema", schemaStr)
                Seq(Seq(schemaStr)).toDF("value")
        }
    }

}
