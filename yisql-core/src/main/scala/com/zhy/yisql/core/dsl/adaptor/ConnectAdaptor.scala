package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.core.execute.ConnectMeta
import com.zhy.yisql.dsl.parser.DSLSQLParser._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-08
  *  \* Time: 23:35
  *  \* Description: 
  *  \*/
class ConnectAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {

  def evaluate(value: String) = {
    TemplateMerge.merge(value, scriptSQLExecListener.env().toMap)
  }

  def analyze(ctx: SqlContext): ConnectStatement = {
    var option = Map[String, String]()
    var format = ""
    var formatAlias = ""

    (0 until ctx.getChildCount).foreach { tokenIndex =>
      ctx.getChild(tokenIndex) match {
        //实际使用format
        case s: FormatContext =>
          format = s.getText
        case s: ExpressionContext =>
          option += (cleanStr(s.qualifiedName().getText) -> evaluate(getStrOrBlockStr(s)))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().qualifiedName().getText) -> evaluate(getStrOrBlockStr(s.expression())))
        case s: DbContext =>
          //format别名 todo 别名不能和已有format重复
          formatAlias = s.getText
          ConnectMeta.options(formatAlias, format, option)
        case _ =>
      }
    }
    ConnectStatement(currentText(ctx), format, option)
  }

  override def parse(ctx: SqlContext): Unit = {
    scriptSQLExecListener.setLastSelectTable(null)
  }
}

case class ConnectStatement(raw: String, format: String, option: Map[String, String])
