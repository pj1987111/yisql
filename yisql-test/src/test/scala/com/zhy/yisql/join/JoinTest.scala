package com.zhy.yisql.join

import com.zhy.yisql.common.utils.json.JsonUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.junit.{Before, Test}

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

class JoinTest {
  var sparkG: SparkSession = _

  @Before
  def before(): Unit = {
    sparkG = SparkSession
      .builder
      .master("local[*]")
      .config("spark.sql.streaming.checkpointLocation", "./spark-checkpoints")
      .appName("streaming-test")
      .getOrCreate()
  }

  /**
   * df 测试，没用
   */
  @Test
  def streamStaticJoin1(): Unit = {
    val spark: SparkSession = sparkG
    import spark.implicits._
    var cacheT = spark.read.json(spark.createDataset[String](getJson(0).split("\n")))
    cacheT.persist()

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        cacheT.unpersist()
        cacheT = spark.read.json(spark.createDataset[String](getJson(milisecs).split("\n")))
        cacheT.persist()
        cacheT.show()
      }
    }, 0, 10, TimeUnit.SECONDS)

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //        .option("includeTimestamp", true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    val line3 = line2.join(cacheT, "id")
    val query = line3.writeStream
      .outputMode("append")
      .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
      //.format("console")
      .start()

    query.awaitTermination()
  }

  /**
   * 转表测试，没用
   */
  @Test
  def streamStaticJoin2(): Unit = {
    val spark: SparkSession = sparkG
    val staticTabName = "sTab"

    import spark.implicits._
    var cacheT = spark.read.json(spark.createDataset[String](getJson(0).split("\n")))
    cacheT.persist()
    cacheT.createOrReplaceTempView(staticTabName)

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        cacheT.unpersist()
        cacheT = spark.read.json(spark.createDataset[String](getJson(milisecs).split("\n")))
        cacheT.persist()
        cacheT.show()
        cacheT.createOrReplaceTempView(staticTabName)
        spark.catalog.refreshTable(staticTabName)
      }
    }, 1, 10, TimeUnit.SECONDS)

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //        .option("includeTimestamp", true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    line2.createOrReplaceTempView("dTab")
    val line3 = spark.sql("select a.*,b.* from dTab a join sTab b on a.id=b.id")
    //    val line3 = line2.join(cacheT, "id")
    val query = line3.writeStream
      .outputMode("append")
      .format("org.apache.spark.sql.execution.streaming.sources.YiSQLConsoleSinkProvider")
      //.format("console")
      .start()

    query.awaitTermination()
  }

  /**
   * 这种可以 df嵌套在foreach中 ok
   */
  @Test
  def streamStaticJoin3(): Unit = {
    val spark: SparkSession = sparkG

    import spark.implicits._
    var cacheT = spark.read.json(spark.createDataset[String](getJson(0).split("\n")))
    cacheT.persist()

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        cacheT.unpersist()
        cacheT = spark.read.json(spark.createDataset[String](getJson(milisecs).split("\n")))
        cacheT.persist()
        //        cacheT.show()
      }
    }, 1, 10, TimeUnit.SECONDS)

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //        .option("includeTimestamp", true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    //    val line3 = line2.join(cacheT, "id")
    val query = line2.writeStream
      .outputMode("append")
      .foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        val joinRes = dataBatch.join(cacheT, "id")
        joinRes.show()
      }
      .start()
    query.awaitTermination()
  }

  /**
   * 使用广播 ok
   */
  @Test
  def streamStaticJoin4(): Unit = {
    val spark: SparkSession = sparkG

    import spark.implicits._

    var staticDf = spark.sparkContext.parallelize(getJson(0).split("\n").filter(str => str.trim.nonEmpty)
      .map(JsonUtils.fromJson[Map[String, String]](_))).map(x => (x("id"), x("content"))).toDF("id", "content")
    var cacheInfoBc: Broadcast[DataFrame] = spark.sparkContext.broadcast(staticDf)

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        staticDf = spark.sparkContext.parallelize(getJson(milisecs).split("\n").filter(str => str.trim.nonEmpty)
          .map(JsonUtils.fromJson[Map[String, String]](_))).map(x => (x("id"), x("content"))).toDF("id", "content")
        cacheInfoBc = spark.sparkContext.broadcast(staticDf)
      }
    }, 1, 10, TimeUnit.SECONDS)

    val host = "localhost"
    val port = "9999"

    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //        .option("includeTimestamp", true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    val query = line2.writeStream
      .outputMode("append")
      .foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        val cacheVal = cacheInfoBc.value
        cacheVal.show()
        val joinRes = dataBatch.join(cacheVal, "id")
        joinRes.show()
      }
      .start()
    query.awaitTermination()
  }

  /**
   * foreachBatch中创建的view无法访问???
   */
  @Test
  def streamStaticJoin5(): Unit = {
    val spark: SparkSession = sparkG
    val staticTabName = "sTab"

    import spark.implicits._
    var cacheT = spark.read.json(spark.createDataset[String](getJson(0).split("\n")))
    cacheT.createOrReplaceTempView(staticTabName)

    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    executor.scheduleAtFixedRate(new Runnable {
      override def run(): Unit = {
        val milisecs = System.currentTimeMillis
        println(s"milisecs is : $milisecs")
        cacheT = spark.read.json(spark.createDataset[String](getJson(milisecs).split("\n")))
        cacheT.createOrReplaceTempView(staticTabName)
      }
    }, 1, 10, TimeUnit.SECONDS)
    val host = "localhost"
    val port = "9999"
    // define socket source
    val lines = spark.readStream
      .format("socket")
      .option("host", host)
      .option("port", port)
      //      .option("includeTimestamp", value = true)
      .load()
    val line2 = lines.as[String].flatMap(_.split(",")).selectExpr("CAST(value AS STRING) as id")
    val dynamicTabName = "dTab"
    val query = line2.writeStream
      .outputMode("append")
      .foreachBatch { (dataBatch: Dataset[Row], batchId: Long) =>
        if (dataBatch.count() > 0) {
          import spark.sparkContext._
          //        val curBatchView = s"${dynamicTabName}_${batchId}"
          dataBatch.show()
          dataBatch.createOrReplaceTempView(dynamicTabName)
//          val count = spark.table(dynamicTabName).count
//          print(count)
          val joinRes = spark.sql(s"select * from $dynamicTabName")
          //        val joinRes = spark.sql(s"select a.*,b.* from $curBatchView a join sTab b on a.id=b.id")
          joinRes.show()
        }
      }
      .start()
    query.awaitTermination()
  }

  def getJson(unit: Long): String = {
    s"""
      {"id":1,"content":"c1_$unit"}
      {"id":2,"content":"c2_$unit"}
      {"id":3,"content":"c3_$unit"}
      {"id":4,"content":"c4_$unit"}
      """
  }
}
