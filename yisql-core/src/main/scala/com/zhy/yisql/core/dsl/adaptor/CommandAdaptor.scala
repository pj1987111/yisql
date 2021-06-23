package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.common.utils.base.Templates
import com.zhy.yisql.core.dsl.processor.CmdParserListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.dsl.parser.DSLSQLParser
import com.zhy.yisql.dsl.parser.DSLSQLParser.{CommandValueContext, RawCommandValueContext, SqlContext}
import org.antlr.v4.runtime.tree.ParseTree

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class CommandAdaptor(cmdParserListener: CmdParserListener) extends DslAdaptor {

  def evaluate(str: String): String = {
    TemplateMerge.merge(str, cmdParserListener.scriptSQLExecListener.env().toMap)
  }

  def analyze(ctx: SqlContext): CommandStatement = {
    var command = ""
    val parameters: ArrayBuffer[String] = ArrayBuffer[String]()
    command = ctx.getChild(0).getText.substring(1)


    (0 until ctx.getChildCount).foreach { tokenIndex: Int =>
      ctx.getChild(tokenIndex) match {
        case s: CommandValueContext =>
          val oringinalText: String = s.getText
          parameters += cleanBlockStr(cleanStr(evaluate(oringinalText)))
        case s: RawCommandValueContext =>
          val box: ArrayBuffer[String] = ArrayBuffer[String]()
          var prePos = 0
          (0 until s.getChildCount).foreach { itokenIndex: Int =>
            val child: ParseTree = s.getChild(itokenIndex)
            if (itokenIndex == 0 || (child.getSourceInterval.a - prePos == 1)) {
              box += child.getText
            } else {
              parameters += box.mkString("")
              box.clear()
              box += child.getText
            }
            prePos = child.getSourceInterval.b
          }

          parameters += box.mkString("")
          box.clear()

        case _ =>
      }
    }
    CommandStatement(currentText(ctx), command, parameters.toList)
  }

  override def parse(ctx: DSLSQLParser.SqlContext): Unit = {
    val CommandStatement(_, _command, _parameters) = analyze(ctx)
    val command: String = _command
    val parameters: ArrayBuffer[String] = new ArrayBuffer[String]() ++ _parameters
    val env: mutable.Map[String, String] = cmdParserListener.scriptSQLExecListener.env()
    val tempCommand: String = try env(command) catch {
      case _: java.util.NoSuchElementException =>
        throw new RuntimeException(s"Command `${command}` is not found.")
      case e: Exception => throw e
    }
    val renderContent: String = Templates.evaluate(tempCommand, parameters)
    cmdParserListener.addStatement(renderContent)
  }
}

case class CommandStatement(raw: String, command: String, parameters: List[String])
