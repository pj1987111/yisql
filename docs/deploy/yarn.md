将包拷贝至spark的jar目录中

$SPARK_HOME/bin/spark-submit --class com.zhy.yisql.StreamApp \
--driver-memory 1g \
--jars /home/admin/spark-2.4.0-bin-2.6.0-cdh5.15.0/jars/*.jar \
--master yarn \
--deploy-mode client \
--executor-memory 2g \
--executor-cores 1 \
--num-executors 1 \
--name yisql \
--conf "spark.sql.hive.thriftServer.singleSession=true" \
/home/admin/spark-2.4.0-bin-2.6.0-cdh5.15.0/jars/yisql-core-2.4-1.0-SNAPSHOT.jar \
-streaming.name yisql \
-streaming.platform spark \
-streaming.rest true \
-streaming.driver.port 9003 \
-streaming.spark.service true \
-streaming.thrift false \
-streaming.enableHiveSupport true
