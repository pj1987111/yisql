package com.zhy.yisql.core

import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-04
  *  \* Time: 16:48
  *  \* Description: 
  *  \*/
class StreamTest extends BaseTest {

  val kafka2consoleTest =
    """
      |set streamName="zhy1";
      |
      |load kafka.`zhy` options
      |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
      |and `enable.auto.commit`="true"
      |and `group.id`="zhy1234"
      |and `failOnDataLoss`="false"
      |and `auto.offset.reset`="latest"
      |--and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
      |as kafka_post_console;
      |
      |save append kafka_post_console
      |as console.``
      |and checkpointLocation="/tmp/cpl-testkafkaconsole"
      |and duration="10"
    """.stripMargin

  val kafka2kafkaTest =
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
      |and checkpointLocation="/tmp/cpl-testkafkaconsole1"
      |and duration="10"
    """.stripMargin

  val kafka2JdbcTest =
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
      |--and checkpointLocation="/tmp/cpl-testkafkaconsole2"
      |and duration="10"
    """.stripMargin

  val kafka2ElasticTest =
    """
      |set streamName="zhy1";
      |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
      |set targetSql="select * from kafka_post_kafka where age>=25";
      |
      |load kafka.`zhy` options
      |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
      |and `enable.auto.commit`="true"
      |and `group.id`="zhy1234"
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
    """.stripMargin

  val kafka2ElasticPythonTest =
    """
      |set streamName="zhy1";
      |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
      |set targetCode='''
      |data = context.fetch_once_as_rows()
      |def process(data):
      |    for row in data:
      |        new_row = { }
      |        new_row["id"] = "---" + row["id"]+"---"
      |        new_row["name"] = "---" + row["name"]+"---"
      |        new_row["message"] = "---" + row["message"]+"---"
      |        new_row["date"] = row["date"]
      |        yield new_row
      |
      |context.build_result(process(data))
      |''';
      |!python conf "python.bin.path=/Library/Frameworks/Python.framework/Versions/3.6/bin/python3 ";
      |!python conf "schema=st(field(id,string),field(name,string),field(message,string),field(date,string))";
      |
      |load kafka.`zhy` options
      |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
      |and `enable.auto.commit`="true"
      |and `group.id`="zhy1234"
      |and `auto.offset.reset`="latest"
      |and `valueSchema`="${sourceSchema}"
      |and `containRaw`="false"
      |as kafka_post_kafka;
      |
      |save append kafka_post_kafka as es.`zhypy/z1` where
      |`es.index.auto.create`="true"
      |and `es.nodes`="cdh173"
      |and `etl.code`="${targetCode}"
      |and duration="10";
    """.stripMargin

  val kafka2HiveTest =
    """
      |set streamName="zhy1";
      |set sourceSchema="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))";
      |--val targetSql: Nothing = "select * from kafka_post_kafka where age>=25";
      |set targetSql="select * from kafka_post_kafka";
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
      |options
      |partitionByCol="date,version"
      |and
      |`etl.sql`="${targetSql}"
      |and duration="10";
    """.stripMargin

  val kafka2DeltaTest =
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
            """.stripMargin

  val delta2ConsoleTest =
    """
      |set streamName="zhy1";
      |
      |load delta.`/tmp/delta/table11` as delta_tab;
      |
      |save append delta_tab
      |as console.``
      |and duration="10"
      |and checkpointLocation="/tmp/s-cpl6";
    """.stripMargin

  val kafka2WebConsoleTest =
    """
      |set streamName="zhy1";
      |
      |load kafka.`g1` options
      |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
      |and `enable.auto.commit`="true"
      |and `group.id`="newG1"
      |and `auto.offset.reset`="latest"
      |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))"
      |and `containRaw`="false"
      |and `failOnDataLoss`="false"
      |as kafka_post_kafka;
      |
      |save append kafka_post_kafka as webConsole.``
      |options idCols="x,y"
      |and dropDuplicate="true"
      |and duration="10"
      |and checkpointLocation="/tmp/s-cpl7";
    """.stripMargin

  @Test
  def kafka2console(): Unit = {
    sqlParseInner(kafka2consoleTest)
  }

  @Test
  def kafka2kafka(): Unit = {
    sqlParseInner(kafka2kafkaTest)
  }

  @Test
  def kafka2Jdbc(): Unit = {
    sqlParseInner(kafka2JdbcTest)
  }

  @Test
  def kafka2Elastic(): Unit = {
    sqlParseInner(kafka2ElasticTest)
  }

  @Test
  def kafka2ElasticPython(): Unit = {
    sqlParseInner(kafka2ElasticPythonTest)
  }

  @Test
  def kafka2Hive(): Unit = {
    sqlParseInner(kafka2HiveTest)
  }

  @Test
  def kafka2Delta(): Unit = {
    sqlParseInner(kafka2DeltaTest)
  }

  @Test
  def delta2Console(): Unit = {
    sqlParseInner(delta2ConsoleTest)
  }

  @Test
  def kafka2WebConsole(): Unit = {
    sqlParseInner(kafka2WebConsoleTest)
  }
}
