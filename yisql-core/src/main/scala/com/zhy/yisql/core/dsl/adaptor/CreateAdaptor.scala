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

package com.zhy.yisql.core.dsl.adaptor

import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.dsl.template.TemplateMerge
import com.zhy.yisql.dsl.parser.DSLSQLLexer
import com.zhy.yisql.dsl.parser.DSLSQLParser.SqlContext
import org.antlr.v4.runtime.misc.Interval

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-12
  *  \* Time: 21:41
  *  \* Description: 
  * hive 创建
  *  \*/
class CreateAdaptor(scriptSQLExecListener: ScriptSQLExecListener) extends DslAdaptor {
  def analyze(ctx: SqlContext): CreateStatement = {
    val input = ctx.start.getTokenSource.asInstanceOf[DSLSQLLexer]._input
    val start = ctx.start.getStartIndex
    val stop = ctx.stop.getStopIndex
    val interval = new Interval(start, stop)
    val originalText = input.getText(interval)
    val sql = TemplateMerge.merge(originalText, scriptSQLExecListener.env().toMap)
    CreateStatement(originalText, sql)
  }

  override def parse(ctx: SqlContext): Unit = {
    val CreateStatement(originalText, sql) = analyze(ctx)
    scriptSQLExecListener.sparkSession.sql(sql).count()
    scriptSQLExecListener.setLastSelectTable(null)
  }
}

case class CreateStatement(raw: String, sql: String)
