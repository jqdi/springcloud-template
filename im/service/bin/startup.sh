#!/bin/bash

# 启动脚本
# 前置准备：
# 1. 准备好服务器根目录'../应用根目录/template-im'
# 2. 将本脚本复制到'应用根目录/template-im/startup.sh'
# 3. 复制cicd/plugins/到'应用根目录/template-im/plugins/'
# 4. 将template-im.jar上传至'应用根目录/template-im/template-im.jar'


# 项目名，建议与项目名保持一致
PROJECT="springcloud-template"
# 应用名，建议与spring.application.name保持一致
MODULE="template-im"
# 环境，该值会赋给spring.profiles.active，可选（dev、test、pre、prod）
ENV="dev"
# JMX端口
JMX_PORT=28050
# skywalking服务端IP+地址
SKYWALKING_COLLECTOR_BACKEND_SERVICE=127.0.0.1:11800
# 日志根目录，建议与logging.file.path保持一致
LOG_HOME="./logs"


function get_pid() {
  # 查看是否存在这个进程，返回结果是数字
  pcount=`ps -ef | grep "$APP_JAR" | grep -v "grep" | wc -l`
  if [ $pcount -gt 0 ]; then  #返回的数字不小于0 说明存在进程
    # 获取进程ID 并 杀掉进程
	pid=`ps -ef | grep "$APP_JAR" | grep -v "grep" | awk '{ print $2; }'`
	echo "$pid"
  else
    echo "-1"
  fi
}

function stop_pid() {
  # 查看是否存在这个进程，返回结果是数字
  jar_pid=$(get_pid)
  echo "$APP_JAR进程ID: $jar_pid"
  if [ $jar_pid == "-1" ]
    then
	echo "$APP_JAR" not running;
  else
    kill $jar_pid
    echo "kill $jar_pid 成功"
  fi
}

echo "参数1：【$1】，参数2：【$2】"

# jar位置，可通过第二个参数修改
APP_JAR="$MODULE.jar"
if [ $# -gt 1 ]; then
    APP_JAR="$2"
fi
echo "JAR文件: $APP_JAR"

# 检查JAR文件是否存在
if [ ! -f "$APP_JAR" ]; then
    echo "错误: 找不到JAR文件: $APP_JAR"
    exit 1
fi

if [ $# -eq 0 ]; then
  echo "请输入参数1：start|stop|restart"
  exit 0
fi

echo "开始执行脚本"
if [ $1 == "stop" ]
  then
  echo $APP_JAR  "stopping"
  stop_pid
  exit 0
fi

if [ $1 == "restart" ]
  then
  echo $APP_JAR  "restart"
  stop_pid
  sleep 1
fi

# 创建日志目录
mkdir -p "$LOG_HOME"

# 获取启动进程ID
PID=$$
echo "启动进程ID:$PID"

# JVM参数配置（第一个留空，后续采用拼接方式添加参数）
JVM_OPTS=""

# 设置JVM运行时编码，默认使用操作系统默认编码（Windows中文版:GBK，Linux:通常是UTF-8(取决于LANG环境变量)，macOS:UTF-8）
#JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"

# 避免意外阻塞代码，urandom安全性没有random高，默认：file:/dev/random
JVM_OPTS="$JVM_OPTS -Djava.security.egd=file:/dev/./urandom"

# 最小堆，默认：物理内存1/64（最小1MB），最大堆，默认：物理内存1/4（最大1GB）
JVM_OPTS="$JVM_OPTS -Xms512M -Xmx2048M"
# 新生代大小，默认：堆内存1/3（需显式设置）
JVM_OPTS="$JVM_OPTS -Xmn512M"
# 新老年代分配比例，默认情况下，新生代与老年代比例为1:2。NewRatio默认值是2。如果NewRatio修改成3，那么新生代与老年代比例就是1:3
JVM_OPTS="$JVM_OPTS -XX:NewRatio=2"
# 新生代分配比例，默认情况下Eden、From、To的比例是8:1:1。SurvivorRatio默认值是8，如果SurvivorRatio修改成4，那么其比例就是4:1:1
JVM_OPTS="$JVM_OPTS -XX:SurvivorRatio=8"
# 新生代对象直接进入老年代阈值，默认：15
JVM_OPTS="$JVM_OPTS -XX:MaxTenuringThreshold=15"

# 每个线程的堆栈大小，默认：1M
JVM_OPTS="$JVM_OPTS -Xss1024K"

# 元空间，默认：21MB
JVM_OPTS="$JVM_OPTS -XX:MetaspaceSize=21M"
# 元空间，最大空间，默认：没有限制
#JVM_OPTS="$JVM_OPTS -XX:MaxMetaspaceSize=2048M"
# 在GC之后，最小的Metaspace剩余空间容量的百分比，默认40%，小于此值增大元空间，最大的Metaspace剩余空间容量的百分比，默认70%，大于此值缩小元空间
JVM_OPTS="$JVM_OPTS -XX:MinMetaspaceFreeRatio=40 -XX:MaxMetaspaceFreeRatio=70"

# 使用CMS垃圾回收器，在HotSpot JVM中，这个参数的默认值通常是9。这意味着默认情况下，JVM会尝试将垃圾收集的时间限制在总运行时间的1/(1+9) = 10%以下。换句话说，JVM会尽可能地让程序运行时间占到总时间的90%，而将剩余的10%用于垃圾收集。
JVM_OPTS="$JVM_OPTS -XX:+UseConcMarkSweepGC -XX:GCTimeRatio=9"
# 使用G1垃圾回收器，预期停顿时间，默认：200ms，新生代最小占比，默认：5%，新生代最大占比，默认：60%
#JVM_OPTS="$JVM_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=5 -XX:G1MaxNewSizePercent=60"

# 输出详细GC日志
JVM_OPTS="$JVM_OPTS -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:$LOG_HOME/gc-$PID.log"
# 发生OOM时生成Dump文件
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_HOME/oom-heapdump-$PID.hprof"
# 发生致命错误时，记录错误信息
JVM_OPTS="$JVM_OPTS -XX:ErrorFile=$LOG_HOME/hs_err_pid-$PID.log"
# 发生致命错误时，记录栈信息、堆信息（没起效果，没有加到启动参数里，不知道是不是引号问题）
#JVM_OPTS="$JVM_OPTS -XX:OnError="jstack $PID > $LOG_HOME/error-jstack-$PID.log;jmap -dump:format=b,file=$LOG_HOME/error-heapdump-$PID.hprof $PID""

# 阿里TTL，有功能性支持线程池传递上下文
JVM_OPTS="$JVM_OPTS -javaagent:plugins/ttl/transmittable-thread-local-2.14.5.jar"
# skywalking日志追踪
JVM_OPTS="$JVM_OPTS
-javaagent:plugins/skywalking-agent/skywalking-agent.jar
-Dskywalking.agent.service_name=$PROJECT::$MODULE
-Dskywalking.collector.backend_service=$SKYWALKING_COLLECTOR_BACKEND_SERVICE
"
# jmx监控
JVM_OPTS="$JVM_OPTS -javaagent:plugins/prometheus/jmx_prometheus_javaagent-1.0.1.jar=$JMX_PORT:plugins/prometheus/jmx_prometheus_javaagent-config.yaml"

# 应用参数
APP_OPTS="--spring.profiles.active=$ENV"

echo "正在启动$ENV服务..."

# 后台运行
nohup java $JVM_OPTS -jar $APP_JAR $APP_OPTS > "$LOG_HOME/nohup.out" 2>&1 &

jar_pid=$(get_pid)
echo "$APP_JAR进程ID: $jar_pid"

exit $?
