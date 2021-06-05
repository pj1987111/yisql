# 1 环境安装

在这里我们使用docker方式安装测试需要的组件，主要包括zookeeper,kafka和mysql，其中mysql主要测试实时交换

## 1.1 安装zookeeper
1. 先pull镜像
```
docker pull mesoscloud/zookeeper:3.4.8
```

2. 创建zookeeper目录，用于存放zookeeper数据及日志
```
mkdir zookeeper
cd zookeeper/
```

3. 运行并启动zookeeper容器

-d 后台运行容器
-p 将容器的端口映射到本机的端口
-v 将主机目录挂载到容器的目录
```
docker run -p 2181:2181 --name zookeeper -v $PWD/conf:/opt/zookeeper/conf -v $PWD/data:/tmp/zookeeper -d mesoscloud/zookeeper:3.4.8
```

4. 检查
```
docker ps
```

(base) ➜  zookeeper docker ps
CONTAINER ID   IMAGE                           COMMAND                  CREATED       STATUS       PORTS                                       NAMES
f4567a435af9   mesoscloud/zookeeper:3.4.8      "/usr/local/bin/dumb…"   2 hours ago   Up 2 hours   0.0.0.0:2181->2181/tcp, :::2181->2181/tcp   zookeeper1

## 1.2 安装kafka
1. pull镜像
```
docker pull wurstmeister/kafka:2.11-2.0.0
```

2. 运行镜像，这里注意KAFKA_ADVERTISED_LISTENERS和KAFKA_ZOOKEEPER_CONNECT需要写ip地址，不能写127.0.0.1或localhost，不然会连不上zk

-d 后台运行容器
-p 将容器的端口映射到本机的端口
-v 将主机目录挂载到容器的目录
-e 设置参数
```
docker run  -d --name kafka1 -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=192.168.50.33:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.50.33:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -d wurstmeister/kafka:2.11-2.0.0
```

3. 查看日志
```
docker logs -f -t --tail 100 kafka1
```

## 1.3 安装mysql
1. pull镜像
```
docker pull mysql:5.7
```

2. 创建mysql目录以及附属目录，方便删除后重建
```
mkdir mysql 
cd mysql
mkdir conf
mkdir data
mkdir logs
touch conf/my.cnf
```

3. 运行镜像 

-d 后台运行容器
-p 将容器的端口映射到本机的端口
-v 将主机目录挂载到容器的目录
-e 设置参数

使用MYSQL_ROOT_PASSWORD参数设置 root密码
```
docker run -p 3306:3306 --name mysql -v $PWD/conf:/etc/mysql/conf.d -v $PWD/logs:/logs -v $PWD/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:5.7
```

4. 验证data目录下是否有文件

```
ls data
```

# 2 离线同步测试

这里使用了str作为源，格式为json，模拟了一些json数据插入kafka中。使用to_json函数将数据进行json化，并可以使用过滤条件等过滤掉age小于25的数据。

```
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

set targetSql="select to_json(struct(*)) as value from data1 where age>=25";

load jsonStr.`jstr` as data1;

save append data1
as kafka.`g1`
`kafka.bootstrap.servers`="127.0.0.1:9092"
and `etl.sql`="${targetSql}";
```

执行后，登陆到docker中，执行以下命令，可以看到数据成功写入
```
docker exec -it containerid /bin/bash

/opt/kafka/bin

./kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic g1 --from-beginning
```


# 3 实时同步测试

1. 启动一个kafka->mysql的实时流同步
```
set streamName="zhy1";
set user="root";
set password="root";

load kafka.`g1` options
`kafka.bootstrap.servers`="127.0.0.1:9092"
and `enable.auto.commit`="true"
and `group.id`="zhy123"
and `auto.offset.reset`="latest"
and `valueSchema`="st(field(id,string),field(name,string),field(message,string),field(date,string),field(version,integer))"
and `containRaw`="false"
as kafka_post_kafka1;

save append kafka_post_kafka1 as jdbc.`z1` where
url="jdbc:mysql://127.0.0.1:3306/zhy?useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false"
and driver="com.mysql.jdbc.Driver"
and user="${user}"
and password="${password}"
--and checkpointLocation="/tmp/cpl-testkafkaconsole2"
and duration="10";
```

2. 执行离线同步测试的命令往kafka中插入几条数据

3. mysql验证