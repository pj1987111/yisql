package com.zhy.yisql.rest.entity

import com.zhy.yisql.core.job.SQLJobType

import scala.beans.BeanProperty

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 11:29
  *  \* Description: 
  *  \*/
case class SQLRunEntity(
                               @BeanProperty
                               var sql: String = "",
                               @BeanProperty
                               var owner: String = "",
                               @BeanProperty
                               var jobType: String = SQLJobType.SCRIPT,
                               @BeanProperty
                               var executeMode: String = "query",
                               @BeanProperty
                               var jobName: String = "",
                               @BeanProperty
                               var timeout: String = "-1",
                               @BeanProperty
                               var silence: String = "false",
                               @BeanProperty
                               var sessionPerUser: String = "false",
                               @BeanProperty
                               var sessionPerRequest: String = "false",
                               @BeanProperty
                               var async: String = "false",
                               @BeanProperty
                               var callback: String = "",
                               @BeanProperty
                               var includeSchema: String = "false",
                               @BeanProperty
                               var fetchType: String = "collect"
                       )

case class KillJobEntity(
                                @BeanProperty
                                var groupId: String = "",
                                @BeanProperty
                                var jobName: String = ""
                        )
