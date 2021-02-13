package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.dsl.ScriptSQLExecListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.dsl.parser.DSLSQLLexer
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import org.antlr.v4.runtime.misc.Interval

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-05
  *  \* Time: 12:03
  *  \* Description: 
  *  \*/
class SelectAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {

    def analyze(ctx: SqlContext): SelectStatement = {
        val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input

        val start = ctx.start.getStartIndex()
        val stop = ctx.stop.getStopIndex()
        val interval = new Interval(start, stop)
        var text = input.getText(interval)

//        val envScope = scriptSQLExecListener.envScope
//                .filter(!_._2.scope.contains(ParameterScope.UN_SELECT))
//                .mapValues(_.value)
//                .toMap
//
        text = TemplateMerge.merge(text, scriptSQLExecListener.env.toMap)

        val chunks = text.split("\\s+")
        val tableName = chunks.last.replace(";", "")
        val sql = try {
            text.replaceAll(s"((?i)as)[\\s|\\n]+${tableName}\\s*\\n*$$", "")
        } catch {
            case e: Exception =>
                text.split("(?i)as").dropRight(1).mkString("as")
        }

        SelectStatement(text, sql, tableName)

    }

    override def parse(ctx: SqlContext): Unit = {

        val SelectStatement(originalText, sql, tableName) = analyze(ctx)

        val df = scriptSQLExecListener.sparkSession.sql(sql)

        df.createOrReplaceTempView(tableName)
        scriptSQLExecListener.setLastSelectTable(tableName)
    }

}

case class SelectStatement(raw: String, sql: String, tableName: String)
