package com.zhy.yisql.dsl

import com.zhy.yisql.core.dsl.processor.ScriptSQLExecListener
import com.zhy.yisql.core.execute.{PathPrefix, SQLExecuteContext}
import org.apache.spark.sql.SparkSession
import org.junit.{Before, Test}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-13
  *  \* Time: 10:04
  *  \* Description: 
  *  \*/
class DslTest {

  var spark:SparkSession = _

  @Before
  def before(): Unit = {
    spark = SparkSession
        .builder
        .master("local[*]")
        .appName("streaming-test")
        .getOrCreate()
  }

  @Test
  def dslTest(): Unit = {
    val sql1 = "!kill format ck1;"

    val context = new ScriptSQLExecListener(spark, null)
    SQLExecuteContext.parse(sql1, context)

    val sql2 = "!kill job ck1;"
    SQLExecuteContext.parse(sql2, context)
  }

  @Test
  def test2(): Unit = {
    val jsonVals2 =
      """
        |[{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1},{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}]
        |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
      """.stripMargin

    val sql = s"""
                 |!schemainfer jsonv2
                 |'''
                 |$jsonVals2
                 |'''
                 |;
        """.stripMargin
    val context = new ScriptSQLExecListener(spark, null)
    SQLExecuteContext.parse(sql, context)
  }

  @Test
  def mergeTest(): Unit = {
    val sql1 = "!hive merge table hhy.json_test_tab partition 'date=20210112,version=1';"
    val context = new ScriptSQLExecListener(spark, null)
    SQLExecuteContext.parse(sql1, context)
  }
}
