# 使用官方的OpenJDK 8镜像作为基础镜像
FROM openjdk:8-jre-slim

# 指定时区
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装一些镜像中没有的软件
RUN apt-get update && apt-get install -y less vim net-tools procps

# 数据卷，挂载日志目录到宿主机，docker run加上参数‘-v 宿主机目录:/logs’，如：‘-v /template/logs:/logs’
VOLUME /logs

# 将jar文件添加到容器中并更名为app.jar
COPY target/template-*.jar app.jar

# 暴露容器内的端口给外部访问，也可以在docker run中使用‘-p 外端口:内端口’指定
EXPOSE 7010

# 使用startup.sh脚本作为主进程
COPY bin/startup.sh startup.sh

# 在容器启动时执行启动脚本
ENTRYPOINT ["sh","startup.sh","start","app.jar"]
