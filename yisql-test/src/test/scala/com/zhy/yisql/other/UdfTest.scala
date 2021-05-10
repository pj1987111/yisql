package com.zhy.yisql.other

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.{Expression, ScalaUDF}
import org.junit.{Before, Test}

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBox

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-10
  *  \* Time: 10:35
  *  \* Description: 
  *  \*/
class UdfTest {
  var spark: SparkSession = _

  @Before
  def init() = {
    System.setProperty("HADOOP_USER_NAME", "admin")
    spark = SparkSession.builder()
      .master("local[*]")
      .appName("test123")
      .enableHiveSupport()
      .getOrCreate()
  }

  @Test
  def scalaUdfTest1(): Unit = {
    val sparkL = spark
    import sparkL.implicits._
    val valList = List(ValE(1.0,1.8),ValE(2.2,2.8))
    val valListDs = valList.toDS
    valListDs.createOrReplaceTempView("user")
    valListDs.show

    val str =
      """
        |def apply(a:Double,b:Double)={
        |   a + b
        |}
      """.stripMargin

    def strFun(a:Double,b:Double): Unit = {

    }

    var classLoader = Thread.currentThread().getContextClassLoader
    if (classLoader == null) {
      classLoader = scala.reflect.runtime.universe.getClass.getClassLoader
    }
    val tb = runtimeMirror(classLoader).mkToolBox()
    val tree = tb.parse(str)
    val model = tb.compile(tree).apply().asInstanceOf[Class[_]]
    val func = model.asInstanceOf[(Seq[Expression]) => ScalaUDF]


    spark.udf.register("testAdd", func)

    sparkL.sql("select testAdd(val1,val2) from user")
  }
}

case class ValE(val1:Double,val2:Double)
