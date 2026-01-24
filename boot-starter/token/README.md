# Token 模块

## 概述

Token模块是一个Spring Boot Starter，提供了一套完整的基于Token的身份验证解决方案。该模块同时支持JWT和Sa-Token两种主流的Token实现方式，开发者可以根据需要选择合适的方案。模块还集成了访问控制功能，可以轻松实现接口级别的权限保护。

## 功能特性

- ✅ **双重Token实现**：同时支持JWT和Sa-Token两种Token实现方式
- ✅ **访问控制**：提供`@RequireLogin`注解实现接口级别的访问控制

## 快速开始

### 1. 添加依赖

在您的模块的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-token</artifactId>
    <version>${boot-starter-token.version}</version>
</dependency>
```

### 2. 配置Token参数

在 `application.yml` 中导入token默认配置：

```yaml
spring:
  profiles:
    include: token
```

**如需自定义token配置**：复制[application-token.yml](src/main/resources/application-token.yml)到你的模块的 `resources` 目录下，并修改以下内容：

```yaml
token:
   # 密钥
   secret: 52ae521312f6461083435e045900486e
```

### 3. 使用访问控制

**如需自定义访问控制开关**：复制[application-token.yml](src/main/resources/application-token.yml)到你的模块的 `resources` 目录下，并修改以下内容：

```yaml
# 访问控制开关
template:
  enable:
    access-control: true
```

在需要进行身份验证的接口上添加`@RequireLogin`注解：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    @RequireLogin
    public Object getUserProfile() {
        // 业务逻辑
        return "User Profile";
    }
    
    @PostMapping("/update")
    @RequireLogin
    public Object updateUser(@RequestBody User user) {
        // 业务逻辑
        return "Update Success";
    }
}
```

## 核心组件

### TokenService 接口

提供统一的Token操作接口：
- `generate(String userId, String device)`：生成Token
- `invalid(String token)`：使Token失效
- `checkAndGet(String token)`：验证Token并获取用户ID
- `getTokenName()`：获取Token名称

### 两种实现方式

#### 1. JWT 实现 ([JsonWebTokenService](src/main/java/com/company/token/jsonwebtoken/JsonWebTokenService.java))

基于JWT标准实现，具有无状态、可扩展的特点。

#### 2. Sa-Token 实现 ([SaTokenService](src/main/java/com/company/token/satoken/SaTokenService.java))

基于Sa-Token框架实现，功能更加强大，支持更多高级特性。

### 访问控制

- `@RequireLogin`：注解用于标识需要登录才能访问的方法或类
- `AccessControlInterceptor`：访问控制拦截器，自动验证Token
- `UnauthorizedHandler`：未授权处理器，处理未授权请求

## 配置详解

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| token.name | x-token | Token在HTTP Header中的名称 |
| token.prefix | | Token前缀，例如"Bearer" |
| token.secret | defaultsecret | JWT加密密钥 |
| token.timeout | 2592000 | Token超时时间（秒），-1表示永不过期 |
| template.enable.access-control | true | 是否启用访问控制功能 |

## 使用示例

### 1. 生成Token

```java
@Autowired
private TokenService tokenService;

public String login(String userId, String device) {
    // 生成Token
    String token = tokenService.generate(userId, device);
    return token;
}
```

### 2. 验证Token

```java
@Autowired
private TokenService tokenService;

public String validateToken(String token) {
    // 验证Token并获取用户ID
    String userId = tokenService.checkAndGet(token);
    if (userId != null) {
        return "User ID: " + userId;
    }
    return "Invalid Token";
}
```

### 3. 使Token失效

```java
@Autowired
private TokenService tokenService;

public void logout(String token) {
    // 使Token失效
    tokenService.invalid(token);
}
```

## 注意事项

1. **安全设置**：生产环境中务必修改默认的token.secret配置，使用强密钥
3. **访问控制**：通过`template.enable.access-control`配置生产环境必须保持开启
