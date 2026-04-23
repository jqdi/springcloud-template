# GracefulResponse 模块

## 概述

GracefulResponse 模块是基于 [GracefulResponse框架](https://doc.feiniaojin.com/graceful-response/home.html) 构建的统一响应处理组件，用于在 Spring Cloud 微服务架构中提供统一的 API 响应格式和异常处理机制。

## 功能特性

### 1. 统一响应格式
- 提供统一的响应数据结构（包含 code、msg、data 字段）
- 支持多种响应风格（Style0 和 Style1）
Style0风格
```json
{
  "status": {
    "code": "0",
    "msg": "ok"
  },
  "payload": {
    "id": 1,
    "name": "JQ"
  }
}
```

Style1风格
```json
{
  "code": "0",
  "msg": "ok",
  "data": {
    "id": 1,
    "name": "JQ"
  }
}
```
- 可自定义成功和失败的响应码及提示信息

### 2. HTTP 消息转换
- 通过 GracefulResponseHttpMessageConverter 处理 HTTP 请求/响应转换
- 自动解析远程服务返回的统一响应格式
- 提取实际业务数据并转换为预期类型

### 3. Feign 客户端集成
- 通过 GracefulResponseFeignClientsConfiguration 配置类自动配置 Feign 客户端
- 使用 GracefulResponseDecoder 解码器处理远程服务响应
- 确保 Feign 调用结果符合预期的数据类型

### 4. 异常处理
- 通过 GracefulResponseFeignExceptionAspect 切面处理 Feign 调用异常
- 自动识别并抛出远程服务返回的业务异常
- 通过 GracefulResponseExceptionContext 管理异常上下文

### 5. 参数化异常处理
- 通过 ArgsExceptionAdvice 处理带参数的业务异常
- 支持异常消息参数替换和国际化
- 使用 GrI18nResponseBodyAdvice 实现消息国际化功能

## 模块结构

```
gracefulresponse/
├── extend/                    # 扩展功能
│   ├── advice/               # 异常处理切面
│   │   ├── ArgsExceptionAdvice.java      # 参数化异常处理
│   │   ├── GrI18nResponseBodyAdvice.java # 国际化响应处理
│   │   └── context/          # 上下文管理
│   │       └── GracefulResponseExceptionArgsContext.java
├── feign/                     # Feign 集成
│   ├── GracefulResponseDecoder.java              # Feign 响应解码器
│   ├── GracefulResponseFeignClientsConfiguration.java  # Feign 自动配置
│   ├── GracefulResponseFeignExceptionAspect.java       # Feign 异常切面
│   ├── advice/               # Feign 异常处理
│   │   └── ResultExceptionAdvice.java
│   ├── context/              # 异常上下文
│   │   └── GracefulResponseExceptionContext.java
│   └── converter/            # 消息转换器
│       └── GracefulResponseHttpMessageConverter.java
```

## 核心组件说明

### GracefulResponseDecoder
- 用于解码 Feign 客户端调用的响应
- 自动识别并处理包含 code、msg、data 字段的响应格式
- 当响应状态码不为成功码时，将异常信息存储到上下文中

### GracefulResponseFeignExceptionAspect
- AOP 切面，处理 Feign 调用后的异常
- 从上下文中获取异常信息并重新抛出
- 确保异常能够正确传递到调用方

### GracefulResponseHttpMessageConverter
- HTTP 消息转换器，处理请求/响应数据的转换
- 自动解析包含统一响应格式的数据
- 支持响应风格 0 和风格 1

### ArgsExceptionAdvice
- 处理自定义的 ArgsBusinessException 异常
- 支持异常消息参数的替换处理
- 将异常信息转换为统一响应格式

### GrI18nResponseBodyAdvice
- 实现响应消息的国际化功能
- 使用 IMessage 接口进行消息转换
- 处理异常参数替换和国际化显示

## 使用

在 `application.yml` 中导入gracefulresponse默认配置：

```yaml
spring:
  profiles:
    include: gracefulresponse
```

在微服务项目中引入该模块后，所有 API 调用和 Feign 客户端调用将自动应用统一的响应处理机制，无需额外操作。