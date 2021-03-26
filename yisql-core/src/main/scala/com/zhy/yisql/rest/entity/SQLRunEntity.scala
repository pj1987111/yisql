package com.zhy.yisql.rest.entity

import com.zhy.yisql.core.job.JobType

import scala.beans.BeanProperty

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 11:29
  *  \* Description: liftjson需要使用Option表示可以json中不存在该列
  *  \*/
case class SQLRunEntity(
                           @BeanProperty
                           var sql: String = "",
                           @BeanProperty
                           var owner: Option[String],
                           @BeanProperty
                           var jobType: Option[String],
                           @BeanProperty
                           var executeMode: Option[String],
                           @BeanProperty
                           var jobName: Option[String],
                           @BeanProperty
                           var timeout: Option[String],
                           @BeanProperty
                           var silence: Option[String],
                           @BeanProperty
                           var sessionPerUser: Option[String],
                           @BeanProperty
                           var sessionPerRequest: Option[String],
                           @BeanProperty
                           var async: Option[String],
                           @BeanProperty
                           var callback: Option[String],
                           @BeanProperty
                           var includeSchema: Option[String],
                           @BeanProperty
                           var fetchType: Option[String]
                       ) {
  def defaults = copy(
    jobType = jobType orElse Some(JobType.SCRIPT),
    executeMode = executeMode orElse Some("query"),
    timeout = timeout orElse Some("-1"),
    silence = silence orElse Some("false"),
    sessionPerUser = sessionPerUser orElse Some("false"),
    sessionPerRequest = sessionPerRequest orElse Some("false"),
    async = async orElse Some("false"),
    includeSchema = includeSchema orElse Some("false"),
    fetchType = fetchType orElse Some("collect")
  )
}

/**
  *
  * @param groupId 杀的任务id
  * @param jobName 杀的任务名
  *                两者选一都可
  */
case class KillJobEntity(
                            @BeanProperty
                            var groupId: Option[String],
                            @BeanProperty
                            var jobName: Option[String]
                        )
