package com.zhy.yisql.core.dsl

import java.util.concurrent.atomic.AtomicReference

import com.zhy.yisql.core.dsl.adaptor.{CreateAdaptor, DslAdaptor, InsertAdaptor, LoadAdaptor, SaveAdaptor, SelectAdaptor, SetAdaptor}
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import com.zhy.yisql.dsl.parser.{DSLSQLBaseListener, DSLSQLLexer, DSLSQLListener, DSLSQLParser}
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-31
  *  \* Time: 21:23
  *  \* Description: 
  *  \*/
object ScriptSQLExec {

    private[this] val mlsqlExecuteContext: ThreadLocal[ExecuteContext] = new ThreadLocal[ExecuteContext]

    private def _context(): ExecuteContext = mlsqlExecuteContext.get

    def getContext(): ExecuteContext = {
        if (_context() == null) {
            //            logError("If this is not unit test, then it may be  something wrong. context should not be null")
            val exec = new ScriptSQLExecListener(null, new PathPrefix("/tmp/zhy", Map()))
            setContext(ExecuteContext(exec, "zhy", Map()))
        }
        _context()
    }

    def setContext(ec: ExecuteContext): Unit = mlsqlExecuteContext.set(ec)

    def setContextIfNotPresent(ec: ExecuteContext): Unit = {
        if (ScriptSQLExec._context() == null) {
            mlsqlExecuteContext.set(ec)
        }
    }

    def unset = mlsqlExecuteContext.remove()

    def parse(input: String, listener: DSLSQLListener): Unit = {
        _parse(input, listener)
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
  * @param _sparkSession
  * @param _pathPrefix 多租户路径
  */
class ScriptSQLExecListener(val _sparkSession: SparkSession, val _pathPrefix: PathPrefix) extends DSLSQLBaseListener {

    private val _branchContext = BranchContextHolder(new mutable.Stack[BranchContext](), new ArrayBuffer[String]())
    //全局环境变量
    private val _env = new scala.collection.mutable.HashMap[String, String]
    private val lastSelectTable = new AtomicReference[String]()
    private val _declaredTables = new ArrayBuffer[String]()

    def branchContext = {
        _branchContext
    }

    def declaredTables = _declaredTables

    def setLastSelectTable(table: String) = {
        if (table != null) {
            _declaredTables += table
        }
        lastSelectTable.set(table)
    }

    def getLastSelectTable() = {
        Option(lastSelectTable.get())
    }

    def addEnv(k: String, v: String) = {
        _env(k) = v
        this
    }

    def env() = _env

    def sparkSession = _sparkSession

    def pathPrefix = _pathPrefix

    override def exitSql(ctx: SqlContext): Unit = {
        def getText = {
            val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input

            val start = ctx.start.getStartIndex()
            val stop = ctx.stop.getStopIndex()
            val interval = new Interval(start, stop)
            input.getText(interval)
        }

        //        def before(clzz: String) = {
        //            _jobListeners.foreach(_.before(clzz, getText))
        //        }
        //
        //        def after(clzz: String) = {
        //            _jobListeners.foreach(_.after(clzz, getText))
        //        }
        //
        //        def traceBC = {
        //            ScriptSQLExec.context().execListener.env().getOrElse("__debug__","false").toBoolean
        //        }

        def str(ctx: SqlContext) = {

            val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input

            val start = ctx.start.getStartIndex()
            val stop = ctx.stop.getStopIndex()
            val interval = new Interval(start, stop)
            input.getText(interval)
        }

        def execute(adaptor: DslAdaptor) = {
            val bc = branchContext.contexts
            if (!bc.isEmpty) {
                //                bc.pop() match {
                //                    case ifC: IfContext =>
                //                        val isBranchCommand = adaptor match {
                //                            case a: TrainAdaptor =>
                //                                val TrainStatement(_, _, format, _, _, _) = a.analyze(ctx)
                //                                val isBranchCommand = (format == "IfCommand"
                //                                        || format == "ElseCommand"
                //                                        || format == "ElifCommand"
                //                                        || format == "FiCommand"
                //                                        || format == "ThenCommand")
                //                                isBranchCommand
                //                            case _ => false
                //                        }
                //
                //                        if (ifC.skipAll) {
                //                            bc.push(ifC)
                //                            if(isBranchCommand){
                //                                adaptor.parse(ctx)
                //                            }
                //                        } else {
                //                            if (ifC.shouldExecute && !isBranchCommand) {
                //                                ifC.sqls += adaptor
                //                                ifC.ctxs += ctx
                //                                bc.push(ifC)
                //                            } else if (!ifC.shouldExecute && !isBranchCommand) {
                //                                bc.push(ifC)
                //                                // skip
                //                            }
                //                            else {
                //                                bc.push(ifC)
                //                                adaptor.parse(ctx)
                //                            }
                //                        }
                //                    case forC: ForContext =>
                //                }
            } else {
                //                if(traceBC) {
                //                    logInfo(format(s"SQL:: ${str(ctx)}"))
                //                }
                adaptor.parse(ctx)
            }
        }

        val PREFIX = ctx.getChild(0).getText.toLowerCase()

        //        before(PREFIX)
        PREFIX match {
            case "load" =>
                val adaptor = new LoadAdaptor(this)
                execute(adaptor)

            case "select" =>
                val adaptor = new SelectAdaptor(this)
                execute(adaptor)

            case "save" =>
                val adaptor = new SaveAdaptor(this)
                execute(adaptor)
            //            case "connect" =>
            //                val adaptor = new ConnectAdaptor(this)
            //                execute(adaptor)
            case "create" =>
                val adaptor = new CreateAdaptor(this)
                execute(adaptor)
            case "insert" =>
                val adaptor = new InsertAdaptor(this)
                execute(adaptor)
            //            case "drop" =>
            //                val adaptor = new DropAdaptor(this)
            //                execute(adaptor)
            //            case "refresh" =>
            //                val adaptor = new RefreshAdaptor(this)
            //                execute(adaptor)
            case "set" =>
                val adaptor = new SetAdaptor(this)
                execute(adaptor)
            //            case "train" | "run" | "predict" =>
            //                val adaptor = new TrainAdaptor(this)
            //                execute(adaptor)
            //            case "register" =>
            //                val adaptor = new RegisterAdaptor(this)
            //                execute(adaptor)
            case _ => throw new RuntimeException(s"Unknow statement:${ctx.getText}")
        }
        //        after(PREFIX)
    }
}

/**
  *
  * @param execListener     sql执行上下文
  * @param owner            所属用户
  * @param userDefinedParam 用户定义变量
  */
case class ExecuteContext(@transient execListener: ScriptSQLExecListener,
                          owner: String,
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