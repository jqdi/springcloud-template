# CacheManager 模块

CacheManager 模块面向 Spring Cache 规范，适合通过注解为方法增加声明式缓存，常用注解包括 `@Cacheable`、`@CachePut`、`@CacheEvict`。

## 概述

本模块用于启用 Spring Cache，并在使用 Redis 作为缓存实现时定制 `RedisCacheManager` 的默认配置：

- 通过 `@EnableCaching` 启用 Spring Cache 注解能力。
- 当 `spring.cache.type=redis` 时注册 `RedisCacheManagerBuilderCustomizer`。
- 将 Redis 缓存值序列化器设置为 `RedisSerializer.string()`，即按字符串写入和读取缓存值。
- 根据 `spring.cache.redis.*` 配置应用过期时间、缓存键前缀、是否缓存空值等策略。

Spring Boot 仍会根据 `spring.cache.type` 自动装配对应的 CacheManager；本模块只额外提供 Redis 场景下的定制配置。

## 核心组件

### [CacheManagerAutoConfiguration](CacheManagerAutoConfiguration.java)

缓存基础配置类，声明了：

- `@Configuration(proxyBeanMethods = false)`
- `@EnableCaching`

用于开启 Spring Cache 的注解代理能力。

### [RedisCacheManagerConfig](RedisCacheManagerConfig.java)

Redis CacheManager 定制类，仅在以下配置生效时注册：

```yaml
spring:
  cache:
    type: redis
```

该类注册 `RedisCacheManagerBuilderCustomizer`，并把缓存值序列化方式设置为：

```java
RedisSerializer.string()
```

因此当前配置适合缓存字符串结果。代码中的 `GenericJackson2JsonRedisSerializer` 仅保留了导入和注释，并未实际启用；不要把当前行为描述为 JSON 序列化。

## 配置启用

在应用的 `application.yml` 中导入框架提供的配置：

```yaml
spring:
  profiles:
    include: cachemanager
```

框架的 `application-cachemanager.yml` 会在 `dev`、`test`、`pre`、`prod`、`prod-zone1`、`prod-zone2` Profile 下设置 Redis 缓存。应用还需要激活上述业务环境 Profile 之一，例如：

```yaml
spring:
  profiles:
    active: dev
    include: cachemanager
```

默认 Redis 缓存配置如下：

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `spring.cache.type` | `redis` | 使用 Redis CacheManager |
| `spring.cache.redis.timeToLive` | `10m` | 默认缓存存活时间，`10m` 表示 10 分钟 |
| `spring.cache.redis.cacheNullValues` | `true` | 是否缓存空值 |
| `spring.cache.redis.useKeyPrefix` | `true` | 是否使用缓存键前缀 |
| `spring.cache.redis.keyPrefix` | 空 | 全局缓存键前缀 |
| `spring.cache.redis.enableStatistics` | `false` | 是否启用统计 |

也可以在应用自己的配置文件中覆盖，例如：

```yaml
spring:
  cache:
    type: redis
    redis:
      timeToLive: 30m
      cacheNullValues: false
      useKeyPrefix: true
```

如果使用 Caffeine，可以改为：

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=10m
```

## 在业务代码中使用

### 查询并缓存字符串结果

```java
@Service
public class UserService {

    @Cacheable(value = "users:name", key = "#id")
    public String getUserNameById(Long id) {
        // 查询数据库并返回字符串结果
        return userRepository.findNameById(id);
    }
}
```

### 更新缓存

```java
@Service
public class UserService {

    @CachePut(value = "users:name", key = "#user.id")
    public String updateUserName(User user) {
        userRepository.updateName(user.getId(), user.getName());
        return user.getName();
    }
}
```

### 删除缓存

```java
@Service
public class UserService {

    @CacheEvict(value = "users:name", key = "#id")
    public void evictUserName(Long id) {
        // 删除业务数据或执行其他操作；方法执行成功后删除对应缓存
    }

    @CacheEvict(value = "users:name", allEntries = true)
    public void evictAllUserNames() {
        // 清空 users:name 缓存名称下的全部缓存键
    }
}
```

## 注意事项

1. Redis 定制配置要求显式设置 `spring.cache.type=redis`，`RedisCacheManagerConfig` 不会在缺少该配置时默认生效。
2. 当前 Redis 值序列化器是字符串序列化器，注解方法的返回值应能按字符串写入缓存；如需直接缓存复杂对象，需要先调整序列化器实现。
3. Spring Cache 注解基于代理生效，应通过其他 Bean 调用被注解的公开方法；在同类中直接调用该方法不会触发缓存切面。
4. `spring.cache.*` 用于配置 Spring Cache；[Cache 模块](../cache/README.md)中的 `template.enable.cache=cachemanager` 用于向自定义 `ICache` 接口适配 CacheManager，两者用途不同。
