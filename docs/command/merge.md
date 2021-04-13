# merge功能测试

# hive merge
测试一下hive的merge

## parquet格式

首先创建一张parquet表，建表语句如下所示

```sql
CREATE TABLE `hhy.json_test_tab2`(
  `age` bigint,
  `id` string,
  `message` string,
  `name` string)
PARTITIONED BY (
  `date` string,
  `version` bigint)
ROW FORMAT SERDE
  'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe'
WITH SERDEPROPERTIES (
  'partitionByCol'='date,version',
  'path'='hdfs://tdhdfs/user/hive/warehouse/hhy.db/json_test_tab2')
STORED AS INPUTFORMAT
  'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'
LOCATION
  'hdfs://tdhdfs/user/hive/warehouse/hhy.db/json_test_tab2'
TBLPROPERTIES (
  'spark.sql.create.version'='2.4.0',
  'spark.sql.partitionProvider'='catalog',
  'spark.sql.sources.provider'='parquet',
  'spark.sql.sources.schema.numPartCols'='2',
  'spark.sql.sources.schema.numParts'='1',
  'spark.sql.sources.schema.part.0'='{\"type\":\"struct\",\"fields\":[{\"name\":\"age\",\"type\":\"long\",\"nullable\":true,\"metadata\":{}},{\"name\":\"id\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"message\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"name\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"date\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"version\",\"type\":\"long\",\"nullable\":true,\"metadata\":{}}]}',
  'spark.sql.sources.schema.partCol.0'='date',
  'spark.sql.sources.schema.partCol.1'='version',
  'transient_lastDdlTime'='1613109265')
```

然后创建一个离线交换，从json写入hive表中，执行3次
```sql
set jstr='''
{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
{"id":"1104","name":"小明4","age":23,"message":"testmsg4","date":"20210112","version":1}
{"id":"1105","name":"小明5","age":24,"message":"testmsg5","date":"20210112","version":1}
{"id":"1106","name":"小明6","age":25,"message":"testmsg6","date":"20210112","version":1}
{"id":"1107","name":"小明7","age":26,"message":"testmsg7","date":"20210112","version":1}
{"id":"1108","name":"小明8","age":27,"message":"testmsg8","date":"20210112","version":1}
{"id":"1109","name":"小明9","age":28,"message":"testmsg9","date":"20210112","version":1}
{"id":"1110","name":"小明10","age":29,"message":"testmsg10","date":"20210112","version":2}
''';
load jsonStr.`jstr` as json_out;
save append json_out hive.`hhy.json_test_tab2`
options partitionByCol="date,version";
```

执行以下命令查看分区，可以看到文件数，第二行是执行merge后date=20210112/version=1 分区的文件个数，可以看到从13个变为2个
```
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab2/date=20210112/version=1 | wc -l
13
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab2/date=20210112/version=1 | wc -l
2
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab2/date=20210112/version=2 | wc -l
4
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab2/date=20210112/version=1
Found 1 items
-rw-r--r--   1 admin supergroup       1343 2021-04-13 20:12 /user/hive/warehouse/hhy.db/json_test_tab2/date=20210112/version=1/part-00000-6da570f5-f069-47d4-926d-f158b465a82a-c000.snappy.parquet
```

执行merge命令
```sql
!hive merge table hhy.json_test_tab2 partition "date=20210112,version=1";
```

执行后再执行一下load+select count(*)查看一下条数，是否正确。验证后还是30行。
```sql
load hive.`hhy.json_test_tab2` as out;
select count(*) from out;
```

## orc格式

首先创建一张orc表，建表语句如下所示

```sql
CREATE TABLE `hhy.json_test_tab3`(
  `age` bigint,
  `id` string,
  `message` string,
  `name` string)
PARTITIONED BY (
  `date` string,
  `version` bigint)
ROW FORMAT SERDE
  'org.apache.hadoop.hive.ql.io.orc.OrcSerde'
WITH SERDEPROPERTIES (
  'partitionByCol'='date,version',
  'path'='hdfs://tdhdfs/user/hive/warehouse/hhy.db/json_test_tab3')
STORED AS INPUTFORMAT
  'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'
LOCATION
  'hdfs://tdhdfs/user/hive/warehouse/hhy.db/json_test_tab3'
TBLPROPERTIES (
  'orc.compress'='snappy',
  'spark.sql.sources.schema.numPartCols'='2',
  'spark.sql.sources.schema.numParts'='1',
  'spark.sql.sources.schema.part.0'='{\"type\":\"struct\",\"fields\":[{\"name\":\"age\",\"type\":\"long\",\"nullable\":true,\"metadata\":{}},{\"name\":\"id\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"message\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"name\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"date\",\"type\":\"string\",\"nullable\":true,\"metadata\":{}},{\"name\":\"version\",\"type\":\"long\",\"nullable\":true,\"metadata\":{}}]}',
  'spark.sql.sources.schema.partCol.0'='date',
  'spark.sql.sources.schema.partCol.1'='version',
  'transient_lastDdlTime'='1613109265');
```

然后创建一个离线交换，从json写入hive表中。
注意：首次空表插入用append的话可能会报错The format of the existing table project_bsc_dhr.bloc_views is HiveFileFormat. It doesn't match the specified format OrcFileFormat.;

解决方法是：
第一次空表的时候执行overwrite，后两次执行append
```sql
set jstr='''
{"id":"1101","name":"小明1","age":20,"message":"testmsg1","date":"20210112","version":1}
{"id":"1102","name":"小明2","age":21,"message":"testmsg2","date":"20210112","version":1}
{"id":"1103","name":"小明3","age":22,"message":"testmsg3","date":"20210112","version":1}
{"id":"1104","name":"小明4","age":23,"message":"testmsg4","date":"20210112","version":1}
{"id":"1105","name":"小明5","age":24,"message":"testmsg5","date":"20210112","version":1}
{"id":"1106","name":"小明6","age":25,"message":"testmsg6","date":"20210112","version":1}
{"id":"1107","name":"小明7","age":26,"message":"testmsg7","date":"20210112","version":1}
{"id":"1108","name":"小明8","age":27,"message":"testmsg8","date":"20210112","version":1}
{"id":"1109","name":"小明9","age":28,"message":"testmsg9","date":"20210112","version":1}
{"id":"1110","name":"小明10","age":29,"message":"testmsg10","date":"20210112","version":2}
''';
load jsonStr.`jstr` as json_out;
save append json_out hive.`hhy.json_test_tab3`
options partitionByCol="date,version";
```

执行以下命令查看分区，可以看到文件数，第二行是执行merge后date=20210112/version=1 分区的文件个数，可以看到从13个变为2个
```
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab3/date=20210112/version=1 | wc -l
13
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab3/date=20210112/version=1 | wc -l
2
[admin@cdh217 ~]$ hdfs dfs -ls /user/hive/warehouse/hhy.db/json_test_tab3/date=20210112/version=1
Found 1 items
-rw-r--r--   1 admin supergroup        834 2021-04-13 23:03 /user/hive/warehouse/hhy.db/json_test_tab3/date=20210112/version=1/part-00000-9b429df3-99b0-47df-887c-4fcbbb48a9b2-c000.snappy.orc
```

执行merge命令
```sql
!hive merge table hhy.json_test_tab3 partition "date=20210112,version=1";
```

执行后再执行一下load+select count(*)查看一下条数，是否正确。
注意orc写完表后可能会遇到此前文件不存在的问题，这时候执行一下refresh table命令，这命令在yisql中也是有的，执行

```sql
!hive sql "refresh table hhy.json_test_tab3";
```

然后执行搜索命令，验证后还是30行。
```sql
load hive.`hhy.json_test_tab3` as out;
select count(*) from out;
```