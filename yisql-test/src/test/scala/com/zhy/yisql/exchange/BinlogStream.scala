package com.zhy.yisql.exchange

import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-01-30
  *  \* Time: 15:36
  *  \* Description: 
  * 默认会在/private/var/folders/vd/产生checkpoint
  *  \*/
class BinlogStream {

    def init() = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val spark = SparkSession.builder()
                .master("local[*]")
                .appName("Binlog2DeltaTest")
                .getOrCreate()
        spark
    }

    @Test
    def test1(): Unit = {
        val spark = init()

        val df = spark.readStream.
                format("org.apache.spark.sql.mlsql.sources.MLSQLBinLogDataSource").
                option("host", "10.57.30.217").
                option("port", "3306").
                option("userName", "root").
                option("password", "123456").
                option("databaseNamePattern", "zhy").
                option("tableNamePattern", "zb").
                load()

        val query = df.writeStream
                .outputMode("append")
                .format("console")
                .trigger(Trigger.ProcessingTime("3 seconds"))
                //                .option("checkpointLocation","./ckp")
                .start()

        //必须加checkpoint，存储在hdfs上
        //        val query = df.writeStream.foreachBatch((batchData: DataFrame, batchId:Long) => {
        //            batchData.show(false)
        //        }).option("checkpointLocation", "/tmp/cpl-binlog25")
        //                //          .outputMode(OutputMode.Append)
        //                .trigger(Trigger.ProcessingTime("5 seconds")).start()
        query.awaitTermination()
        //        Thread.currentThread().join()
    }
}