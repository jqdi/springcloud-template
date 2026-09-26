# framework 模块

## 与 boot-starter 模块区别
#### 1. 只有部分模块适用的公共代码，放在boot-starter，按需引用
#### 2. 全部模块都适用的公共代码，放在framework，减少引用工作
#### 3. 本质上全部模块都适用的公共代码也可以放在boot-starter，然后在framework中引用boot-starter

## 简介

framework 模块是整个微服务架构的核心基础框架，提供了一系列通用的自动化配置和基础设施功能。该模块基于 Spring Boot 和 Spring Cloud 构建，集成了微服务开发中常用的组件和工具，旨在简化微服务应用的开发，提高开发效率和代码质量。

## 核心功能

### 1. 自动配置
- Web MVC 配置
- 拦截器统一管理
- HTTP 消息转换器配置
- Redis 自动配置

### 2. 缓存管理
支持多种缓存实现：
- Redis 缓存
- Caffeine 本地缓存

### 3. 分布式锁
提供多种分布式锁实现：
- JVM 锁（单机环境）
- Redisson 分布式锁（集群环境）

### 4. 消息处理
- 国际化消息处理
- 消息驱动（RabbitMQ、RocketMQ、Redis Stream）

### 5. 线程池管理
- 自定义线程池配置
- 线程上下文传递
- 日志追踪ID传递

### 6. 熔断器集成
- Resilience4J 熔断器
- Sentinel 熔断器

### 7. 链路追踪
- Trace ID 生成与传递
- Skywalking 集成
- 日志追踪上下文管理

### 8. 服务治理
- Eureka/Nacos 注册发现
- Apollo/Nacos 配置中心
- 健康检查

## 模块结构

```
framework/
├── autoconfigure/          # 自动配置类
├── cachemanger/            # 缓存相关组件
├── canal/                  # 数据库变更监听
├── check/                  # 健康检查
├── circuitbreaker/         # 熔断器集成
├── config/                 # 配置中心集成
├── constant/               # 常量定义
├── context/                # 上下文管理
├── developer/              # 开发者工具
├── discovery/              # 服务发现集成
├── feign/                  # Feign 客户端配置
├── filter/                 # 过滤器
├── globalresponse/         # 全局响应处理
├── gracefulshutdown/       # 优雅关闭
├── i18n/                   # 国际化消息处理
├── lock/                   # 分布式锁
├── messagedriven/          # 消息驱动
├── sequence/               # 序列生成器
├── threadpool/             # 线程池管理
├── trace/                  # 链路追踪
└── util/                   # 工具类
```

## 主要组件详解

### 1. 缓存组件
提供统一的缓存接口 [ICache](src/main/java/com/company/framework/cache/ICache.java)，支持 Redis、Guava 和组合缓存三种实现。

```java
// 注入缓存实例
@Autowired
private CacheManager cacheManager;

// 使用缓存
Cache cache = cacheManager.getCache(Constants.CacheName.USER_INFO);
String value = cache.get(appCode, () -> appVersionService.selectLastByAppCode(appCode));
```

### 2. 分布式锁
通过 [@Lock](src/main/java/com/company/framework/lock/annotation/Lock.java) 注解实现分布式锁：

```java
@Service
public class BusinessService {
    
    @Lock(key = "business_lock_#{#param}", expireTime = 60)
    public void businessMethod(String param) {
        // 业务逻辑
    }
}
```

### 3. 消息驱动
支持多种消息中间件：
- RabbitMQ
- RocketMQ
- Redis Stream

提供统一的消息发送接口 [MessageSender](src/main/java/com/company/framework/messagedriven/MessageSender.java)。

### 4. 线程池
提供可配置的线程池 [ThreadPoolTaskExecutor](src/main/java/com/company/framework/threadpool/ThreadPoolAutoConfiguration.java)，
支持上下文传递和日志追踪ID传递。

### 5. 熔断器
集成 Resilience4J 和 Sentinel，提供服务容错能力。

### 6. 链路追踪
通过 [TraceManager](src/main/java/com/company/framework/trace/TraceManager.java) 管理请求链路，
确保在分布式环境下能够追踪完整的调用链。

## 配置说明

模块提供了丰富的配置选项，主要配置文件位于 [resources](src/main/resources) 目录下：

- [application-feign.yml](src/main/resources/application-feign.yml) - Feign 客户端配置
- [application-redis.yml](src/main/resources/application-redis.yml) - Redis 配置
- [application-rabbitmq.yml](src/main/resources/application-rabbitmq.yml) - RabbitMQ 配置
- [application-rocketmq.yml](src/main/resources/application-rocketmq.yml) - RocketMQ 配置
- [application-resilience4j.yml](src/main/resources/application-resilience4j.yml) - Resilience4J 配置
- [bootstrap-eureka.yml](src/main/resources/bootstrap-eureka.yml) - Eureka 配置
- [bootstrap-nacos-discovery.yml](src/main/resources/bootstrap-nacos-discovery.yml) - Nacos 发现配置
- [bootstrap-nacos-config.yml](src/main/resources/bootstrap-nacos-config.yml) - Nacos 配置中心

## 使用方法

### 1. 添加依赖
在需要使用框架功能的项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>template-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用功能
通过配置文件启用相应功能：

```yaml
template:
  enable:
    lock: redisson # 启用Redisson分布式锁
    message-driven: rabbitmq # 启用RabbitMQ消息驱动
```

### 3. 使用组件
直接注入所需的组件即可使用：

```java
@Service
public class DemoService {
    
    @Autowired
    private CacheManager cacheManager; // 缓存组件
    
    @Autowired
    private LockClient lockClient; // 锁组件
    
    @Autowired
    private MessageSender messageSender; // 消息发送组件
}
```

## 设计理念

### 1. 开箱即用
通过自动配置机制，大部分功能只需添加依赖和简单配置即可使用。

### 2. 可插拔架构
各个组件之间松耦合，可以根据项目需求选择性使用。

### 3. 统一抽象
对同类功能提供统一的接口抽象，降低学习和使用成本。

### 4. 易于扩展
提供丰富的扩展点，支持自定义实现。

## 最佳实践

### 1. 配置管理
建议通过外部配置中心（Apollo/Nacos）管理配置，而非硬编码在代码中。

### 2. 组件选择
根据实际业务场景选择合适的组件实现，如：
- 单机环境使用 JVM 锁
- 集群环境使用 Redisson 分布式锁
- 高并发场景使用组合缓存

### 3. 监控告警
启用 Actuator 和 Prometheus 监控，及时发现系统问题。
