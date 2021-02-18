package com.zhy.yisql.core

import com.zhy.yisql.core.job.runner.LocalSQLExecutor
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-10
  *  \* Time: 14:36
  *  \* Description: 
  *  \*/
class BatchTest extends BaseTest {

    @Test
    def readJsonParOrc(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        executor.sql(
            """
              |set jstr='''
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
              |''';
              |
              |set savePath="/tmp/zhy/jsontest";
              |set savePathParquet="/tmp/zhy/parquettest";
              |set savePathOrc="/tmp/zhy/orctest";
              |
              |load jsonStr.`jstr` as json_out;
              |
              |save overwrite json_out as json.`${savePath}`;
              |
              |load json.`${savePath}` as table1;
              |
              |save overwrite json_out as parquet.`${savePathParquet}`;
              |
              |load parquet.`${savePathParquet}` as table2;
              |
              |save overwrite json_out as orc.`${savePathOrc}`;
              |
              |load orc.`${savePathOrc}` as table3;
              |
              |select a.id,a.name,b.age,b.date,c.message,c.version from table1 a join table2 b join table3 c on a.id=b.id and a.id=c.id as select_out;
              |
              |--save append select_out
              |--as kafka.`zhy1`
              |--`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def readCsv(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        executor.sql(
            """
              |set csvStr='''
              |id,name,age,message,date,version
              |1101,1,20,123,12,1
              |1101,1,20,123,12,2
              |1101,1,20,123,12,3
              |1101,1,20,123,12,4
              |''';
              |
              |set savePath="/tmp/zhy/csvtest"
              |
              |load csvStr.`csvStr` where header="true" as csv_out;
              |
              |save overwrite csv_out as csv.`${savePath}` where header="true";
              |
              |load csv.`${savePath}` where header="true" as table2;
              |
              |--select * from table2 where name='1' as select_out;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def json2Es(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set jstr='''
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
              |''';
              |
              |load jsonStr.`jstr` as data1;
              |
              |save overwrite data1 as es.`zhy/z1` where
              |`es.index.auto.create`="true"
              |and es.nodes="cdh173";
              |
              |load es.`zhy/z1` where
              |and es.nodes="cdh173"
              |as table1;
              |
              |select * from table1 as output1;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def json2Kafka(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set jstr='''
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
              |''';
              |
              |set targetSql="select to_json(struct(*)) as value from data1 where age>=25";
              |
              |load jsonStr.`jstr` as data1;
              |
              |save append data1
              |as kafka.`zhy`
              |`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and `etl.sql`="${targetSql}";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def kafkaRead(): Unit = {
        val p = System.getProperties
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |load kafka.`zhy` where
              |kafka.bootstrap.servers="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and multiplyFactor="2"
              |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
              |as table1;
              |
              |save append table1
              |as console.``;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def kafkaAdhoc(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |load adHocKafka.`zhy` where
              |kafka.bootstrap.servers="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and multiplyFactor="2"
              |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
              |as table1;
              |
              |load adHocKafka.`zhy` where
              |kafka.bootstrap.servers="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
              |and multiplyFactor="2"
              |and timeFormat="yyyyMMdd"
              |and startingTime="20210212"
              |and endingTime="20210213"
              |and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
              |as table2;
              |
              |--select cast(value as string) as textValue, * from table1 as output;
              |--select count(*) from table1 as output;
              |--select count(*) from table2 as output;
              |--select cast(value as string) as value from table2 as output;
              |
              |save append table2
              |as console.``;
              |
              |--kafka数据重新插入，指定时间范围
              |--save append output
              |--as kafka.`zhy1`
              |--`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def es2mysql(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set user="root";
              |set password="123456";
              |
              |load es.`zhy/z1` where
              |and es.nodes="cdh173"
              |as data1;
              |
              |save append data1 as jdbc.`z1` where
              |url="jdbc:mysql://10.57.30.217:3306/zhy?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false"
              |and driver="com.mysql.jdbc.Driver"
              |and user="${user}"
              |and password="${password}"
              |
              |load jdbc.`z1` where
              |url="jdbc:mysql://10.57.30.217:3306/zhy?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false"
              |and driver="com.mysql.jdbc.Driver"
              |and user="${user}"
              |and password="${password}" as data2;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def hiverw(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |set savePath="/tmp/zhy/jsontest1";
              |
              |load hive.`hhy.trajectory_min_section`  as table1;
              |
              |select * from hhy.trajectory_min_section as table2;
              |
              |insert overwrite table hhy.trajectory_min_section_zhy select * from table1;
              |
              |select id,direction,tr_section from hhy.trajectory_min_section_zhy as table3;
              |
              |--save overwrite table3 json.`${savePath}` options fileNum="1";
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def hiveFormat(): Unit = {
        val executor = new LocalSQLExecutor(Map())
        executor.sql(
            """
              |load hive.`hhy.trajectory_min_section`  as table1;
              |
              |save append table1 hive.`hhy.trajectory_min_section_parquet_test`
              |options partitionByCol="route_block,direction";
              |
              |save append table1 hive.`hhy.trajectory_min_section_orc_test`
              |options file_format="orc" and partitionByCol="route_block,direction";
              |
              |set jstr='''
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
              |''';
              |load jsonStr.`jstr` as json_out;
              |--save append json_out hive.`hhy.json_test_tab`
              |--options partitionByCol="date,version";
              |
              |--select count(*) from hhy.trajectory_min_section_parquet_test as pq_table;
              |--select count(*) from hhy.trajectory_min_section_orc_test as orc_table;
              |select count(*) from hhy.json_test_tab as json_test_tab;
              |
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def deltaBatchAppend(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        executor.sql(
            """
              |set jstr='''
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
              |''';
              |load jsonStr.`jstr` as json_out;
              |
              |save append json_out delta.`/tmp/delta/table11`
              |options idCols="id";
              |
              |load delta.`/tmp/delta/table11` as output;
              |
              |--select count(*) from output;
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def deltaSelect(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        //        val executor = new RunScriptExecutor(Map())
        executor.sql(
            """
              |load delta.`/tmp/delta/table11` as output;
              |
              |--select count(*) from output;
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }

    @Test
    def deltaSelectVersions(): Unit = {
        System.setProperty("HADOOP_USER_NAME", "admin")
        val executor = new LocalSQLExecutor(Map("defaultPathPrefix"->"/user/datacompute/export"))
        //        val executor = new RunScriptExecutor(Map())
        executor.sql(
            """
              |load delta.`/tmp/delta/table10` where
              |startingVersion="0"
              |and endingVersion="4"
              |as table1
              |;
              |
              |--select * from table1 as table2;
              |
              |select __delta_version__, collect_list(id) from table1 group by __delta_version__,id
              |as table2
              |;
            """.stripMargin)
        val res = executor.simpleExecute()
        println(res)
    }
}
