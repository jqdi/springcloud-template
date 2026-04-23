# 日志追踪-MDC

## 概述

MDC（Mapped Diagnostic Context，映射调试上下文）是Logback和Log4j等日志框架提供的一个便利功能，用于在多线程环境下区分不同的日志输出。

## 主要功能

- 在日志中添加上下文信息，如请求ID、用户ID等
- 便于在复杂的分布式系统中追踪单个请求的完整流程
- 解决多线程环境下日志混乱的问题

## 解决方案

### ForkJoinPool日志ID传递问题

在使用ForkJoinPool（如parallelStream、CompletableFuture）时，由于线程切换可能导致日志ID传递中断。可以通过以下方式解决：

- 使用TransmittableThreadLocal解决ForkJoinPool日志ID传递问题
- 启动参数添加：`-javaagent:path/to/transmittable-thread-local-2.14.5.jar`

## 配置说明

### 启动参数配置
在应用启动参数中添加以下参数：

```bash
-javaagent:path/to/transmittable-thread-local-2.14.5.jar
```

### 日志格式配置
在日志配置文件中加入MDC变量，例如：

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{36} - %msg%n</pattern>
```

## 使用场景

- 微服务架构下的请求链路追踪
- 并发处理中的日志关联
- 分布式系统中的问题定位

## 注意事项

- 确保在请求结束时清理MDC中的上下文信息，避免内存泄漏
- TransmittableThreadLocal需要在应用启动时通过Java Agent方式加载
- 版本兼容性需要注意，确保与JDK版本匹配

## 扩展阅读

- 详细了解MDC原理及实现机制
- 探索与其他链路追踪工具（如SkyWalking、Zipkin）的集成方案