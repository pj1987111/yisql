[spark elasticsearch配置](https://www.elastic.co/guide/en/elasticsearch/hadoop/current/spark.html)

# 1 查询

全表搜索

```sql
load es.`zhy/z1` where
and es.nodes="cdh173"
as data1;

select count(*) from data1 as table2;
```

# 2 pushdown查询

id和date的点查，用搜索下推的方式下沉到es端执行，在网络带宽上可以极大的减少。
在es.query中可以写上es的query，如果是比较大的json可以用下面例子中的，把querystring赋值。

```sql
set pushdownq='''
{
  "query" : {
	  "bool": {
	      "must": [
	        { "match": { "id":   "---1108---"}},
	        { "match": { "date": "20210112" }}
	      ]
    }
  }
}
'''

load es.`zhypy/z1` where
and es.nodes="cdh173"
and es.query="${pushdownq}"
as data1;
```

# 3 写入es

可以使用es.index.auto.create 在没有索引存在的话自动创建

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

load jsonStr.`jstr` as data1;

save overwrite data1 as es.`zhy/z1` where
`es.index.auto.create`="true"
and es.nodes="cdh173";
```