yisql可以通过sql来方便对job的管理

执行下面的sql可以查看目前正在执行的任务，再这个测试中，我们有一个正在运行的流任务，
另外一个任务是执行的show jobs任务
```sql
!show jobs;
```

```json
{
    "19f89767-bda1-4582-8675-ab131ae8998a":{
        "owner":"testzhy",
        "jobType":"script",
        "jobName":"",
        "jobContent":"!show jobs
",
        "groupId":"19f89767-bda1-4582-8675-ab131ae8998a",
        "progress":{
            "totalJob":1,
            "currentJobIndex":1,
            "script":"run command as ShowCommand.`jobs///////////`"
        },
        "startTime":1618285856160,
        "timeout":-1
    },
    "71fe05d7-c594-4c7e-9cfe-8f00106636d4":{
        "owner":"testzhy",
        "jobType":"stream",
        "jobName":"kafka2ck",
        "jobContent":"set streamName="kafka2ck";
set sourceSchema="st(field(name,string),field(city,string),field(age,integer))";
load kafka.`zhy` options
`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
and `enable.auto.commit`="true"
and `group.id`="zhy1234"
and `auto.offset.reset`="latest"
and `valueSchema`="${sourceSchema}"
and `containRaw`="false"
as kafka_post_kafka;
save append kafka_post_kafka as ck1.`test_users3` where
and duration="10"",
        "groupId":"71fe05d7-c594-4c7e-9cfe-8f00106636d4",
        "progress":{
            "totalJob":1,
            "currentJobIndex":1,
            "script":"save append kafka_post_kafka as ck1.`test_users3` where
and duration="10""
        },
        "startTime":1618285751404,
        "timeout":-1
    }
}
```

还可以使用kill job 命令将任务删除
支持任务名或id两种方式
```sql
!kill job kafka2ck;
或
!kill job 71fe05d7-c594-4c7e-9cfe-8f00106636d4;
```

执行完后再执行show命令，可以看到，流任务已经被kill了
```json
{
    "6f182d5d-1689-4816-9d91-d266b6b099d9":{
        "owner":"testzhy",
        "jobType":"script",
        "jobName":"",
        "jobContent":"!show jobs
",
        "groupId":"6f182d5d-1689-4816-9d91-d266b6b099d9",
        "progress":{
            "totalJob":1,
            "currentJobIndex":1,
            "script":"run command as ShowCommand.`jobs///////////`"
        },
        "startTime":1618285897532,
        "timeout":-1
    }
}
```