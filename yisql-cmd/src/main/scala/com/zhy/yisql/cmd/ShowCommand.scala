package com.zhy.yisql.cmd

import com.zhy.yisql.core.cmds.{CommandCollection, SQLCmd}
import com.zhy.yisql.core.execute.SQLExecuteContext
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-03
  *  \* Time: 14:51
  *  \* Description: 
  *  \*/
class ShowCommand extends SQLCmd {
    override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
        def cleanPath = {
            path.split("/").filterNot(f => f.isEmpty)
        }

        def help = {
            val context = SQLExecuteContext.getContext()
            context.execListener.addEnv("__show_help_content__",
                """
                  |command
                  |!show jobs;
                  |!show job [groupId/name]
                  |!show datasources;
                  |!show "datasources/params/[datasource name]";
                  |!show resource;
                  |!show "resource/[groupId]";
                  |!show "progress/[groupId]";
                  |!show et;
                  |!show et [ETName];
                  |!show functions;
                  |!show function [functionName];
                  |!show tables;
                  |!show tables from [database];
                """.stripMargin)

            s"""
               |set __show_help_content__='''
               |${context.execListener.env()("__show_help_content__")}
               |''';
               |load csvStr.`__show_help_content__` where header="true" as __show_help_content__;
         """.stripMargin
        }

        val newPath = cleanPath
        val sql = newPath match {
            case Array("et", name) => s"load modelExample.`${name}` as __output__;"
            case Array("et") => s"load modelList.`` as __output__;"
            case Array("functions") => s"run command as ShowFunctionsExt.``;"
            case Array("function", name) => s"run command as ShowFunctionsExt.`${name}`;"
            case Array("tables") => s"run command as ShowTablesExt.``;"
            case Array("tables" ,"named" ,aliasName) => s"run command as ShowTablesExt.`` as ${aliasName};"
            case Array("tables" ,"from" ,name) => s"run command as ShowTablesExt.`${name}`;"
            case Array("tables" ,"from" ,name ,"named" ,aliasName) => s"run command as ShowTablesExt.`` as ${aliasName};"
            case Array("jobs") => s"run command as ShowJobsExt.``;"
            case Array("job", name) => s"run command as ShowJobsExt.`${name}`;"
            case Array("commands") | Array() | Array("help") | Array("-help") =>
                help
            case _ => s"load _yisql_.`${newPath.mkString("/")}` as output;"
        }
        CommandCollection.evaluateYiSQL(spark, sql)
    }

}
