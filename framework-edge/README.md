# framework-edge 模块

## 简介

framework-edge 模块是专为边缘服务设计的轻量级框架模块，主要用于处理来自客户端的请求。该模块基于 [template-framework](../framework) 构建，但剔除了边缘服务不会使用的功能组件，同时增加了面向客户端请求处理的特有功能，如访问控制、设备信息收集、数据脱敏等。

## 核心功能

### 1. 访问控制
- [@RequireLogin](src/main/java/com/company/framework/annotation/RequireLogin.java) 注解实现API访问权限控制
- 未授权访问拦截处理

### 2. 请求上下文处理
- [HttpContextFilter](src/main/java/com/company/framework/filter/HttpContextFilter.java) 统一处理HTTP请求上下文信息
- 自动解析并设置设备信息、平台信息、版本信息等到请求头

### 3. 设备信息收集
- [DeviceInfoFilter](src/main/java/com/company/framework/filter/DeviceInfoFilter.java) 收集客户端设备信息
- 通过消息驱动发布设备信息事件

### 4. 用户来源跟踪
- [SourceFilter](src/main/java/com/company/framework/filter/SourceFilter.java) 跟踪用户来源渠道
- 支持引流统计、邀请奖励等业务场景

### 5. 数据脱敏
- [@Sensitive](src/main/java/com/company/framework/jackson/annotation/Sensitive.java) 注解实现数据脱敏
- 支持多种脱敏类型（姓名、手机号、身份证、银行卡等）

### 6. Jackson 序列化增强
- 数据格式化注解（[@FormatNumber](src/main/java/com/company/framework/jackson/annotation/FormatNumber.java)、[@PlusMinusNumber](src/main/java/com/company/framework/jackson/annotation/PlusMinusNumber.java)）
- 自动描述注解（[@AutoDesc](src/main/java/com/company/framework/jackson/annotation/AutoDesc.java)）

### 7. 跨域配置
- [CorsConfigurer](src/main/java/com/company/framework/interceptor/CorsConfigurer.java) 统一跨域配置

### 8. 请求体加解密
- 集成 [boot-starter-encryptbody](../boot-starter/encryptbody) 模块，支持请求体和响应体的加解密

## 模块结构

```
framework-edge/
├── annotation/             # 注解类
├── context/                # 上下文工具类
├── enums/                  # 枚举类
├── filter/                 # 过滤器
├── globalresponse/         # 全局响应处理
├── interceptor/            # 拦截器
└── jackson/                # Jackson序列化相关
    ├── annotation/         # Jackson注解
    └── serializer/         # Jackson序列化器
```

## 主要组件详解

### 1. 访问控制
通过 [@RequireLogin](src/main/java/com/company/framework/annotation/RequireLogin.java) 注解标记需要登录才能访问的API：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/info")
    @RequireLogin
    public UserInfo getUserInfo() {
        // 只有登录用户才能访问此接口
        return userInfo;
    }
}
```

### 2. 数据脱敏
使用 [@Sensitive](src/main/java/com/company/framework/jackson/annotation/Sensitive.java) 注解对敏感数据进行脱敏处理：

```java
public class UserInfo {
    private String name;
    
    @Sensitive(DesensitizedType.MOBILE)
    private String mobile;
    
    @Sensitive(DesensitizedType.ID_CARD)
    private String idCard;
    
    // getters and setters
}
```

### 3. 数据格式化
使用 [@FormatNumber](src/main/java/com/company/framework/jackson/annotation/FormatNumber.java) 注解对数字进行格式化：

```java
public class AccountInfo {
    @FormatNumber(pattern = "#,##0.00")
    private BigDecimal balance;
    
    // getters and setters
}
```

### 4. 自动描述
使用 [@AutoDesc](src/main/java/com/company/framework/jackson/annotation/AutoDesc.java) 注解自动生成描述信息：

```java
public class OrderInfo {
    @AutoDesc(value = "订单状态", map = {"1:待支付", "2:已支付", "3:已完成"})
    private Integer status;
    
    // getters and setters
}
```

## 配置说明

模块提供了以下配置选项：

### 1. 国际化配置
模块包含国际化资源文件：
- [messages.properties](src/main/resources/i18n-framework-edge/messages.properties) - 默认语言
- [messages_en_US.properties](src/main/resources/i18n-framework-edge/messages_en_US.properties) - 英文
- [messages_zh_CN.properties](src/main/resources/i18n-framework-edge/messages_zh_CN.properties) - 简体中文
- [messages_zh_HK.properties](src/main/resources/i18n-framework-edge/messages_zh_HK.properties) - 繁体中文

## 使用方法

### 1. 添加依赖
在需要使用框架功能的项目的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>template-framework-edge</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 使用组件
直接使用提供的注解和组件：

```java
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    @GetMapping("/sensitive-data")
    @RequireLogin
    public UserInfo getSensitiveData() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("张三");
        userInfo.setMobile("13800138000");
        userInfo.setIdCard("11010119900307XXXX");
        return userInfo;
    }
}
```

## 与 Framework 模块的区别

| 特性 | Framework | Framework-Edge |
|------|-----------|----------------|
| 定位 | 核心服务框架 | 边缘服务框架 |
| 功能完整性 | 完整功能集 | 精简功能集 |
| Elasticsearch | 包含 | 剔除 |
| 消息驱动 | 完整支持 | 部分支持 |
| 熔断器 | 完整支持 | 依赖引入 |
| 访问控制 | 基础支持 | 增强支持 |
| 数据脱敏 | 不包含 | 原生支持 |
| 设备信息收集 | 不包含 | 原生支持 |

## 设计理念

### 1. 轻量级设计
针对边缘服务的特点，剔除了不必要的重型组件，保持模块轻量。

### 2. 安全优先
内置访问控制和数据脱敏功能，保障边缘服务的安全性。

### 3. 客户端友好
提供丰富的数据格式化和描述功能，提升客户端体验。

### 4. 易于集成
与主框架保持一致的设计风格，便于团队理解和使用。

## 最佳实践

### 1. 合理使用访问控制
对涉及用户隐私和敏感操作的API务必添加 [@RequireLogin](src/main/java/com/company/framework/annotation/RequireLogin.java) 注解。

### 2. 数据脱敏策略
根据业务需求选择合适的脱敏类型，避免敏感信息泄露。

### 3. 设备信息利用
充分利用设备信息收集功能，为业务分析和运营提供数据支持。

### 4. 跨域配置
根据实际部署环境配置合适的跨域策略。
