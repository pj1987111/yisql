package com.zhy.yisql.core.dsl.adaptor

import java.util.UUID

import com.zhy.yisql.core.cmds.{CmdRegister, SQLCmd}
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.dsl.parser.DSLSQLParser._
import com.zhy.yisql.runner.ScriptSQLExecListener

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 14:37
  *  \* Description: 
  *  \*/
class RunAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {
    def evaluate(value: String) = {
        TemplateMerge.merge(value, scriptSQLExecListener.env().toMap)
    }

    def analyze(ctx: SqlContext): RunStatement = {
        var tableName = ""
        var format = ""
        var path = ""
        var options = Map[String, String]()

        var asTableName = ""
        (0 until ctx.getChildCount).foreach { tokenIndex =>
            ctx.getChild(tokenIndex) match {
                case s: TableNameContext =>
                    tableName = evaluate(s.getText)
                case s: FormatContext =>
                    format = s.getText
                case s: PathContext =>
                    path = evaluate(cleanStr(s.getText))
                case s: ExpressionContext =>
                    options += (cleanStr(s.qualifiedName().getText) -> evaluate(getStrOrBlockStr(s)))
                case s: BooleanExpressionContext =>
                    options += (cleanStr(s.expression().qualifiedName().getText) -> evaluate(getStrOrBlockStr(s.expression())))
                case s: AsTableNameContext =>
                    asTableName = evaluate(cleanStr(s.tableName().getText))
                case _ =>
            }
        }
        RunStatement(currentText(ctx), tableName, format, path, options, asTableName)
    }

    override def parse(ctx: SqlContext): Unit = {
        val RunStatement(_, tableName, format, path, options, asTableName) = analyze(ctx)
        val owner = options.get("owner")
//        val df = scriptSQLExecListener.sparkSession.table(tableName)
        val runDf = RunAdaptor.findAlg(format).run(scriptSQLExecListener.sparkSession, path, options)

        val tempTable = if (asTableName.isEmpty) UUID.randomUUID().toString.replace("-", "") else asTableName
        runDf.createOrReplaceTempView(tempTable)
        scriptSQLExecListener.setLastSelectTable(tempTable)
    }
}

object RunAdaptor {
    def findAlg(name: String) = {
        CmdRegister.getMapping.get(name.capitalize) match {
            case Some(clzz) =>
                Class.forName(clzz).newInstance().asInstanceOf[SQLCmd]
            case None =>
                throw new RuntimeException(s"${name} is not found")
        }
    }
}

case class RunStatement(raw: String, inputTableName: String, etName: String, path: String, option: Map[String, String], outputTableName: String)
