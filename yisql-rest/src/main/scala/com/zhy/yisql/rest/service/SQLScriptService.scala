package com.zhy.yisql.rest.service

import com.zhy.yisql.core.job.JobManager
import com.zhy.yisql.rest.entity.SQLRunEntity
import org.springframework.stereotype.Service

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 11:07
  *  \* Description: 
  *  \*/
@Service
class SQLScriptService {
//    def run(sqlRunEntity: SQLRunEntity): Unit = {
//        var outputResult: String = if (sqlRunEntity.includeSchema) "{}" else "[]"
//        try {
//            val jobInfo = JobManager.getJobInfo(
//                sqlRunEntity.owner, sqlRunEntity.jobType, sqlRunEntity.jobName, sqlRunEntity.sql,
//                sqlRunEntity.timeout
//            )
//            val context = createScriptSQLExecListener(sparkSession, jobInfo.groupId)
//
//            def query = {
//                if (paramAsBoolean("async", false)) {
//                    JobManager.asyncRun(sparkSession, jobInfo, () => {
//                        try {
//                            ScriptSQLExec.parse(param("sql"), context,
//                                skipInclude = paramAsBoolean("skipInclude", false),
//                                skipAuth = paramAsBoolean("skipAuth", true),
//                                skipPhysicalJob = paramAsBoolean("skipPhysicalJob", false),
//                                skipGrammarValidate = paramAsBoolean("skipGrammarValidate", true))
//
//                            outputResult = getScriptResult(context, sparkSession)
//                            htp.post(new Url(param("callback")),
//                                Map("stat" -> s"""succeeded""",
//                                    "res" -> outputResult,
//                                    "jobInfo" -> JSONTool.toJsonStr(jobInfo)))
//                        } catch {
//                            case e: Exception =>
//                                e.printStackTrace()
//                                val msgBuffer = ArrayBuffer[String]()
//                                if (paramAsBoolean("show_stack", false)) {
//                                    format_full_exception(msgBuffer, e)
//                                }
//                                htp.post(new Url(param("callback")),
//                                    Map("stat" -> s"""failed""",
//                                        "msg" -> (e.getMessage + "\n" + msgBuffer.mkString("\n")),
//                                        "jobInfo" -> JSONTool.toJsonStr(jobInfo)
//                                    ))
//                        }
//                    })
//                } else {
//                    JobManager.run(sparkSession, jobInfo, () => {
//                        ScriptSQLExec.parse(param("sql"), context,
//                            skipInclude = paramAsBoolean("skipInclude", false),
//                            skipAuth = paramAsBoolean("skipAuth", true),
//                            skipPhysicalJob = paramAsBoolean("skipPhysicalJob", false),
//                            skipGrammarValidate = paramAsBoolean("skipGrammarValidate", true)
//                        )
//                        if (!silence) {
//                            outputResult = getScriptResult(context, sparkSession)
//                        }
//                    })
//                }
//            }
//
//            def analyze = {
//                ScriptSQLExec.parse(param("sql"), context,
//                    skipInclude = false,
//                    skipAuth = true,
//                    skipPhysicalJob = true,
//                    skipGrammarValidate = true)
//                context.preProcessListener.map(f => JSONTool.toJsonStr(f.analyzedStatements.map(_.unwrap))) match {
//                    case Some(i) => outputResult = i
//                    case None =>
//                }
//            }
//
//            params.getOrDefault("executeMode", "query") match {
//                case "query" => query
//                case "analyze" => analyze
//                case executeMode: String =>
//                    AppRuntimeStore.store.getController(executeMode) match {
//                        case Some(item) =>
//                            outputResult = Class.forName(item.customClassItem.className).
//                                    newInstance().asInstanceOf[CustomController].run(params().toMap + ("__jobinfo__" -> JSONTool.toJsonStr(jobInfo)))
//                        case None => throw new RuntimeException(s"no executeMode named ${executeMode}")
//                    }
//            }
//
//        } catch {
//            case e: Exception =>
//                val msg = ExceptionRenderManager.call(e)
//                render(500, msg.str.get)
//        } finally {
//            RequestCleanerManager.call()
//            cleanActiveSessionInSpark
//        }
//    }
}
