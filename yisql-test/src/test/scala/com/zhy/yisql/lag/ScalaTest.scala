package com.zhy.yisql.lag

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext, SparkSession, SparkUtils}
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.{Before, Test}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-08
  *  \* Time: 23:31
  *  \* Description: 
  *  \*/
class ScalaTest {
  var sparkSession: SparkSession = _

  @Before
  def setUp() = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("test")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    sparkSession = sqlContext.sparkSession
  }

  @Test
  def evalCode(): Unit = {
    import scala.tools.reflect.ToolBox

    val toolbox = scala.reflect.runtime.currentMirror.mkToolBox()

    val n = 10
    val code2 = s"println($n)"
    toolbox.eval(toolbox.parse(code2))

    val list = "List(1,2,3)"
    val ll = toolbox.eval(toolbox.parse(list)).asInstanceOf[List[Int]]
    println(ll)
  }

  /**
    * 每个batch修改值
    */
  @Test
  def editValTest(): Unit = {
    val session = sparkSession
    import session.implicits._
    val timezoneid = session.sessionState.conf.sessionLocalTimeZone
    val df = session.createDataset[String](Seq("a1", "b1")).toDF("value")
    val struct = df.schema
    val abc = df.rdd.mapPartitions { iter => {
      val encoder = RowEncoder.apply(struct).resolveAndBind()
      iter.map { irow =>
        val seq = irow.toSeq
        val editVal = "=====" + seq.apply(0).asInstanceOf[String] + "====="
        val nRow = Row.fromSeq(Seq(editVal))
        encoder.toRow(nRow)
      }
    }
    }

    val outDf = SparkUtils.internalCreateDataFrame(session,
      abc,
      StructType(Seq(StructField("value", StringType))), false)
    outDf.show()
  }

  @Test
  def scalaEmmbed(): Unit = {
    val emmbedCode =
      s"""
         |//val editVal = "====="+seq.apply(0).asInstanceOf[String]+"====="
         |print("123123")
      """.stripMargin

    val session = sparkSession
    import session.implicits._
    val timezoneid = session.sessionState.conf.sessionLocalTimeZone
    val df = session.createDataset[String](Seq("a1", "b1")).toDF("value")
    val struct = df.schema
    val abc = df.rdd.mapPartitions { iter => {
      val encoder = RowEncoder.apply(struct).resolveAndBind()
      import scala.tools.reflect.ToolBox
      val toolbox = scala.reflect.runtime.currentMirror.mkToolBox()

      iter.map { irow =>
        val seq = irow.toSeq
        val editVal = "====="+seq.apply(0).asInstanceOf[String]+"====="
        toolbox.eval(toolbox.parse(emmbedCode))
        val nRow = Row.fromSeq(Seq(editVal))
        encoder.toRow(nRow)
      }
    }
    }

    val outDf = SparkUtils.internalCreateDataFrame(session,
      abc,
      StructType(Seq(StructField("value", StringType))), false)
    outDf.show()
  }
}
