package com.zhy.yisql.core

import com.zhy.yisql.core.job.runner.LocalSQLExecutor
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-04
  *  \* Time: 16:48
  *  \* Description: 
  *  \*/
class StreamTest {

    @Test
    def kafka2console(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set streamName="zhy1";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
              |as kafka_post_console;
              |
              |save append kafka_post_console
              |as console.``
              |and checkpointLocation="/tmp/cpl-testkafkaconsole"
              |and duration="10"
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def kafka2kafka(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set streamName="zhy1";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |as kafka_post_kafka;
              |
              |save append kafka_post_kafka
              |as kafka.`zhy1`
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and checkpointLocation="/tmp/cpl-testkafkaconsole"
              |and duration="10"
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def kafka2Jdbc(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set streamName="zhy1";
              |set user="root";
              |set password="123456";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))"
              |and `containRaw`="false"
              |as kafka_post_kafka;
              |
              |save append kafka_post_kafka as jdbc.`z1` where
              |url="jdbc:mysql://10.57.30.217:3306/zhy?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false"
              |and driver="com.mysql.jdbc.Driver"
              |and user="${user}"
              |and password="${password}"
              |and checkpointLocation="/tmp/cpl-testkafkaconsole"
              |and duration="10"
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def kafka2Elastic(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set streamName="zhy1";
              |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
              |set targetSql="select * from kafka_post_kafka where age>=25";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |and `valueSchema`="${sourceSchema}"
              |and `containRaw`="false"
              |as kafka_post_kafka;
              |
              |save append kafka_post_kafka as es.`zhy/z1` where
              |`es.index.auto.create`="true"
              |and `es.nodes`="cdh173"
              |and `etl.sql`="${targetSql}"
              |and duration="10";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def kafka2Hive(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set streamName="zhy1";
              |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
              |set targetSql="select * from kafka_post_kafka where age>=25";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |and `valueSchema`="${sourceSchema}"
              |and `containRaw`="false"
              |as kafka_post_kafka;
              |
              |save append kafka_post_kafka hive.`hhy.json_test_tab`
              |options partitionByCol="date,version"
              |and `etl.sql`="${targetSql}"
              |and duration="10";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def kafka2Delta(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        executor.sql(
            """
              |set streamName="zhy1";
              |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
              |set targetSql="select * from kafka_post_kafka where age<=25";
              |
              |load kafka.`zhy` options
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `enable.auto.commit`="true"
              |and `group.id`="zhy123"
              |and `auto.offset.reset`="latest"
              |and `valueSchema`="${sourceSchema}"
              |and `containRaw`="false"
              |as kafka_post_kafka;
              |
              |save append kafka_post_kafka delta.`/tmp/delta/table11`
              |options `etl.sql`="${targetSql}"
              |and idCols="id"
              |and dropDuplicate="true"
              |and duration="10"
              |and checkpointLocation="/tmp/s-cpl6";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }

    @Test
    def delta2Console(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        executor.sql(
            """
              |set streamName="zhy1";
              |
              |load delta.`/tmp/delta/table11` as delta_tab;
              |
              |save append delta_tab
              |as console.``
              |and duration="10"
              |and checkpointLocation="/tmp/s-cpl6";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
        Thread.currentThread().join()
    }
}
