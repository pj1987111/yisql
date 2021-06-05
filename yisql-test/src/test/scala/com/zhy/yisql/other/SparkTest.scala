package com.zhy.yisql.other

import org.apache.spark.sql.SparkSession
import org.junit.{Before, Test}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-14
  *  \* Time: 13:27
  *  \* Description: 
  *  \*/
class SparkTest {
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
  def testFunction(): Unit = {
    val df = spark.sql("select hhy_dw.license_check('沪AGG7609_4')")
    df.show()
  }

  @Test
  def testShowSchema(): Unit = {
    val items =
      """
        |{"id":9,"content":"1一","label":0.0}
        |{"id":10,"content":"2二","label":0.0}
        |{"id":11,"content":"中国","label":0.0}
        |{"id":12,"content":"e","label":0.0}
        |{"id":13,"content":"5","label":0.0}
        |{"id":14,"content":"4","label":0.0}
      """.stripMargin.split("\n")
    val sparkS = spark
    import sparkS.implicits._
    val loadTable = sparkS.read.json(sparkS.createDataset[String](items))
    loadTable.show()
    val showDf = Seq(loadTable.schema.treeString).toDF("schema")
    showDf.show(false)
  }
}
