package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.core.execute.ConnectMeta
import com.zhy.yisql.dsl.parser.DSLSQLLexer
import com.zhy.yisql.dsl.parser.DSLSQLParser.{ExpressionContext, SqlContext}
import org.antlr.v4.runtime.misc.Interval
import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-31
  *  \* Time: 21:31
  *  \* Description: 
  *  \*/
trait DslAdaptor extends DslTool with Logging {
    def parse(ctx: SqlContext): Unit
}

trait DslTool {

    def evaluate(value: String, env: scala.collection.mutable.HashMap[String, String]):String = {
        TemplateMerge.merge(value, env.toMap)
    }

    def emptyDataFrame(sparkSession: SparkSession) = {
        sparkSession.emptyDataFrame
//        import sparkSession.implicits._
//        Seq.empty[String].toDF("name")
    }

    def isStream(env: scala.collection.mutable.HashMap[String, String]) = {
        env.contains("streamName")
    }

//    def branchContext = {
//        ScriptSQLExec.context().execListener.branchContext.contexts
//    }

    def currentText(ctx: SqlContext) = {
        val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input

        val start = ctx.start.getStartIndex()
        val stop = ctx.stop.getStopIndex()
        val interval = new Interval(start, stop)
        input.getText(interval)
    }

    def cleanStr(str: String) = {
        if (str.startsWith("`") || str.startsWith("\"") || (str.startsWith("'") && !str.startsWith("'''")))
            str.substring(1, str.length - 1)
        else str
    }


    def cleanBlockStr(str: String) = {
        if(str.startsWith("'''\n") && str.endsWith("\n'''"))
            str.substring(4, str.length - 4)
        else if (str.startsWith("'''") && str.endsWith("'''"))
            str.substring(3, str.length - 3)
        else str
    }

    def getStrOrBlockStr(ec: ExpressionContext) = {
        if (ec.STRING() == null || ec.STRING().getText.isEmpty) {
            cleanBlockStr(ec.BLOCK_STRING().getText)
        } else {
            cleanStr(ec.STRING().getText)
        }
    }

    def withPathPrefix(prefix: String, path: String): String = {

        val newPath = cleanStr(path)
        if (prefix.isEmpty) return newPath

        if (path.contains("..")) {
            throw new RuntimeException("path should not contains ..")
        }
        if (path.startsWith("/")) {
            return prefix + path.substring(1, path.length)
        }
        return prefix + newPath

    }

    def parseDBAndTableFromStr(str: String) = {
        val cleanedStr = cleanStr(str)
        val dbAndTable = cleanedStr.split("\\.")
        if (dbAndTable.length > 1) {
            val db = dbAndTable(0)
            val table = dbAndTable.splitAt(1)._2.mkString(".")
            (db, table)
        } else {
            (cleanedStr, cleanedStr)
        }

    }

    def resourceRealPath(scriptSQLExecListener: ScriptSQLExecListener,
                         resourceOwner: Option[String],
                         path: String): String = {
        withPathPrefix(scriptSQLExecListener.pathPrefix.pathPrefix(resourceOwner), cleanStr(path))
    }

//    def parseRef(format: String, path: String, separator: String, callback: Map[String, String] => Unit) = {
//        var finalPath = path
//        var dbName = ""
//
//        val firstIndex = finalPath.indexOf(separator)
//
//        if (firstIndex > 0) {
//
//            dbName = finalPath.substring(0, firstIndex)
//            finalPath = finalPath.substring(firstIndex + 1)
//            ConnectMeta.presentThenCall(DBMappingKey(format, dbName), options => callback(options))
//        }
//
//        Array(dbName, finalPath)
//    }

    /**
      *
      * @param format 新或旧format(别名)
      * @return 原始format  原始option
      */
    def formatAlias(format:String): (String, Map[String, String]) = {
        ConnectMeta.options(format).getOrElse((format, Map[String, String]()))
    }

    def mergeOptions(optionTemplate: Map[String, String], thisOption: Map[String, String]): Map[String, String] = {
        var params = optionTemplate
        if(thisOption!=null) {
            for (params2_entry <- thisOption) {
                params += (params2_entry._1 -> params2_entry._2)
            }
        }
        params
    }
}
