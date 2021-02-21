package com.zhy.yisql.rest.controller

import com.zhy.yisql.common.utils.bean.BeanUtils
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.core.execute.SQLExecute
import com.zhy.yisql.core.job.JobManager
import com.zhy.yisql.rest.entity.{KillJobEntity, SQLRunEntity}
import com.zhy.yisql.rest.service.SQLScriptService
import org.apache.spark.SparkInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 20:45
  *  \* Description: 
  *  \*/
@RestController
class SQLScriptController extends Logging {
    @Autowired
    var service: SQLScriptService = _

    @RequestMapping(value = Array("/test"))
    def test = "sql test ok"

    @RequestMapping(value = Array("/sql/run"))
    def run(sqlRunEntity: SQLRunEntity): String = {
        val params: Map[String, String] = BeanUtils.getCCParams(sqlRunEntity)
        logInfo(s"/sql/run method params is: $params")
        val executor = new SQLExecute(params)
        val res = executor.simpleExecute()
        res._2
    }

    @RequestMapping(value = Array("/job/list"))
    def listJobs(): String = {
        val infoMap = JobManager.getJobInfo
        JSONTool.toJsonStr(infoMap)
    }

    @RequestMapping(value = Array("/job/kill"))
    def killJob(killJobEntity: KillJobEntity): String = {
        val groupId = killJobEntity.getGroupId
        val executor = new SQLExecute(Map())
        if(groupId==null) {
            val jobName = killJobEntity.getJobName
            val groupIds = JobManager.getJobInfo.filter(f => f._2.jobName == jobName)
            groupIds.headOption match {
                case Some(item) => JobManager.killJob(executor.getSessionByOwner(item._2.owner), item._2.groupId)
                case None =>
            }
        }else {
            JobManager.getJobInfo.find(f => f._2.groupId == groupId) match {
                case Some(item) => JobManager.killJob(executor.getSessionByOwner(item._2.owner), item._2.groupId)
                case None =>
            }
        }
        executor.cleanActiveSessionInSpark
        "job killing..."
    }

    @RequestMapping(value = Array("/instance/resource"))
    def instanceResource = {
        val executor = new SQLExecute(Map())
        val session = executor.getSession
        val resource = new SparkInstance(session).resources
        executor.cleanActiveSessionInSpark
        JSONTool.toJsonStr(resource)
    }
}
