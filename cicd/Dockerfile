# 使用官方的OpenJDK 8镜像作为基础镜像
FROM openjdk:8-jre-slim

# 指定时区
ENV TZ=Asia/Shanghai LANG=C.UTF-8 LC_ALL=C.UTF-8
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装一些镜像中没有的软件
RUN apt-get update && apt-get install -y less vim net-tools procps

# 数据卷，挂载日志目录到宿主机，docker run加上参数‘-v 宿主机目录:/logs’，如：‘-v /template/logs:/logs’
VOLUME /logs

# 将依赖的插件添加到容器中
RUN mkdir -p plugins
COPY plugins plugins
