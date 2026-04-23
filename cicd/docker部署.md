# 构建基础镜像
构建命令：docker build -t springcloud-template/base-image:{version} .
```
demo：docker build -t springcloud-template/base-image:v1 .
```

# 版本日志
### v1
```
1. 基于openjdk:8-jre-slim构建基础镜像
2. 在镜像中创建一个目录存放我们的应用
3. 指定时区
4. 安装一些镜像中没有的软件
5. 将依赖的插件添加到容器中
```

# Dockerfile 说明
```
FROM ：指定基础镜像
MAINTAINER ：维护者信息
RUN ：构建镜像时所需要执行的命令
CMD ：用于容器在真正运行后所需要执行的命令，CMD 和 RUN 其实都是执行命令，但是 CMD 用于最后面 注：每个 Dockerfile 只能有一条 CMD 命令。如果指定了多条 CMD 命令，只有最后一条会被执行。
ENTRYPOINT ：其实和 CMD 差不多，具体区别：CMD # 指定容器启动时要执行的命令，只有最后一个会生效，可被替代 ；ENTRYPOINT # 指定容器启动时 要执行的命令,可以追加命令。注：每个 Dockerfile 只能有一条 ENTRYPOINT命令，如果指定了多条 ENTRYPOINT 命令，只有最后一条会被执行。
ADD ：复制文件或目录到容器，如果是压缩文件，会自动解压
COPY ：拷贝文件到容器内，与 ADD 一样但不能解压
LABEL ：用于为镜像添加元数据，一个标识
ENV ：设置环境变量
EXPOSE ：指定于外界交互的端口
VOLUME ：文件挂载，容器与宿主机之间的文件共享功能，等同于 docker run 时的参数-v
WORKDIR ：工作目录相当于 cd
USER ：指定运行容器时的用户名ARG ：设置变量
ONBUILD ：该指令只有在当该镜像被用作其他镜像的基础镜像时，才会生效
```

# 样例

## 构建镜像demo
```
cd ../eureka
docker build -t template-eureka .
cd ../gateway
docker build -t template-gateway .
cd ../web
docker build -t template-web .
```

## 运行镜像demo
```
docker run --name template-eureka -d -p 7010:7010 template-eureka
docker run --name template-gateway -d -p 7020:7020 template-gateway
docker run --name template-web -d -p 9010:9010 template-web
```
