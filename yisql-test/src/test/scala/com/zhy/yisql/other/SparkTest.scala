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
        .appName("Binlog2DeltaTest")
        .enableHiveSupport()
        .getOrCreate()
  }

  @Test
  def testFunction(): Unit = {
    val df = spark.sql("select hhy_dw.license_check('沪AGG7609_4')")
    df.show()
  }


}
