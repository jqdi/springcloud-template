# Config Starter 使用指南（配置中心）

## 简介

Config Starter 是一个基于 Spring Boot 的自动化配置模块，用于接入配置中心并实现**配置的动态刷新**。模块同时支持 **Nacos**、**Apollo**、**Spring Cloud Config** 三种配置中心，并附带三种配置中心对应的 bootstrap 配置模板。

**该模块不承载配置本身**，只负责"配置变更后如何让应用感知"。因此**不引入配置中心（连一个都不启用）时也不需要本模块**，`@Value` / `@ConfigurationProperties` 走 Spring 原生行为即可。

## 功能特性

### 1. 配置动态刷新

- **Nacos**：`@Value` 由本模块的 `SpringValueAutoRefreshProcessor` 刷新；`@ConfigurationProperties` 由 Spring Cloud 的 `ContextRefresher` 派生刷新
- **Apollo**：`@Value` 由 Apollo 自带的 `AutoUpdateConfigChangeListener` 刷新；`@ConfigurationProperties` 通过本模块发布的 `EnvironmentChangeEvent` 刷新

> 刷新粒度说明：`@Value` 的刷新是**按 bean 整体重新注入**，不是按 key 精确更新（详见「动态刷新原理」与「已知限制」）。

### 2. 配置变更日志

配置中心推送变更时，会打印每个变更项的类型、key 以及变更前后的值，便于排查"配置到底有没有生效"：

```text
# LogValueConfigChangeListener：type key oldValue -> newValue
changed:ADDED template.sqllimit.max null -> 1000
# EnvironmentChangeListener -> SpringValueAutoRefreshProcessor
changed keys: [template-app.yaml]
changed keys refresh finish
```

`changed keys:` 里打出来的**是 Nacos 的 Data ID 而不是具体的配置项 key**：在 config-data 技术栈下，Nacos 抛出的 `EnvironmentChangeEvent` 携带的就是 Data ID（如 `template-app.yaml`）。所以"某个 key 是否被刷新"要看上一行的 from -> to 日志，不能只看 `changed keys:`。

- **Nacos**：由 [LogValueConfigChangeListener](src/main/java/com/company/config/nacos/LogValueConfigChangeListener.java) 打印
- **Apollo**：由 [PropertiesRefresher](src/main/java/com/company/config/apollo/PropertiesRefresher.java) 打印

### 3. 自动装配、按需生效

两个配置类通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 装配，并按开关自动判断是否生效：

| 自动配置类 | 生效条件 | 说明 |
| --- | --- | --- |
| [NacosAutoConfiguration](src/main/java/com/company/config/NacosAutoConfiguration.java) | `spring.cloud.nacos.config.enabled` 为 `true` 或未配置（`matchIfMissing = true`） | 装配 Nacos 相关的监听器 |
| [ApolloAutoConfiguration](src/main/java/com/company/config/ApolloAutoConfiguration.java) | `apollo.bootstrap.enabled` 为 `true` | 装配 Apollo 的刷新器 |

即：用 Nacos 的项目只会装上 Nacos 的监听器，用 Apollo 的项目只会装上 Apollo 的刷新器，互不干扰。

## 快速开始

### 1. 添加依赖

在您的项目的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-config</artifactId>
    <version>${boot-starter-config.version}</version>
</dependency>
```

> 本模块的 [pom.xml](pom.xml) 中同时声明了 `spring-cloud-config-client`、`apollo-client`、`spring-cloud-starter-alibaba-nacos-config` 三个客户端，属于演示性质。**实际项目按需保留其中一个**，避免引入无用的配置中心依赖。

### 2. 引入 bootstrap 配置模板

本模块提供了三份开箱即用的 bootstrap 配置模板（已按 dev/test/pre/prod 分环境）：

| 模板文件 | 用途 | `spring.profiles.include` 取值 |
| --- | --- | --- |
| [bootstrap-nacos-config.yml](src/main/resources/bootstrap-nacos-config.yml) | Nacos 配置中心 | `nacos-config` |
| [bootstrap-apollo.yml](src/main/resources/bootstrap-apollo.yml) | Apollo 配置中心 | `apollo` |
| [bootstrap-config.yml](src/main/resources/bootstrap-config.yml) | Spring Cloud Config 配置中心 | `config` |

复制需要的模板到你的模块的 `resources` 目录下，然后在 `bootstrap.yml` 中引入：

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

### 3. 选择配置中心

在模板中按环境打开对应开关（Nacos/Apollo 为 dev 关闭、test/pre/prod 开启；Spring Cloud Config 各环境默认关闭）：

```yaml
# 使用 Nacos 配置中心
spring:
  cloud:
    nacos:
      config:
        enabled: true # 开关

# 使用 Apollo 配置中心
apollo:
  bootstrap:
    enabled: true # 开关
    namespaces: application

# 使用 Spring Cloud Config 配置中心
spring:
  cloud:
    config:
      enabled: true # 开关
```

## 核心组件

### Nacos 侧

| 组件 | 职责 |
| --- | --- |
| [EnvironmentChangeListener](src/main/java/com/company/config/nacos/EnvironmentChangeListener.java) | 监听 `EnvironmentChangeEvent`，把变更的 key 交给刷新处理器 |
| [LogValueConfigChangeListener](src/main/java/com/company/config/nacos/LogValueConfigChangeListener.java) | 监听 Nacos 配置变更，打印变更前后值（无需再发事件，事件由 Spring Cloud 侧抛出） |
| [SpringValueAutoRefreshProcessor](src/main/java/com/company/config/nacos/SpringValueAutoRefreshProcessor.java) | 实现 `@Value` 字段的自动刷新（只给 Nacos 用，Apollo 无需） |

`LogValueConfigChangeListener` 除了打日志，自己还会向 Nacos 注册一个 Data ID 监听器（用的是 `spring.cloud.nacos.config.name` / `group`）。这与阿里自带的 `NacosContextRefresher` 的注册存在重复：**两者 dataId+group 相同时，先注册的那个生效**。本模块的 Data ID 配置与 `spring.config.import` 一致，因此实际生效的是后者，本模块这个监听器退化为空转——保留它是为了在不走 `spring.config.import` 的老写法下也能生效。

### Apollo 侧

| 组件 | 职责 |
| --- | --- |
| [PropertiesRefresher](src/main/java/com/company/config/apollo/PropertiesRefresher.java) | 通过 `@ApolloConfigChangeListener` 监听变更，打印变更前后值并发布 `EnvironmentChangeEvent`，驱动 `@ConfigurationProperties` 刷新 |

## 配置详解

### 1. Nacos 配置中心

| 配置项 | 示例值 | 说明 |
| --- | --- | --- |
| spring.cloud.nacos.config.enabled | false | 配置中心开关，false 表示不接入 |
| spring.cloud.nacos.config.server-addr | 127.0.0.1:8848 | 配置中心地址 |
| spring.cloud.nacos.config.username / password | | 用户名 / 密码 |
| spring.cloud.nacos.config.file-extension | yaml | 配置文件格式 |
| spring.cloud.nacos.config.namespace | ${spring.profiles.active} | 命名空间，一般做环境隔离 |
| spring.cloud.nacos.config.group | springcloud-template | Group，一般配置为项目名 |
| spring.cloud.nacos.config.name | ${spring.application.name}.yaml | Data ID，需与 `spring.config.import` 中的值一致 |
| spring.config.import | optional:nacos:${spring.application.name}.yaml | 声明从 Nacos 导入配置，`optional:` 表示配置不存在时不报错 |

> Data ID 不加 `.${file-extension}` 后缀可能会读不到配置，`name` 与 `spring.config.import` 两处必须保持一致。

> 如果保留了 `spring.config.import` 却在 dev 环境把 `spring.cloud.nacos.config.enabled` 置为 `false`，Nacos 客户端仍会做"是否漏配 import"的检查。该检查可用 `spring.cloud.nacos.config.import-check.enabled=false` 关闭；本模块的模板已用 `optional:nacos:` 前缀避免启动失败。

### 2. Apollo 配置中心

| 配置项 | 示例值 | 说明 |
| --- | --- | --- |
| app.id | ${spring.application.name} | Apollo 的 AppId，建议与 `spring.application.name` 一一对应 |
| apollo.meta | http://localhost:8080 | Meta Server 地址，多个用英文逗号分隔 |
| apollo.cluster | default | 集群，一般不需要修改 |
| apollo.bootstrap.enabled | false | 开关，false 表示不使用 Apollo |
| apollo.bootstrap.namespaces | application | 命名空间，多个用英文逗号分隔 |

> 若监听了多个 namespace，`@ApolloConfigChangeListener` 的 `value` 需与 `apollo.bootstrap.namespaces` 保持一致，否则部分命名空间的变更不会触发刷新。

### 3. Spring Cloud Config 配置中心

| 配置项 | 示例值 | 说明 |
| --- | --- | --- |
| spring.cloud.config.enabled | false | 配置中心开关，模板中各环境默认关闭 |
| spring.cloud.config.uri | http://localhost:7030 | config-server 的请求路径 |
| spring.cloud.config.name | ${spring.application.name} | 指定拉取配置文件的 application，默认取 `spring.application.name` |
| spring.cloud.config.profile | ${spring.profiles.active} | 拉取的 profile，默认从 `spring.profiles.active` 获取 |
| spring.cloud.config.label | master | 拉取的分支 |
| spring.config.import | optional:configserver:http://localhost:7030 | 声明从 config-server 导入配置；Spring Boot 2.7.x 中引用了 config 会有 configserver 检查，`optional:` 表示连不上时不报错 |

> 该方式即原 `template-config` 模块（端口 7030）配套的客户端配置。配置动态实时刷新体验不如 Apollo/Nacos，官方建议优先使用 Apollo 或 Nacos 做配置中心。

## 动态刷新原理

### 1. Nacos

```text
Nacos 推送变更
 ├─ 阿里自带：NacosContextRefresher
 │    -> 发 RefreshEvent -> RefreshEventListener -> ContextRefresher.refresh()
 │       -> 发布 EnvironmentChangeEvent（keys = Data ID）
 │       -> 重新绑定 @ConfigurationProperties          ← @ConfigurationProperties 在此刷新
 └─ 本模块：LogValueConfigChangeListener
      -> 打印 type key oldValue -> newValue
      （不发事件，事件由上面那条链路发出）
          ↓ EnvironmentChangeEvent 到达
      EnvironmentChangeListener
      -> SpringValueAutoRefreshProcessor.changedKeys(keys)
         -> 对登记过的、含 @Value 的 bean 逐个重新注入      ← @Value 在此刷新
```

两个关键点：

1. **本项目故意没有在 `LogValueConfigChangeListener` 里发 `EnvironmentChangeEvent`**（源码中该段被注释掉了，注释写的就是"这里无需发送事件"）。因为阿里自带的 `NacosContextRefresher` 已经会发，重复发会导致一轮变更刷两次。
2. `SpringValueAutoRefreshProcessor` 继承 `AutowiredAnnotationBeanPostProcessor`，把自动注入类型改成 `@Value`，在 **bean 初始化时**记录"哪些 bean 含 `@Value`"，配置变更时对这批 bean 逐个 `processInjection` 重新注入。

> 由于记录动作只发生在 bean 初始化时，**注册时机之后才被创建的 bean 不在名单里**（正常场景下所有单例在启动期就创建完了，不受影响）。

### 2. Apollo

```text
配置中心推送变更
  -> PropertiesRefresher(@ApolloConfigChangeListener) 打印变更前后值
  -> publishEvent(EnvironmentChangeEvent)
  -> @ConfigurationProperties 刷新
  -> @Value 由 Apollo 自带的 AutoUpdateConfigChangeListener 刷新，无需额外处理
```

## 注意事项

1. **不要直接修改本模块源码**：如需调整配置，复制模板 `bootstrap-*.yml` 到自己的模块 `resources` 目录后修改。
2. **配置中心三选一**：Nacos、Apollo、Spring Cloud Config 建议只保留一个，同时开启多套会增加排查成本。
3. **dev 环境默认关闭**：模板中 dev 环境默认关闭配置中心（Nacos/Apollo 的 dev 配置也关闭，Spring Cloud Config 各环境均关闭），本地开发不依赖中间件即可启动；Nacos/Apollo 在 test/pre/prod 默认开启，需要时按环境确认开关。
4. **Nacos 的 Data ID 要对齐**：`spring.cloud.nacos.config.name` 与 `spring.config.import` 两处不一致会静默读不到配置。
5. **`@ConditionalOnProperty` 的 bean 不会刷新**：当 bean 上有 `@ConditionalOnProperty` 时，配置变更不会让该 bean 重新装配（需重启），这是当前实现已知的限制。
6. **Apollo 多 namespace 需同步监听**：详见上文 Apollo 配置详解中的说明。
7. **Nacos 下 dev 环境等于"完全不刷新"**：`bootstrap-nacos-config.yml` 的 dev 环境把 `spring.cloud.nacos.config.enabled` 置为 `false`，而 `NacosAutoConfiguration` 的生效条件正是 `spring.cloud.nacos.config.enabled`（`matchIfMissing = true`）——**dev 环境下整个自动配置类都不装配，`SpringValueAutoRefreshProcessor` 根本不进容器，`@Value` 不会刷新**。开发时如需验证刷新，请把该开关打开。
8. **数据源类配置不要用 `@Value` 接**：`@Value` 的刷新方式是对 bean 整体重新注入，对 `DataSource`、连接池、`RedisTemplate`、线程池这类"改了就期望重建"的配置，重注入不会触发重建，接配置中心只会造成"看着改了其实没生效"的假象。
9. **`@RefreshScope` 与 `@Value` 自动刷新不要叠加使用**：`@RefreshScope` 的 bean 在容器里是作用域代理对象，而 `changedKeys` 里 `beanFactory.getBean(beanName)` 取到的正是代理，重新注入可能写不到目标实例上；同时 `ContextRefresher.refresh()` 会销毁 `refresh` 作用域，与本模块的重新注入叠加属于重复维护。两者选其一即可。
10. **`@Value` 写在父类上时当前实现不会刷新（已知缺陷）**：`SpringValueAutoRefreshProcessor` 用一个实例字段 `beanNamesNeedRefresh` 同时充当两个语义——「本类是否含 `@Value`」和「全部待刷新 bean 的名字」。该字段一旦非空（即任何一个含 `@Value` 的 bean 先被初始化过），扫描父类的循环就会被提前跳出，**`@Value` 只写在父类的 bean 不会被登记，配置变更后静默不刷新**（编写本文档时该实现尚未修复）。排查手段：给这类 bean 加一个自有 `@Value` 字段，或在启动日志里确认它是否出现在刷新名单相关日志中；根治需要把「是否找到」判断改为方法内局部变量。
11. **`@Value("${x}")`（无默认值）遇上配置项被删除会抛异常**：只要该 bean 在刷新名单里，重新注入就会因占位符无法解析而抛 `IllegalStateException`，并中断本轮对后续 bean 的刷新。给 `@Value` 配默认值（`${x:默认值}`）可规避。
12. **`@PostConstruct` 不会因刷新重跑**：重新注入只触发注入点（字段 / setter），不会重跑 `@PostConstruct` 等初始化回调，所以不必担心副作用方法被重复执行。
