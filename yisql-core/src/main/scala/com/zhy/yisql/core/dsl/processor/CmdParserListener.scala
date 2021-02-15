package com.zhy.yisql.core.dsl.processor

import com.zhy.yisql.core.dsl.adaptor.{CommandAdaptor, SetAdaptor, SingleStatement, StatementAdaptor}
import com.zhy.yisql.dsl.parser.DSLSQLBaseListener
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import com.zhy.yisql.runner.ScriptSQLExecListener

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 10:39
  *  \* Description: 
  * 特殊命令转换解析器
  *  \*/
class CmdParserListener(val scriptSQLExecListener: ScriptSQLExecListener) extends DSLSQLBaseListener {
    private val _statements = new ArrayBuffer[String]()
    private val _singleStatements = new ArrayBuffer[SingleStatement]()

    def toScript = {
//        scriptSQLExecListener.addEnv(MLSQLEnvKey.CONTEXT_STATEMENT_NUM, _statements.length.toString)
        _statements.mkString(";") + ";"
    }

    def statements = {
        _statements
    }

    def analyzedStatements = {
        _singleStatements
    }

    def addStatement(v: String) = {
        _statements += v
        this
    }

    def addSingleStatement(v: SingleStatement) = {
        _singleStatements += v
        this
    }

    override def exitSql(ctx: SqlContext): Unit = {

        ctx.getChild(0).getText.toLowerCase() match {
            case item if item.startsWith("!") =>
                new CommandAdaptor(this).parse(ctx)
                new StatementAdaptor(this, (raw) => {}).parse(ctx)
            case "set" => {
                new SetAdaptor(scriptSQLExecListener).parse(ctx)
                new StatementAdaptor(this, (raw) => {
                    addStatement(raw)
                }).parse(ctx)
            }
            case _ => new StatementAdaptor(this, (raw) => {
                addStatement(raw)
            }).parse(ctx)
        }

    }
}
