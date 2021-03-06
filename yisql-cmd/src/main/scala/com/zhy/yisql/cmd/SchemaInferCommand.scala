package com.zhy.yisql.cmd

import com.alibaba.fastjson.JSON
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.util.SparkSchemaJsonParser
import org.apache.spark.sql.execution.datasources.json.JsonInferSchema
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.unsafe.types.UTF8String
import tech.mlsql.common.utils.serder.json.JSONTool

import scala.collection.JavaConversions._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 19:02
  *  \* Description: 
  *  \*/
class SchemaInferCommand extends SQLCmd {
    override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
        val command = JSONTool.parseJson[List[String]](params("parameters"))

        import spark.implicits._

        command match {
            case Seq("json", str, _*) =>
                val schemaStr = SchemaInferCommand.inferSchema(str.split("\n").map(f => UTF8String.fromString(f)), spark)
                logInfo(s"Infer schema: ${schemaStr}")
                Seq(Seq(schemaStr)).toDF("value")
            case Seq("jsonv2", str, _*) =>
                val schemaStr = SchemaInferCommand.inferSchemaV2(str.split("\n").map(f => UTF8String.fromString(f)), spark)
                logInfo(s"Infer schema: ${schemaStr}")
                Seq(Seq(schemaStr)).toDF("value")
            case _ => throw new RuntimeException(
                """
                  |please use `!schemainfer help;` to get the usage.
                """.stripMargin)
        }
    }
}

object SchemaInferCommand {
    def inferSchema(data: Seq[UTF8String], spark: SparkSession) = {
        val schema = JsonInferSchema.inferJson(data, spark)
        val schemaStr = JsonInferSchema.serializeSchema(schema)
        schemaStr
    }

    def inferSchemaV2(data: Seq[UTF8String], spark: SparkSession) = {
        val schema = JsonInferSchema.inferJson(data, spark)
        val schemaStr = SparkSchemaJsonParser.serializeSchema(schema)
        schemaStr
    }

    def inferSchemaOld(dataMsg: String, spark: SparkSession) = {
        var uSeq: Seq[UTF8String] = Seq.empty[UTF8String]

        for (data <- dataMsg.split("\n")) {
            if (data.charAt(0) == '[' && data.charAt(data.length - 1) == ']') {
                for (line <- JSON.parseArray(data)) {
                    val utfStr = UTF8String.fromString(line.toString)
                    uSeq = uSeq :+ utfStr
                }
            } else
                uSeq = uSeq :+ UTF8String.fromString(data)
        }

        val schema = JsonInferSchema.inferJson(uSeq, spark)
        val schemaStr = JsonInferSchema.serializeSchema(schema)
        schemaStr
    }
}