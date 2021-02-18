package com.zhy.yisql.core.job

import com.zhy.yisql.core.cmds.CommandCollection
import com.zhy.yisql.core.dsl.CaseChangingCharStream
import com.zhy.yisql.core.dsl.processor.{CmdParserListener, ScriptSQLExecListener}
import com.zhy.yisql.dsl.parser.{DSLSQLLexer, DSLSQLListener, DSLSQLParser}
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-31
  *  \* Time: 21:23
  *  \* Description: 
  * sql运行上下文
  *  \*/
object SQLExecContext {

    private[this] val yisqlExecuteContext: ThreadLocal[ExecuteContext] = new ThreadLocal[ExecuteContext]

    private def _context(): ExecuteContext = yisqlExecuteContext.get

    def getContext(): ExecuteContext = {
        if (_context() == null) {
            //            logError("If this is not unit test, then it may be  something wrong. context should not be null")
            val exec = new ScriptSQLExecListener(null, new PathPrefix("/tmp/zhy", Map()))
            setContext(ExecuteContext(exec, "zhy", "", Map()))
        }
        _context()
    }

    def setContext(ec: ExecuteContext): Unit = yisqlExecuteContext.set(ec)

    def setContextIfNotPresent(ec: ExecuteContext): Unit = {
        if (SQLExecContext._context() == null) {
            yisqlExecuteContext.set(ec)
        }
    }

    def unset = yisqlExecuteContext.remove()

    def parse(input: String, listener: DSLSQLListener): Unit = {
        var cmd = input

        val sqel = listener.asInstanceOf[ScriptSQLExecListener]
        //命令注册
        CommandCollection.fill(sqel)

        val preProcessListener = new CmdParserListener(sqel)
        sqel.cmdParserListener = Some(preProcessListener)
        _parse(cmd, preProcessListener)
        cmd = preProcessListener.toScript

        _parse(cmd, listener)
    }

    def _parse(input: String, listener: DSLSQLListener) = {
        val loadLexer = new DSLSQLLexer(new CaseChangingCharStream(input))
        val tokens = new CommonTokenStream(loadLexer)
        val parser = new DSLSQLParser(tokens)

        val stat = parser.statement()
        ParseTreeWalker.DEFAULT.walk(listener, stat)
    }
}

case class BranchContextHolder(contexts: mutable.Stack[BranchContext], traces: ArrayBuffer[String])

trait BranchContext

//case class IfContext(sqls: mutable.ArrayBuffer[DslAdaptor],
//                     ctxs: mutable.ArrayBuffer[SqlContext],
//                     variableTable: VariableTable,
//                     shouldExecute: Boolean,
//                     haveMatched: Boolean,
//                     skipAll: Boolean) extends BranchContext

case class ForContext() extends BranchContext

/**
  *
  * @param execListener     sql执行上下文
  * @param owner            所属用户
  * @param userDefinedParam 用户定义变量
  */
case class ExecuteContext(@transient execListener: ScriptSQLExecListener,
                               owner: String,
                               groupId: String,
                               userDefinedParam: Map[String, String] = Map()
                              )

class PathPrefix(val _defaultPathPrefix: String, val _allPathPrefix: Map[String, String]) {
    def pathPrefix(owner: Option[String]): String = {

        if (_allPathPrefix != null && _allPathPrefix.nonEmpty && owner.isDefined) {
            val pathPrefix = _allPathPrefix.get(owner.get)
            if (pathPrefix.isDefined && pathPrefix.get.endsWith("/")) {
                return pathPrefix.get
            } else if (pathPrefix.isDefined && !pathPrefix.get.endsWith("/")) {
                return pathPrefix.get + "/"
            }
        }

        if (_defaultPathPrefix != null && _defaultPathPrefix.nonEmpty) {
            if (_defaultPathPrefix.endsWith("/")) {
                return _defaultPathPrefix
            } else {
                return _defaultPathPrefix + "/"
            }
        } else {
            return ""
        }
    }
}