package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.datasource.{DataSourceConfig, DataSourceRegistry}
import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.dsl.parser.DSLSQLParser
import com.zhy.yisql.dsl.parser.DSLSQLParser._
import org.apache.spark.sql.streaming.DataStreamReader
import org.apache.spark.sql.{DataFrame, DataFrameReader}

import scala.language.reflectiveCalls

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-31
  *  \* Time: 21:41
  *  \* Description: 
  *  \*/
class LoadAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {

  def analyze(ctx: SqlContext): LoadStatement = {
    var format = ""
    var option = Map[String, String]()
    var path = ""
    var tableName = ""
    (0 until ctx.getChildCount).foreach { tokenIndex =>
      ctx.getChild(tokenIndex) match {
        case s: FormatContext =>
          val aliasV = formatAlias(s.getText)
          format = aliasV._1
          option = aliasV._2
        case s: ExpressionContext =>
          option += (cleanStr(s.qualifiedName().getText) -> evaluate(getStrOrBlockStr(s), scriptSQLExecListener.env()))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().qualifiedName().getText) -> evaluate(getStrOrBlockStr(s.expression()), scriptSQLExecListener.env))
        case s: PathContext =>
          path = evaluate(s.getText, scriptSQLExecListener.env())
        case s: TableNameContext =>
          tableName = evaluate(s.getText, scriptSQLExecListener.env())
        case _ =>
      }
    }
    LoadStatement(currentText(ctx), format, path, option, tableName)
  }

  override def parse(ctx: DSLSQLParser.SqlContext): Unit = {
    val LoadStatement(_, format, path, option, tableName) = analyze(ctx)
    log.info(option.toString)

    var table: DataFrame = null
    val dsConf = DataSourceConfig(cleanStr(path), option, Option(emptyDataFrame(scriptSQLExecListener.sparkSession)))

    //todo 优化
    DataSourceRegistry.fetch(format, option).map { datasource =>
      if (isStream) {
        table = datasource.asInstanceOf[ {def sLoad(reader: DataStreamReader, config: DataSourceConfig): DataFrame}].
          sLoad(scriptSQLExecListener.sparkSession.readStream, dsConf)
      } else {
        table = datasource.asInstanceOf[ {def bLoad(reader: DataFrameReader, config: DataSourceConfig): DataFrame}].
          bLoad(scriptSQLExecListener.sparkSession.read, dsConf)
      }
      table
    }.getOrElse {
      if (isStream) {
        throw new RuntimeException(s"load is not support with ${format}  in stream mode")
      }
      if (path == "-" || path.isEmpty) {
          table = scriptSQLExecListener.sparkSession.read.options(option).format(format).load()
      } else {
          table = scriptSQLExecListener.sparkSession.read.options(option).format(format)
            .load(resourceRealPath(scriptSQLExecListener, option.get("owner"), path))
      }
    }
    table.createOrReplaceTempView(tableName)
    scriptSQLExecListener.setLastSelectTable(tableName)
  }

  def isStream = {
    scriptSQLExecListener.env().contains("streamName")
  }

}

case class LoadStatement(raw: String, format: String, path: String, option: Map[String, String] = Map[String, String](), tableName: String)

