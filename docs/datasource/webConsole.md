由于流程序是一个源源不断的结果，所以不能用传统的批命令的方式返回，
yisql支持一种较webConsole的sink，可以将数据以socket方式发送到socketserver上。

通过命令启动socket server监听
```
nc -l 6049
```

启动一个实时流交换程序
读取kafka数据，通过socket将数据输出
```
set streamName="zhy1";

load kafka.`g1` options
`kafka.bootstrap.servers`="10.57.30.214:9092,10.57.30.215:9092,10.57.30.216:9092"
and `enable.auto.commit`="true"
and `group.id`="newG1"
and `auto.offset.reset`="latest"
and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer),field(age,integer))"
and `containRaw`="false"
and `failOnDataLoss`="false"
as kafka_post_kafka;

save append kafka_post_kafka as webConsole.``
options idCols="x,y"
and dropDuplicate="true"
and duration="10"
and checkpointLocation="/tmp/s-cpl7";
```
![](.webConsole_images/f1394bf4.png)