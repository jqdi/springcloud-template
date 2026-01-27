# GlobalResponse 模块

## 概述

GlobalResponse 模块是基于 GracefulResponse 框架构建的全局统一响应处理组件，用于在 Spring Cloud 微服务架构中提供统一的 API 响应格式和异常处理机制。

## 模块结构

```
globalresponse/
├── gracefulresponse/              # 请阅读gracefulresponse的README.md
├── ArgsBusinessException.java     # 带参数的业务异常类
├── BusinessException.java         # 业务异常类
├── ExceptionUtil.java             # 异常工具类
└── GlobalExceptionHandler.java    # 全局异常处理器（已废弃）
```

## 功能特性

### 1. 统一响应格式
提供统一的响应数据结构（包含 code、msg、data 字段）
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

### 2. 异常处理机制
- **ExceptionUtil**: 异常工具类，提供统一的异常抛出接口
  - `throwException(String code, String message)`: 抛出带编码的异常
  - `throwException(String message)`: 抛出简单异常
  - `throwException(String code, String message, Object... args)`: 抛出带参数的异常
  - `throwException(String message, Object... args)`: 抛出带参数的异常

- **ArgsBusinessException**: 带参数的业务异常类，支持异常消息的参数化替换

### 3. GracefulResponse 集成
- **gracefulresponse 模块**: 完整集成了 GracefulResponse 框架
  - 支持 Feign 客户端的统一响应处理
  - 提供 HTTP 消息转换器
  - 实现异常的统一处理和转换

### 4. 国际化支持
- 支持异常消息的国际化处理
- 通过 IMessage 接口实现消息转换
- 支持异常参数替换和国际化显示

### 5. Feign 集成
- 自动处理 Feign 客户端调用的响应格式转换
- 统一处理远程服务返回的异常信息
- 确保 Feign 调用结果符合预期的数据类型

## 使用方式

### 1. 抛出业务异常
```java
// 简单异常
ExceptionUtil.throwException("业务处理失败");

// 带编码的异常
ExceptionUtil.throwException("1001", "用户不存在");

// 带参数的异常
ExceptionUtil.throwException("1002", "参数 {0} 无效", "userId");
```

### 2. 在微服务中使用
在 Spring Cloud 微服务项目中引入该模块后，所有 API 调用和 Feign 客户端调用将自动应用统一的响应处理机制。

## 注意事项

- 推荐使用 `ExceptionUtil` 工具类来抛出业务异常，便于灵活切换异常实现类
- `BusinessException` 和 `GlobalExceptionHandler` 已标记为 @Deprecated，建议使用 GracefulResponse 框架提供的异常处理机制
