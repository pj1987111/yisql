package com.zhy.yisql.core.dsl.processor

import java.util.concurrent.atomic.AtomicReference

import com.zhy.yisql.core.dsl.adaptor._
import com.zhy.yisql.core.execute.{BranchContext, BranchContextHolder, PathPrefix}
import com.zhy.yisql.core.job.JobProgressListener
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import com.zhy.yisql.dsl.parser.{DSLSQLBaseListener, DSLSQLLexer}
import org.antlr.v4.runtime.misc.Interval
import org.apache.spark.sql.SparkSession

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 19:04
  *  \* Description: 
  *  \*/
/**
  *
  * @param _sparkSession
  * @param _pathPrefix 多租户路径
  */
class ScriptSQLExecListener(val _sparkSession: SparkSession, val _pathPrefix: PathPrefix) extends DSLSQLBaseListener {

  private val _branchContext = BranchContextHolder(new mutable.Stack[BranchContext](), new ArrayBuffer[String]())
  //全局环境变量
  private val _env = new scala.collection.mutable.HashMap[String, String]

  private[this] val _jobProgressListeners = ArrayBuffer[JobProgressListener]()

  private val lastSelectTable = new AtomicReference[String]()
  private val _declaredTables = new ArrayBuffer[String]()

  var cmdParserListener: Option[CmdParserListener] = None

  def branchContext = {
    _branchContext
  }

  def declaredTables = _declaredTables

  def addJobProgressListener(l: JobProgressListener) = {
    _jobProgressListeners += l
    this
  }

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

  def clone(sparkSession: SparkSession): ScriptSQLExecListener = {
    val ssel = new ScriptSQLExecListener(sparkSession, new PathPrefix(_pathPrefix._defaultPathPrefix, _pathPrefix._allPathPrefix))
    _env.foreach { case (a, b) => ssel.addEnv(a, b) }

    ssel.cmdParserListener = cmdParserListener

    ssel
  }

  override def exitSql(ctx: SqlContext): Unit = {
    def getText = {
      val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input

      val start = ctx.start.getStartIndex()
      val stop = ctx.stop.getStopIndex()
      val interval = new Interval(start, stop)
      input.getText(interval)
    }

    def before(clzz: String) = {
      _jobProgressListeners.foreach(_.before(clzz, getText))
    }

    def after(clzz: String) = {
      _jobProgressListeners.foreach(_.after(clzz, getText))
    }

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

    before(PREFIX)
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

      case "connect" =>
        val adaptor = new ConnectAdaptor(this)
        execute(adaptor)

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

      case "run" =>
        val adaptor = new RunAdaptor(this)
        execute(adaptor)

      //            case "train" | "run" | "predict" =>
      //                val adaptor = new TrainAdaptor(this)
      //                execute(adaptor)
      case "register" =>
        print(1)
      //                val adaptor = new RegisterAdaptor(this)
      //                execute(adaptor)
      case _ => throw new RuntimeException(s"Unknow statement:${ctx.getText}")
    }
    //        after(PREFIX)
  }
}