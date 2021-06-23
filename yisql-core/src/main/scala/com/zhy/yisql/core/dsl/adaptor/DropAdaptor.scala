package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.dsl.parser.DSLSQLLexer
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.misc.Interval

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-12
 *  \* Time: 21:41
 *  \* Description: 
 * hive 删除
 *  \ */
class DropAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {
  def analyze(ctx: SqlContext): DropStatement = {
    val input: CharStream = ctx.start.getTokenSource.asInstanceOf[DSLSQLLexer]._input
    val start: Int = ctx.start.getStartIndex
    val stop: Int = ctx.stop.getStopIndex
    val interval = new Interval(start, stop)
    val originalText: String = input.getText(interval)
    val sql: String = TemplateMerge.merge(originalText, scriptSQLExecListener.env().toMap)
    DropStatement(originalText, sql)
  }

  override def parse(ctx: SqlContext): Unit = {
    val DropStatement(_, sql) = analyze(ctx)
    scriptSQLExecListener.sparkSession.sql(sql).count()
    scriptSQLExecListener.setLastSelectTable(null)
  }
}

case class DropStatement(raw: String, sql: String)
