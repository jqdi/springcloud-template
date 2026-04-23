# 请先使用根目录docker/Dockerfile构建基础镜像
FROM springcloud-template/base-image:v1

# 将jar文件添加到容器中并更名为app.jar
COPY target/template-*.jar app.jar

# 暴露容器内的端口给外部访问，也可以在docker run中使用'-p 外端口:内端口'指定
EXPOSE 9050

# 使用startup.sh脚本作为主进程
COPY bin/startup.sh startup.sh

# 在容器启动时执行启动脚本
ENTRYPOINT ["sh","startup.sh","start","app.jar"]
