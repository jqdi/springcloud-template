# Config Starter 使用指南（配置中心）

## 简介

Config Starter 用于接入配置中心并实现**配置的动态刷新**：配置中心改了值，应用无需重启、也无需加 `@RefreshScope`，`@Value` 与 `@ConfigurationProperties` 都能自动拿到新值。模块同时支持 **Nacos**、**Apollo**、**Spring Cloud Config** 三种配置中心，并附带对应的 bootstrap 配置模板。

该模块从 framework 与 gateway 中下沉而来，原先两处各维护一份 config 代码，现在只维护一套，按需引用。**它不承载配置本身**，只负责"配置变更后如何让应用感知"。

## 功能特性

### 1. `@Value` 动态刷新（**不需要** `@RefreshScope`）

这是本模块与常见做法最大的差别：**普通 bean 上的 `@Value` 就能随配置中心刷新，不必把 bean 改成 `@RefreshScope`**。

```java
@Component // 普通 bean，不加任何注解
public class SmsPropertiesHolder {
    @Value("${sms.daily-limit:1000}")
    private Integer dailyLimit; // 配置中心改动后，这里会被自动重新注入
}
```

两条链路都绕开了 `refresh` 作用域，无需作用域代理（也就没有"拿到的是代理对象"、懒加载、`@PostConstruct` 时机变化这类额外复杂度）：

- **Nacos**：`SpringValueAutoRefreshProcessor` 收到变更事件后，对登记过的 bean 原地重新注入一遍 `@Value` 字段 / setter；
- **Apollo**：Apollo 自带的 `AutoUpdateConfigChangeListener` 把新值写回 `@Value` 字段。

> 刷新是**按 bean 整体重新注入**，不是按 key 精确更新；只改注入点，不重建 bean，也不会重跑 `@PostConstruct`。

### 2. `@ConfigurationProperties` 动态刷新

- **Nacos**：由 Spring Cloud 的 `ContextRefresher` 派生刷新（阿里自带的 `NacosContextRefresher` 触发）；
- **Apollo**：本模块的 `PropertiesRefresher` 发布 `EnvironmentChangeEvent` 驱动刷新。

### 3. 配置变更日志

会打印每个变更项的类型、key 以及变更前后的值，便于排查"配置到底有没有生效"：

```text
changed:ADDED sms.daily-limit null -> 2000   # Nacos: LogValueConfigChangeListener / Apollo: PropertiesRefresher
changed keys: [template-app.yaml]            # Nacos: SpringValueAutoRefreshProcessor
changed keys refresh finish
```

Nacos 侧之所以要自己写监听器，是因为 Spring Cloud 的 `RefreshEventListener` **只打印变更的 key，不打印变更前后的 value**。

### 4. 自动装配、按需生效

两个自动配置类通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 装配：

| 自动配置类 | 生效条件 |
| --- | --- |
| [NacosAutoConfiguration](src/main/java/com/company/config/NacosAutoConfiguration.java) | `spring.cloud.nacos.config.enabled` 为 `true` 或未配置（`matchIfMissing = true`） |
| [ApolloAutoConfiguration](src/main/java/com/company/config/ApolloAutoConfiguration.java) | `apollo.bootstrap.enabled` 为 `true` |

用 Nacos 的项目只会装上 Nacos 的监听器，用 Apollo 的项目只会装上 Apollo 的刷新器，互不干扰。

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-config</artifactId>
    <version>${boot-starter-config.version}</version>
</dependency>
```

> 本模块的 [pom.xml](pom.xml) 同时声明了三个配置中心客户端，属于演示性质，**实际项目按需保留其中一个**。

### 2. 引入 bootstrap 配置模板

三份模板均已按 dev/test/pre/prod 分环境：

| 模板文件 | 用途 | `spring.profiles.include` | 模板内开关默认值 |
| --- | --- | --- | --- |
| [bootstrap-nacos-config.yml](src/main/resources/bootstrap-nacos-config.yml) | Nacos 配置中心 | `nacos-config` | dev 关闭，test/pre/prod 开启 |
| [bootstrap-apollo.yml](src/main/resources/bootstrap-apollo.yml) | Apollo 配置中心 | `apollo` | 各环境均关闭 |
| [bootstrap-config.yml](src/main/resources/bootstrap-config.yml) | Spring Cloud Config 配置中心 | `config` | 各环境均关闭 |

复制需要的模板到自己的 `resources` 目录，在 `bootstrap.yml` 中引入：

```yaml
spring:
  application:
    name: template-app
  profiles:
    active: dev # dev,test,pre,prod
    include: nacos-config # 引入 bootstrap-*.yml
```

并在 `application.yml` 中确保 bootstrap 被加载（Spring Boot 2.7.x 起不再自动加载 `bootstrap.yml`）：

```yaml
spring:
  config:
    import: classpath:bootstrap.yml
```

### 3. 打开开关

```yaml
# Nacos
spring.cloud.nacos.config.enabled: true
# Apollo
apollo.bootstrap.enabled: true
# Spring Cloud Config
spring.cloud.config.enabled: true
```

## 核心组件

### Nacos 侧（均在 [NacosAutoConfiguration](src/main/java/com/company/config/NacosAutoConfiguration.java) 中以 `@Bean` 装配）

| 组件 | 职责 |
| --- | --- |
| [SpringValueAutoRefreshProcessor](src/main/java/com/company/config/nacos/SpringValueAutoRefreshProcessor.java) | `@Value` 自动刷新的核心：登记含 `@Value` 的 bean，变更时对它们重新注入（标了 `@Role(ROLE_INFRASTRUCTURE)`） |
| [EnvironmentChangeListener](src/main/java/com/company/config/nacos/EnvironmentChangeListener.java) | 监听 `EnvironmentChangeEvent`，把变更的 key 交给刷新处理器 |
| [LogValueConfigChangeListener](src/main/java/com/company/config/nacos/LogValueConfigChangeListener.java) | 打印变更前后值；同时在装配时向 Nacos 注册该 Data ID 的监听器 |

### Apollo 侧

| 组件 | 职责 |
| --- | --- |
| [PropertiesRefresher](src/main/java/com/company/config/apollo/PropertiesRefresher.java) | `@ApolloConfigChangeListener` 监听变更，打印变更前后值并发布 `EnvironmentChangeEvent` |

## 配置详解

### Nacos

| 配置项 | 模板值 | 说明 |
| --- | --- | --- |
| spring.cloud.nacos.config.enabled | false | 开关，false 表示不接入 |
| spring.cloud.nacos.config.server-addr | 127.0.0.1:8848 | 配置中心地址 |
| spring.cloud.nacos.config.username / password | nacos / nacos | 用户名 / 密码 |
| spring.cloud.nacos.config.namespace | ${spring.profiles.active} | 命名空间，一般做环境隔离 |
| spring.cloud.nacos.config.group | springcloud-template | Group，一般配置为项目名 |
| spring.cloud.nacos.config.name | ${spring.application.name}.yaml | Data ID，需与 `spring.config.import` 一致 |
| spring.config.import | optional:nacos:${spring.application.name}.yaml | 声明从 Nacos 导入配置，`optional:` 表示读不到时不报错 |

### Apollo

| 配置项 | 模板值 | 说明 |
| --- | --- | --- |
| app.id | ${spring.application.name} | AppId，建议与 `spring.application.name` 一一对应 |
| apollo.meta | http://localhost:8080 | Meta Server 地址，多个用逗号分隔 |
| apollo.bootstrap.enabled | false | 开关 |
| apollo.bootstrap.namespaces | application | 命名空间，多个用逗号分隔 |

> 若监听多个 namespace，`@ApolloConfigChangeListener` 的 `value` 要与 `apollo.bootstrap.namespaces` 保持一致，否则部分命名空间的变更不会触发刷新。

### Spring Cloud Config

| 配置项 | 模板值 | 说明 |
| --- | --- | --- |
| spring.cloud.config.enabled | false | 开关 |
| spring.cloud.config.uri | http://localhost:7030 | config-server 请求路径 |
| spring.cloud.config.name / profile / label | ${spring.application.name} / ${spring.profiles.active} / master | 拉取的 application、profile、分支 |
| spring.config.import | optional:configserver:http://localhost:7030 | 声明从 config-server 导入配置 |

> 该方式即原 `template-config` 模块（端口 7030）的客户端配置；动态刷新体验不如 Apollo/Nacos，建议优先用后两者。

## 动态刷新原理

### Nacos

```text
Nacos 推送变更
 |- 阿里自带：NacosContextRefresher -> RefreshEvent -> ContextRefresher.refresh()
 |    -> 发布 EnvironmentChangeEvent（keys 是 Data ID）-> @ConfigurationProperties 刷新
 |- 本模块：LogValueConfigChangeListener -> 打印 old -> new（不发事件，上面那条链路已发）
          | EnvironmentChangeEvent 到达
      EnvironmentChangeListener -> SpringValueAutoRefreshProcessor.changedKeys()
         -> 对登记过的含 @Value 的 bean 原地重新注入        -> @Value 刷新
```

`SpringValueAutoRefreshProcessor` 继承 `AutowiredAnnotationBeanPostProcessor`，把自动注入类型改成 `@Value`：bean 初始化时记录"哪些 bean 含 `@Value`"，变更时对这批 bean 逐个 `processInjection`。

**为什么不需要 `@RefreshScope`**：`changedKeys` 是对**已存在的 bean 实例**做原地重新注入，不销毁、不重建 bean，也不重跑 `@PostConstruct`，所以用不上 `refresh` 作用域那套「销毁 + 重建」的机制。代价是只改注入点的值。

### Apollo

```text
Apollo 推送变更
  -> PropertiesRefresher(@ApolloConfigChangeListener) 打印旧值 -> 新值
  -> publishEvent(EnvironmentChangeEvent)  -> @ConfigurationProperties 刷新
  -> @Value 由 Apollo 自带的 AutoUpdateConfigChangeListener 刷新
```

## 注意事项

1. **配置中心选一个**：Nacos、Apollo、Spring Cloud Config 建议只保留一个，同时开启多套会增加排查成本。
2. **Nacos 的 Data ID 要对齐**：`spring.cloud.nacos.config.name` 与 `spring.config.import` 不一致会静默读不到配置。
3. **dev 环境下 Nacos 等于"完全不刷新"**：模板中 `spring.cloud.nacos.config.enabled=false`，而 `NacosAutoConfiguration` 的生效条件正是它，**整个自动配置类都不装配，`@Value` 不会刷新**。本地要验证刷新请把开关打开。
4. **`@Value` 不要额外加 `@RefreshScope`**：本模块已经能让普通 bean 的 `@Value` 刷新，叠加 `@RefreshScope` 属于两套机制并用（代理对象上的重新注入可能落不到目标实例，`ContextRefresher` 还会销毁 `refresh` 作用域）。只有当 bean 需要"配置变了就整个重建"时才用 `@RefreshScope`。
5. **`@ConditionalOnProperty` 的 bean 不会刷新**：这类 bean 不会因配置变更重新装配，需要重启。
6. **数据源类配置不要用 `@Value` 接**：`@Value` 刷新只改注入点的值，不会重建 `DataSource`、连接池、线程池这类组件，接了配置中心只会造成"看着改了其实没生效"。
7. **`@Value` 写在父类上时当前不刷新（已知缺陷）**：`SpringValueAutoRefreshProcessor` 用实例字段 `beanNamesNeedRefresh` 同时充当"本类是否含 `@Value`"和"全部待刷新 bean"两个语义，该字段一旦非空就会提前跳出父类扫描，导致 `@Value` 只写在父类的 bean 登记不上。修复方式是把"是否找到"改为方法内局部变量。
8. **`@Value("${x}")`（无默认值）遇上配置项被删除会抛异常**：重新注入时占位符解析失败会抛 `IllegalStateException` 并中断本轮刷新，给 `@Value` 配默认值（`${x:默认值}`）可规避。
9. **源码里有两处不会被调用的遗留覆写**，改这个类之前先看一眼：`postProcessPropertyValues(...)`（自 Spring 5.3 起被 `postProcessProperties` 取代）与 `setOrder(int)`（无任何调用点、且会把顺序设成比父类默认值更晚）。删掉或修正它们不会影响现有刷新行为。
