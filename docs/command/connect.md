yisql可以通过sql来方便对connect的管理

首先建立一个connect连接
```sql
set user="default";
set password="ck2020";
connect ck where
url="jdbc:clickhouse://192.168.6.52:8123"
and user="${user}"
and password="${password}"
and driver="ru.yandex.clickhouse.ClickHouseDriver"
as ck1;
```

建立完后可以通过下面的sql查询已有connect别名
```sql
!show formats;
```

可以得到如下所示输出
```json
{
    "ck1":{
        "_1":"ck",
        "_2":{
            "url":"jdbc:clickhouse://192.168.6.52:8123",
            "user":"default",
            "password":"ck2020"
        }
    }
}
```

还可以使用kill命令将已建别名删除
```sql
!kill format ck1;
```

然后再执行show命令，可以看到之前建的别名已经被成功删除了。