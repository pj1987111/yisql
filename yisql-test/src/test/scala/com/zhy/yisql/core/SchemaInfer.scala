package com.zhy.yisql.core

import com.zhy.yisql.core.execute.SQLExecute
import com.zhy.yisql.core.util.SparkSchemaJsonParser
import org.apache.spark.sql.catalyst.expressions.JsonToStructs
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, SparkSession, functions => F}
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 20:54
  *  \* Description: 
  *  \*/
class SchemaInfer extends BaseTest {
    System.setProperty("HADOOP_USER_NAME", "admin")
    val executor = new SQLExecute(Map("defaultPathPrefix" -> "/user/datacompute/export"))

    def getSimpleSession = {
        SparkSession
                .builder()
                .master("local[*]")
                .appName("RunScriptExecutor")
                .enableHiveSupport()
                .getOrCreate()
    }

    val schemaInferTest1 =
        """
          |!schemainfer json
          |'''
          |[{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1},{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1},{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}]
          |{"id":"1101","name":"小明1","age":"20","message":"testmsg1","date":"20210112","version":1}
          |{"id":"1101","name":"小明1","age":"20","message":"testmsg1","date":"20210112","version":1}
          |'''
          |;
        """.stripMargin

    val schemaInferTest2 =
        """
          |!schemainfer json
          |'''
          |{"table":"test1","data":{"id":"1101","name":"小明1","age":"20","message":"testmsg1","date":"20210112","version":1}}
          |'''
          |;
        """.stripMargin

    val schemaInfer2Test =
        s"""
          |!schemainfer jsonv2
          |'''
          |$jsonVals2
          |'''
          |;
        """.stripMargin
    @Test
    def schemaInfer(): Unit = {
        sqlParseInner(schemaInferTest1)
        sqlParseInner(schemaInferTest2)
    }

    @Test
    def schemaInfer2(): Unit = {
        sqlParseInner(schemaInfer2Test)
    }

    //普通json
    def jsonVals1 =
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

    def valueSchema1 =
        """st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))""".stripMargin

    //数组模拟
    def jsonVals2 =
        """
          |[{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1},{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}]
          |{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
        """.stripMargin

    def valueSchema2 =
        """st(field(age,string),field(date,string),field(id,string),field(message,string),field(name,string),field(version,string))"""

    def valueDataType2() = {
        ArrayType(StructType(Array(
            StructField("name", StringType),
            StructField("version", StringType),
            StructField("message", StringType),
            StructField("age", IntegerType)
        )))
    }

    //嵌套模拟
    def jsonVals3 =
        """
          |{"table":"1","data":{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}}
          |{"table":"1","data":{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}}
          |{"table":"1","data":{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}}
        """.stripMargin

    def valueSchema3 =
        """st(field(table,string,alias_tab),field(data,st(field(id,string,alias_id),field(name,string),field(message,string),field(date,string),field(version,integer))))""".stripMargin

    def valueDataType3() = {
        val st = new StructType()
        st.add(StructField("table", StringType))
        st.add(StructField("dd", StructType(Array(
            StructField("name", StringType),
            StructField("version", StringType),
            StructField("message", StringType),
            StructField("age", IntegerType)
        ))))
        st
    }

    //嵌套模拟+数组混用
    def jsonVals4 =
        """
          |{"table":"1","data":{"id":"1101","inner":{"age":20,"message":"testmsg1"},"date":"20210112","version":1}}
          |{"table":"1","data":{"id":"1101","inner":{"age":20,"message":"testmsg1"},"date":"20210112","version":1}}
          |[{"table":"1","data":{"id":"1101","inner":{"age":20,"message":"testmsg1"},"date":"20210112","version":1}},{"table":"1","data":{"id":"1101","inner":{"age":20,"message":"testmsg1"},"date":"20210112","version":2}},{"table":"1","data":{"id":"1101","inner":{"age":20,"message":"testmsg1"},"date":"20210112","version":3}}]
        """.stripMargin

    def valueSchema4 =
        """st(field(data,st(field(date,string),field(id,string),field(inner,st(field(age,string),field(message,string))),field(version,string))),field(table,string))""".stripMargin


    @Test
    def read(): Unit = {
        val jsonVals = jsonVals4
        val valueSchema = valueSchema4
        val spark = getSimpleSession
        import spark.implicits._
        val iterms = jsonVals.split("\n").map(f => f.trim).filter(f => f.length > 0).toSeq
        var loadTable = iterms.toDF("value")
        loadTable = SparkSchemaJsonParser.parseDataFrame(
            loadTable.selectExpr("CAST(value AS STRING) as value"), valueSchema)
        loadTable.show()
    }

    @Test
    def readWithSchema(): Unit = {
        val jsonVals = jsonVals2
        val sourceSchema = valueDataType2
        val spark = getSimpleSession
        import spark.implicits._
        val iterms = jsonVals.split("\n").map(f => f.trim).filter(f => f.length > 0).toSeq
        var loadTable = iterms.toDF("value")
        var newDf = loadTable.selectExpr("CAST(value AS STRING) as value")

        newDf = newDf.select(new Column(JsonToStructs(sourceSchema, Map(), F.col("value").expr, None)).as("data"), F.col("value"))
        newDf = newDf.select(F.explode(newDf("data"))).toDF("data")
        newDf.show()
    }
}
