package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.dsl.ScriptSQLExecListener
import com.zhy.yisql.core.util.shell.ShellCommand
import com.zhy.yisql.dsl.parser.DSLSQLParser._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-04
  *  \* Time: 21:16
  *  \* Description: 
  *  \*/
class SetAdaptor(scriptSQLExecListener : ScriptSQLExecListener) extends DslAdaptor {
    def analyze(ctx: SqlContext): SetStatement = {
        var key = ""
        var value = ""
        var command = ""
        var original_command = ""
        var option = Map[String, String]()
        (0 until ctx.getChildCount).foreach { tokenIndex =>

            ctx.getChild(tokenIndex) match {
                case s: SetKeyContext =>
                    key = s.getText
                case s: SetValueContext =>
                    original_command = s.getText
                    if (s.quotedIdentifier() != null && s.quotedIdentifier().BACKQUOTED_IDENTIFIER() != null) {
                        command = cleanStr(s.getText)
                    } else if (s.qualifiedName() != null && s.qualifiedName().identifier() != null) {
                        command = cleanStr(s.getText)
                    }
                    else {
                        command = original_command
                    }
                case s: ExpressionContext =>
                    option += (cleanStr(s.qualifiedName().getText) -> getStrOrBlockStr(s))
                case s: BooleanExpressionContext =>
                    option += (cleanStr(s.expression().qualifiedName().getText) -> getStrOrBlockStr(s.expression()))
                case _ =>
            }
        }
        SetStatement(currentText(ctx), key, command, original_command, option, option.getOrElse("type", ""))
    }

    override def parse(ctx: SqlContext): Unit = {
        val SetStatement(_, key, command, original_command, option, mode) = analyze(ctx)
        var value = ""

        def doRealJob(command: String): String = {
            val df = scriptSQLExecListener.sparkSession.sql(command)

            val resultHead = df.collect().headOption
            if (resultHead.isDefined) {
                resultHead.get.get(0).toString
            } else {
                ""
            }
        }

        var overwrite = true
        option.get("type") match {
            case Some("sql") =>
                doRealJob(command)
            case Some("shell") =>
                value = ShellCommand.execCmd(command).trim
            case Some("conf") =>
                key match {
                    case "spark.scheduler.pool" =>
                        scriptSQLExecListener.sparkSession
                                .sqlContext
                                .sparkContext
                                .setLocalProperty(key, original_command)
                    case _ =>
                        scriptSQLExecListener.sparkSession.sql(s""" set ${key} = ${original_command} """)
                }
            case _ =>
                value = cleanBlockStr(cleanStr(command))
        }

        scriptSQLExecListener.addEnv(key, value)
    }
}

case class SetStatement(raw: String, key: String, command: String, original_command: String, option: Map[String, String], mode: String)
