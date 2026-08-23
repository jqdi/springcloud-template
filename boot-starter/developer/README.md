# Developer Route Starter 使用指南

## 简介

Developer Route Starter 是一个基于 Spring Cloud LoadBalancer 的扩展模块，用于在微服务联调场景下，将请求流量自动路由到当前开发者本地的服务实例。

该 Starter 解决了一个长期存在的联调痛点：**多个开发者同时联调同一个微服务集群时，A 改的代码会被 B 的请求打到，互相干扰、互相覆盖数据**。开启本模块后，每个请求都会通过 HTTP Header 识别出"发起人"，再结合服务实例 metadata 中的 `developer` 标签，把流量精准地路由到该开发者自己的本地实例上。

核心功能（详见 [package-info.java](src/main/java/com/company/developer/package-info.java)）：

1. **环境流量路由到开发本地**：让开发人员可以在本地进行调试和测试；
2. **共享联调环境**：无需在本地启动所有没有代码改动的服务，开发人员可以利用环境上已有的服务配合本地改动的服务跑通完整流程。

## 功能特性

### 1. 流量按开发者隔离
通过 HTTP 请求头识别当前请求所属的开发者，将流量优先路由到该开发者本地的服务实例，避免多人联调时互相干扰。

### 2. 分级优先级策略
基于 `ServicePriorityPolicy` 策略链，对候选服务实例按优先级排序，默认顺序如下：

| 策略实现 | order | serverOrder | 命中条件 | 说明 |
| --- | --- | --- | --- | --- |
| `DeveloperSelfPriorityPolicy` | 100 | 1000 | 实例 metadata 中 `developer` 命中当前请求头中的开发者标识 | 当前开发者自己的本地实例，最高优先级 |
| `OnLineServicePriorityPolicy` | 200 | 2000 | 实例 metadata 中 `developer_route_tag` 包含 `ONLINE` | 环境上的在线实例，次优先（兜底联调用） |
| `OtherDeveloperPriorityPolicy` | 300 | 20000 | 实例 metadata 中 `developer` 不属于当前开发者 | 其他开发者的本地实例，最低优先级，避免互相干扰 |
| `DefaultServicePriorityPolicy` | `Integer.MAX_VALUE` | 10000 | 兜底策略 | 未命中任何策略时的默认优先级 |

策略命中规则：`ServicePriorityPolicyManager` 按 `order` 升序遍历策略，第一个 `support()` 返回 `true` 的策略生效；若全部不命中，则使用 `DefaultServicePriorityPolicy`。

### 3. 注入式 Supplier 链扩展
通过 `BeanPostProcessor` 将 [DeveloperServiceInstanceListSupplier](src/main/java/com/company/developer/DeveloperServiceInstanceListSupplier.java) 插入到 Spring Cloud LoadBalancer 的 Supplier 链中，无需改变现有配置即可生效。

```
原链：RetryAware -> Caching -> DiscoveryClient
新链：RetryAware -> Developer -> Caching -> DiscoveryClient
```

### 4. 按环境启停
通过 `developer.enabled` 控制开关，dev/test/pre 环境默认开启，prod 环境强制关闭（详见 [application-developer.yml](src/main/resources/application-developer.yml)），避免误把生产流量路由到本地。

## 快速开始

### 1. 添加依赖

在您的项目的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-developer</artifactId>
    <version>${boot-starter-developer.version}</version>
</dependency>
```

### 2. 引入默认配置

在 `application.yml` 中通过 `spring.profiles.include` 引入默认配置：

```yaml
spring:
  profiles:
    include: developer
```

默认配置按 profile 区分环境：

```yaml
# dev / test / pre 环境：开启
developer:
  enabled: true
  headers: x-deviceid,x-current-user-id

# prod 环境：强制关闭
developer:
  enabled: false
  headers: x-deviceid,x-current-user-id
```

**如需自定义配置**：复制 [application-developer.yml](src/main/resources/application-developer.yml) 到你的模块的 `resources` 目录下，并按需修改。

### 3. 配置服务实例 metadata

要让流量精准路由到本地，本地启动的服务实例需要向注册中心上报 metadata：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        metadata:
          # 标识当前实例归属的开发者，与请求头中的值匹配
          developer: zhangsan
          # 可选：标记为 ONLINE 联调实例，供其他开发者兜底调用
          developer_route_tag: ONLINE
```

### 4. 在请求中携带开发者标识

客户端发起请求时，需要带上 `developer.headers` 中声明的请求头，模块将从中提取开发者标识：

```http
GET /api/user/info
x-deviceid: zhangsan
x-current-user-id: zhangsan
```

只要任一请求头有值，模块就会用这些值去匹配服务实例 metadata 中的 `developer` 字段。

## 工作原理

### 1. 路由匹配流程

```text
请求 -> LoadBalancer
  -> DeveloperServiceInstanceListSupplier.get(request)
     -> 从 RequestDataContext 取出 HttpHeaders
     -> 按 developer.headers 配置提取开发者标识列表 developerList
     -> 调用 ServicePriorityPolicyManager.serverOrder(instance, developerList)
        -> 遍历策略链，找到第一个 support() 为 true 的策略
        -> 返回该策略的 serverOrder 作为该实例的排序值
     -> 按 serverOrder 分组，取最小值对应的一组实例返回
```

### 2. 关键源码

- 自动装配入口：[DeveloperRouteAutoConfiguration](src/main/java/com/company/developer/DeveloperRouteAutoConfiguration.java)、[ServicePriorityPolicyAutoConfiguration](src/main/java/com/company/developer/ServicePriorityPolicyAutoConfiguration.java)
- Supplier 链插入器：[DeveloperServiceInstanceListConfiguration](src/main/java/com/company/developer/DeveloperServiceInstanceListConfiguration.java)
- 流量过滤主体：[DeveloperServiceInstanceListSupplier](src/main/java/com/company/developer/DeveloperServiceInstanceListSupplier.java)
- 策略管理器：[ServicePriorityPolicyManager](src/main/java/com/company/developer/policy/ServicePriorityPolicyManager.java)
- 内置策略实现：[DeveloperSelfPriorityPolicy](src/main/java/com/company/developer/policy/impl/DeveloperSelfPriorityPolicy.java)、[OnLineServicePriorityPolicy](src/main/java/com/company/developer/policy/impl/OnLineServicePriorityPolicy.java)、[OtherDeveloperPriorityPolicy](src/main/java/com/company/developer/policy/impl/OtherDeveloperPriorityPolicy.java)、[DefaultServicePriorityPolicy](src/main/java/com/company/developer/policy/impl/DefaultServicePriorityPolicy.java)

## 高级配置

### 1. 自定义请求头

`developer.headers` 支持多个请求头，以英文逗号分隔。模块会依次从这些 Header 中取值并合并作为开发者标识：

```yaml
developer:
  enabled: true
  headers: x-deviceid,x-current-user-id,x-employee-id
```

### 2. 自定义优先级策略

实现 [ServicePriorityPolicy](src/main/java/com/company/developer/policy/ServicePriorityPolicy.java) 接口，并将其声明为 Spring Bean 即可自动加入策略链：

```java
@Component
public class MyPriorityPolicy implements ServicePriorityPolicy {

    @Override
    public boolean support(ServiceInstance serviceInstance, List<String> developerList) {
        // 自定义命中逻辑
        return true;
    }

    @Override
    public int serverOrder(ServiceInstance serviceInstance) {
        // 返回优先级，数值越小优先级越高
        return 500;
    }

    @Override
    public int getOrder() {
        // 策略遍历顺序，数值越小越优先匹配
        return 150;
    }
}
```

> 内置策略使用 `@ConditionalOnMissingBean` 装配，若要完全替换某个内置策略，只需在容器中注册一个同类型 Bean 即可覆盖。

### 3. 自定义默认策略

如需替换兜底的 `DefaultServicePriorityPolicy`，可通过 `ServicePriorityPolicyManager.setDefaultServicePriorityPolicy(...)` 注入：

```java
@Bean
public ServicePriorityPolicyManager customManager(List<ServicePriorityPolicy> policies) {
    ServicePriorityPolicyManager manager = new ServicePriorityPolicyManager(policies);
    manager.setDefaultServicePriorityPolicy(new MyDefaultPolicy());
    return manager;
}
```

## 注意事项

1. **生产环境必须关闭**：`developer.enabled` 在 prod 环境必须保持为 `false`，否则可能将生产流量错误路由到本地实例。该模块默认在 [application-developer.yml](src/main/resources/application-developer.yml) 中已按 profile 做好环境隔离。
2. **依赖 Spring Cloud LoadBalancer**：模块基于 Spring Cloud LoadBalancer 的 `ServiceInstanceListSupplier` 扩展机制实现，仅在使用 LoadBalancer 作为客户端负载均衡器的项目中生效（不兼容 Ribbon）。
3. **请求头必须由网关/调用方透传**：开发者标识依赖 HTTP Header，需确保网关或上游服务在转发请求时保留 `developer.headers` 中声明的请求头。
4. **metadata 字段约定**：服务实例 metadata 中的 `developer` 字段用于标识实例归属开发者，`developer_route_tag=ONLINE` 用于标记可被其他开发者兜底调用的环境实例，注册到注册中心时务必带上。
5. **未携带请求头时**：当请求未带任何开发者标识时，模块会跳过过滤逻辑，直接返回上游 Supplier 提供的原始实例列表，不影响正常调用。
6. **与灰度发布共用负载均衡器**：开发者路由与灰度发布策略共享同一套 LoadBalancer 与策略链，二者通过 metadata 标签区分，互不冲突。
