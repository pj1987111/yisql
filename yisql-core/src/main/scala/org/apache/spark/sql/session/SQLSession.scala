/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.session

import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.{HashSet => MHSet}


class SQLSession(username: String,
                 password: String,
                 ipAddress: String,
                 withImpersonation: Boolean,
                 sessionManager: SessionManager,
                 opManager: SQLOperationManager,
                 sessionConf: Map[String, String] = Map()
                  ) extends Logging {


  @volatile private[this] var lastAccessTime: Long = System.currentTimeMillis()
  private[this] var lastIdleTime = 0L

  private[this] val activeOperationSet = new MHSet[String]()


  private[this] lazy val _sqlSparkSession = new SQLSparkSession(username, sessionConf)

  private[this] def acquire(userAccess: Boolean): Unit = {
    if (userAccess) {
      lastAccessTime = System.currentTimeMillis
    }
  }

  private[this] def release(userAccess: Boolean): Unit = {
    if (userAccess) {
      lastAccessTime = System.currentTimeMillis
    }
    if (activeOperationSet.isEmpty) {
      lastIdleTime = System.currentTimeMillis
    } else {
      lastIdleTime = 0
    }
  }

  def sparkSession: SparkSession = this._sqlSparkSession.sparkSession

  def yisqlSparkSession: SQLSparkSession = this._sqlSparkSession

  def open(sessionConf: Map[String, String]): Unit = {
    yisqlSparkSession.init(sessionConf)
    lastAccessTime = System.currentTimeMillis
    lastIdleTime = lastAccessTime
  }

  def close(): Unit = {
    acquire(true)
    try {
      // Iterate through the opHandles and close their operations
      activeOperationSet.clear()
    } finally {
      release(true)
    }
  }


  def visit(): SQLSession = {
    acquire(true)
    release(true)
    this
  }


  def getUserName = username

  def getOpManager = opManager

}
