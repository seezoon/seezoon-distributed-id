#!/bin/bash
set -e
# 不指定这获取环境中java命令
#JAVA_HOME=
MAIN_CLASS="com.seezoon.MainApplication"
# 可加入自定的目录
#CLASS_PATH=""
LOG_PATH="./logs"

#JVM_MEM="-Xmx512m -Xms128m"
if [ "${IN_CONTAINER}" = true ]; then
  JVM_MEM="-XX:+UseContainerSupport -XX:InitialRAMPercentage=10.0 -XX:MaxRAMPercentage=80.0"
fi
#JVM_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
JVM_ARGS="-XX:AutoBoxCacheMax=2000 -XX:+UseStringDeduplication -XX:+UseG1GC -verbose:gc -Xlog:gc*,safepoint:${LOG_PATH}/gc.log:time,uptime:filecount=1,filesize=10M -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -XX:HeapDumpPath=${LOG_PATH}/dump -XX:ErrorFile=${LOG_PATH}/hs_err_%p.log"

#优雅关机等待时间
SHUTDOWN_SECONDS=30
SERVER_OTPS="--spring.profiles.active=cli --server.tomcat.basedir=. --server.tomcat.connection-timeout=5s --server.tomcat.max-connections=2000 --server.tomcat.accept-count=200 --server.tomcat.threads.min-spare=10 --server.tomcat.threads.max=500 --server.shutdown=graceful --spring.lifecycle.timeout-per-shutdown-phase=${SHUTDOWN_SECONDS}s"
# export for java properties
export LOG_PATH
