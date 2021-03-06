#!/usr/bin/env bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

#export STREAMX_HOME="$(cd "`dirname "$0"`"/..; pwd)"
export STREAMX_HOME="/home/admin/spark-2.4.0-bin-2.6.0-cdh5.15.0"
echo $STREAMX_HOME

# Find the java binary
if [ -n "${JAVA_HOME}" ]; then
  JAVA_RUN="${JAVA_HOME}/bin/java"
else
  if [ `command -v java` ]; then
    JAVA_RUN="java"
  else
    echo "JAVA_HOME is not set" >&2
    exit 1
  fi
fi

JAR_DIR=$STREAMX_HOME/jars/*.jar
CLASSPATH=""
for f in ${JAR_DIR}; do
  CLASSPATH=${CLASSPATH}":"$f
done

CLASS_NAME=com.zhy.yisql.StreamApp

echo $JAVA_RUN -cp $CLASSPATH $CLASS_NAME $@
echo "yisql starting ..."
nohup $JAVA_RUN -cp $CLASSPATH $CLASS_NAME $@ &