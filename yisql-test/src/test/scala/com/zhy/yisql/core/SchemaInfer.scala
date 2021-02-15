package com.zhy.yisql.core

import com.zhy.yisql.runner.RunScriptExecutor
import org.apache.spark.sql.SparkSession
import org.junit.Test
import tech.mlsql.schema.parser.SparkSimpleSchemaParser

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 20:54
  *  \* Description: 
  *  \*/
class SchemaInfer {
    System.setProperty("HADOOP_USER_NAME", "admin")
    val executor = new RunScriptExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))


    @Test
    def schemaInfer(): Unit = {
        executor.sql(
            """
              |
              |
              |!schemainfer json
              |'''
              |[{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1},{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1},{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}]
              |{"id":"1101","name":"小明1","age":"20","message":"testmsg1","date":"20210112","version":1}
              |{"id":"1101","name":"小明1","age":"20","message":"testmsg1","date":"20210112","version":1}
              |'''
              |;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def read(): Unit ={
        def getSimpleSession = {
            SparkSession
                    .builder()
                    .master("local[*]")
                    .appName("RunScriptExecutor")
                    .enableHiveSupport()
                    .getOrCreate()
        }

        val jsonVals =
            """
              |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
              |{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
              |{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
              |{"id":"1104","name":"小明4","age":23,"message":"testmsg4","date":"20210112","version":1}
              |{"id":"1105","name":"小明5","age":24,"message":"testmsg5","date":"20210112","version":1}
              |{"id":"1106","name":"小明6","age":25,"message":"testmsg6","date":"20210112","version":1}
              |{"id":"1107","name":"小明7","age":26,"message":"testmsg7","date":"20210112","version":1}
              |{"id":"1108","name":"小明8","age":27,"message":"testmsg8","date":"20210112","version":1}
              |{"id":"1109","name":"小明9","age":28,"message":"testmsg9","date":"20210112","version":1}
              |{"id":"1110","name":"小明10","age":29,"message":"testmsg10","date":"20210112","version":2}
            """.stripMargin
        val spark = getSimpleSession
        import spark.implicits._
        val iterms = jsonVals.split("\n").filter(f=>f.length>0).toSeq
//        val loadTable = spark.read.json(spark.createDataset[String](iterms))
        val loadTable = iterms.toDF("value")
        loadTable.show()
        val valueSchema1 =
            """st(field(data,map(string,string)),field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))""".stripMargin
        val sourceSchema = SparkSimpleSchemaParser.parse(valueSchema1)
        println(sourceSchema)
    }
}
