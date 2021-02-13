package com.zhy.yisql.core.dsl.adaptor

import java.util.UUID

import com.zhy.yisql.core.datasource.{DataSinkConfig, DataSourceRegistry}
import com.zhy.yisql.core.dsl.{ScriptSQLExec, ScriptSQLExecListener}
import com.zhy.yisql.dsl.parser.DSLSQLParser
import com.zhy.yisql.dsl.parser.DSLSQLParser._
import org.apache.spark.sql.streaming.{DataStreamWriter, OutputMode, StreamingQuery}
import org.apache.spark.sql._

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-31
  *  \* Time: 21:41
  *  \* Description: 
  *  \*/
class SaveAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {
    val isStream: Boolean = isStream(scriptSQLExecListener.env)

    def analyze(ctx: SqlContext): SaveStatement = {
        var mode = if (isStream) OutputMode.Append else SaveMode.ErrorIfExists
        var format = ""
        var option = Map[String, String]()
        var tableName = ""
        var partitionByCol = ArrayBuffer[String]()
        var path = ""

        (0 until ctx.getChildCount).foreach { tokenIndex =>
            ctx.getChild(tokenIndex) match {
                case s: FormatContext =>
                    format = s.getText
                case s: PathContext =>
                    path = evaluate(cleanStr(s.getText), scriptSQLExecListener.env)
                case s: TableNameContext =>
                    tableName = evaluate(s.getText, scriptSQLExecListener.env)
                case s: OverwriteContext =>
                    mode = SaveMode.Overwrite
                case s: AppendContext =>
                    mode = if (isStream) OutputMode.Append else SaveMode.Append
                case s: ErrorIfExistsContext =>
                    mode = SaveMode.ErrorIfExists
                case s: IgnoreContext =>
                    mode = SaveMode.Ignore
                case s: CompleteContext =>
                    mode = OutputMode.Complete
                case s: UpdateContext =>
                    mode = OutputMode.Update
                case s: ColContext =>
                    partitionByCol += cleanStr(s.identifier().getText)
                case s: ColGroupContext =>
                    partitionByCol += cleanStr(s.col().identifier().getText)
                case s: ExpressionContext =>
                    option += (cleanStr(s.qualifiedName().getText) -> evaluate(getStrOrBlockStr(s), scriptSQLExecListener.env))
                case s: BooleanExpressionContext =>
                    option += (cleanStr(s.expression().qualifiedName().getText) -> evaluate(getStrOrBlockStr(s.expression()), scriptSQLExecListener.env))
                case _ =>
            }
        }
        SaveStatement(currentText(ctx), tableName, format, path, option, mode.toString, partitionByCol.toList)
    }

    override def parse(ctx: DSLSQLParser.SqlContext): Unit = {
        val SaveStatement(_, tableName, format, path, option, mode, partitionByCol) = analyze(ctx)
        val spark:SparkSession = scriptSQLExecListener.sparkSession
        val context = ScriptSQLExec.getContext
        //添加sql配置，可以过滤，etl等操作
        var df: DataFrame = option.get("etl.sql").map{sql=>
            spark.sql(sql)
        }.getOrElse{
            spark.table(tableName)
        }
        var streamQuery: StreamingQuery = null

        if (option.contains("fileNum")) {
            df = df.repartition(option.getOrElse("fileNum", "").toString.toInt)
        }

        val saveRes = DataSourceRegistry.fetch(format, option).map { datasource =>
            if (isStream) {
                val res = datasource.asInstanceOf[ {def sSave(writer: DataStreamWriter[Row], config: DataSinkConfig): Any}].sSave(
                    df.writeStream,
                    // here we should change final_path to path in future
                    DataSinkConfig(path, option, mode, Option(df)))
                res
            } else {
                val newOption = if (partitionByCol.nonEmpty) {
                    option ++ Map("partitionByCol" -> partitionByCol.mkString(","))
                } else option

                val res = datasource.asInstanceOf[ {def bSave(writer: DataFrameWriter[Row], config: DataSinkConfig): Any}].bSave(
                    df.write,
                    // here we should change final_path to path in future
                    DataSinkConfig(path, newOption, mode, Option(df)))
                res
            }
        }.getOrElse {
            if (isStream) {
                throw new RuntimeException(s"save is not support with ${format}  in stream mode")
            }
            val writer = df.write
            if (partitionByCol.nonEmpty) {
                writer.partitionBy(partitionByCol: _*)
            }
            writer.mode(mode)
            if (path == "-" || path.isEmpty) {
                writer.format(option.getOrElse("implClass", format)).save()
            }
            else {
                writer.format(option.getOrElse("implClass", format)).save(resourceRealPath(context.execListener, Option(context.owner), path))
            }
        }

        if (isStream) {
            streamQuery = saveRes.asInstanceOf[StreamingQuery]
        }
        //next todo put in job manager...

        val tempTable = UUID.randomUUID().toString.replace("-", "")
        val outputTable = emptyDataFrame(spark)
        outputTable.createOrReplaceTempView(tempTable)
        scriptSQLExecListener.setLastSelectTable(tempTable)
    }
}

case class SaveStatement(raw: String, inputTableName: String, format: String, path: String, option: Map[String, String] = Map(), mode: String, partitionByCol: List[String])

