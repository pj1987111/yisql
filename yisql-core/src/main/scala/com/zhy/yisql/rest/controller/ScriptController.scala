package com.zhy.yisql.rest.controller

import com.zhy.yisql.common.utils.bean.BeanUtils
import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.core.execute.SQLExecute
import com.zhy.yisql.core.job.JobManager
import com.zhy.yisql.netty.annotation.{PostMapping, RequestBody, RestController}
import com.zhy.yisql.netty.rest.{HttpStatus, ResponseEntity}
import com.zhy.yisql.rest.entity.{KillJobEntity, SQLRunEntity}
import org.apache.spark.SparkInstanceService

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-23
  *  \* Time: 20:48
  *  \* Description: 
  *  \*/
@RestController
class ScriptController extends Logging {
  @PostMapping(value = "/test")
  def test = ResponseEntity.status(HttpStatus.CREATED).build("sql test ok")

//  @PostMapping(value = "/sql/run")
//  def run(@RequestBody sqlRunEntity: SQLRunEntity) = {
//    val params: Map[String, String] = BeanUtils.getCCParams(sqlRunEntity)
//    logInfo(s"/sql/run method params is: $params")
//    val executor = new SQLExecute(params)
//    val res = executor.simpleExecute()
//    ResponseEntity.status(HttpStatus.CREATED).build(res._2)
//  }

  @PostMapping(value = "/sql/run")
  def run(@RequestBody body: String) = {
    val sqlRunEntity = JSONTool.parseJson[SQLRunEntity](body).defaults
    val params: Map[String, String] = BeanUtils.getCCParams(sqlRunEntity)
    logInfo(s"/sql/run method params is: $sqlRunEntity")
    val executor = new SQLExecute(params)
    val res = executor.simpleExecute()
    ResponseEntity.status(HttpStatus.CREATED).build(res._2)
  }

  @PostMapping(value = "/job/list")
  def listJobs() = {
    val infoMap = JobManager.getJobInfo
    ResponseEntity.status(HttpStatus.CREATED).build(JSONTool.toJsonStr(infoMap))
  }

  @PostMapping(value = "/job/kill")
  def killJob(@RequestBody body: String) = {
    val killJobEntity = JSONTool.parseJson[KillJobEntity](body)
    val groupId = killJobEntity.getGroupId
    val executor = new SQLExecute(Map())
    if(groupId.isEmpty) {
      val jobName = killJobEntity.getJobName
      val groupIds = JobManager.getJobInfo.filter(f => f._2.jobName == jobName.get)
      groupIds.headOption match {
        case Some(item) => JobManager.killJob(executor.getSessionByOwner(item._2.owner), item._2.groupId)
        case None =>
      }
    }else {
      JobManager.getJobInfo.find(f => f._2.groupId == groupId.get) match {
        case Some(item) => JobManager.killJob(executor.getSessionByOwner(item._2.owner), item._2.groupId)
        case None =>
      }
    }
    executor.cleanActiveSessionInSpark

    ResponseEntity.status(HttpStatus.CREATED).build("job killing...")
  }

  @PostMapping(value = "/instance/resource")
  def instanceResource = {
    val executor = new SQLExecute(Map())
    val session = executor.getSession
    val resource = new SparkInstanceService(session).resources
    executor.cleanActiveSessionInSpark
    ResponseEntity.status(HttpStatus.CREATED).build(JSONTool.toJsonStr(resource))
  }
}
