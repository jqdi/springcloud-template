# 日志追踪-SkyWalking

## 概述

Apache SkyWalking是一款开源的应用性能监控（APM）工具，主要用于分布式系统的链路追踪、性能监控和故障诊断。

## 安装部署

### 下载地址
- SkyWalking Server: https://archive.apache.org/dist/skywalking/
- Java Agent: https://archive.apache.org/dist/skywalking/java-agent/

### 项目存储位置
- 本项目存放于：`cicd/plugins/skywalking-agent`

## 服务端配置

### 启动SkyWalking服务
```bash
cd /path/to/apache-skywalking-apm-bin-8.2.0/bin
./startup.sh
```

## 客户端配置

### 启动参数配置
在应用启动参数中添加以下参数：

```bash
-javaagent:/path/to/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=springcloud-template::{服务名} -Dskywalking.collector.backend_service=127.0.0.1:11800
```

### 示例配置
```bash
-javaagent:/opt/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=springcloud-template::template-web -Dskywalking.collector.backend_service=127.0.0.1:11800
```

## 插件配置

### 插件激活说明
注意：`/bootstrap-plugins`、`/optional-plugins`、`/optional-reporter-plugins`目录下的插件需要复制到`/plugins`目录下才会生效。

### Bootstrap插件配置
根据官方文档，Bootstrap类插件包括：
- JDK HttpURLConnection插件
- JDK Callable and Runnable插件  
- JDK ThreadPoolExecutor插件
- JDK ForkJoinPool插件

这些插件都是可选的，需要手动从bootstrap-plugins目录复制到plugins目录下才能生效。

### 特定场景插件配置

#### 1. 线程(new Thread)日志ID传递
将 `/skywalking-agent/bootstrap-plugins/apm-jdk-threading-plugin-8.16.0.jar` 复制到 `/plugins` 目录（经测试，可能不起效果，需进一步验证）

#### 2. 线程池(ThreadPoolExecutor、ThreadPoolTaskExecutor)日志ID传递
将 `/skywalking-agent/bootstrap-plugins/apm-jdk-threadpool-plugin-8.16.0.jar` 复制到 `/plugins` 目录

#### 3. ForkJoinPool（parallelStream、CompletableFuture）线程池日志ID传递
将 `/skywalking-agent/bootstrap-plugins/apm-jdk-forkjoinpool-plugin-8.16.0.jar` 复制到 `/plugins` 目录

#### 4. XXL-Job日志ID传递
`/plugins` 目录已包含 `apm-xxl-job-2.x-plugin-8.16.0.jar`

#### 5. RabbitMQ日志ID传递
`/plugins` 目录已包含 `apm-rabbitmq-plugin-8.16.0.jar`

**RabbitMQ插件问题说明：**
SkyWalking官方提供的RabbitMQ插件存在缺陷，它只对RabbitMQ官方原生Client实现扩展。但在项目中通常不直接使用原生Client，而是使用Spring RabbitMQ Client。由于Spring RabbitMQ Consumer中存在跨线程操作，导致跟踪ID断链。

**解决方案：**
使用社区提供的增强插件：
- 项目地址：https://github.com/Aas-ee/skywalking-apm-sniffer
- 将构建好的插件 `apm-custom-rabbitmq-5.x-plugin-8.14.0.jar` 复制到 `/plugins` 目录

#### 6. RocketMQ日志ID传递
`/plugins` 目录已包含 `apm-rocketMQ-5.x-plugin-8.16.0.jar`

#### 7. Gateway网关日志ID打印
将以下文件从 `/skywalking-agent/optional-plugins` 复制到 `/plugins` 目录：
- `apm-spring-webflux-5.x-plugin-8.16.0.jar`
- `apm-spring-cloud-gateway-3.x-plugin-8.16.0.jar`

**说明：** 经测试，网关插件可能不起效果，这可能是SkyWalking 8.16.0兼容问题，升级版本后可能可解决。临时解决方案可参考项目中的`SkywalkingTraceFilter`。

## 测试验证

- 测试用例：`TraceIdController`

## 注意事项

- 插件激活需要将相应jar包移动到plugins目录
- 不同版本可能存在兼容性问题
- 生产环境中需要合理配置资源限制
- SkyWalking 8.16.0版本可能存在某些插件兼容问题

## 参考资料

- Bootstrap插件官方文档：https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/bootstrap-plugins/#bootstrap-class-plugins
