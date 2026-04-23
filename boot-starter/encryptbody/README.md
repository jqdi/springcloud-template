# EncryptBody Starter 使用指南

## 简介

EncryptBody Starter 是一个基于 Spring Boot 的自动化配置模块，用于对 HTTP 请求体和响应体进行加解密处理。该 Starter 基于 [encrypt-body-spring-boot-starter](https://gitee.com/licoy/encrypt-body-spring-boot-starter) 开发，提供了对 RESTful API 数据传输的安全保护。

## 功能特性

### 1. 多种加密算法支持
- **AES 加密**：高级加密标准，安全性高
- **DES 加密**：数据加密标准
- **RSA 加密**：非对称加密算法
- **MD5 哈希**：消息摘要算法
- **SHA 哈希**：安全哈希算法

### 2. 灵活的加密方式
- **整体加密**：对整个响应体或请求体进行加密
- **字段加密**：仅对特定字段进行加密
- **注解驱动**：通过简单的注解实现加解密

### 3. 环境配置支持
- 支持不同环境的不同配置
- 可通过配置开关控制是否启用加解密功能

## 快速开始

### 1. 添加依赖

在您的项目的 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-encryptbody</artifactId>
    <version>${boot-starter-encryptbody.version}</version>
</dependency>
```

### 2. 配置参数

在 `application.yml` 中导入encryptbody默认配置：

```yaml
spring:
  profiles:
    include: encryptbody
```

**如需自定义encryptbody配置**：复制[application-encryptbody.yml](src/main/resources/application-encryptbody.yml)到你的模块的 `resources` 目录下，并修改以下内容：

```yaml
encrypt:
  body:
    enabled: false # 是否开启加解密
    aes-key: 1468657f04e24e4d8c43f1b8b4032984 #AES加解密秘钥
    des-key: 1468657f04e24e4d8c43f1b8b4032984 #DES加解密秘钥
```

## 使用方法

### 1. 响应体加密

#### 对整个响应体加密

使用相应的加密注解标记 Controller 方法：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    // AES加密整个响应体
    @GetMapping("/info")
    @AESEncryptBody
    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(1L);
        userInfo.setName("张三");
        userInfo.setPhone("13800138000");
        return userInfo;
    }
    
    // RSA加密整个响应体
    @PostMapping("/detail")
    @RSAEncryptBody
    public UserDetail getUserDetail(@RequestBody UserQuery query) {
        // 业务逻辑
        return userDetail;
    }
}
```

#### 对特定字段加密

使用 [@FieldBody](src/main/java/cn/licoy/encryptbody/annotation/FieldBody.java) 注解标记需要加密的实体类：

```java
public class UserInfo {
    private Long id;
    
    private String name;
    
    // 对phone字段进行AES加密
    @AESEncryptBody
    private String phone;
    
    // 对身份证字段进行DES加密，并指定加密后存储的字段
    @DESEncryptBody
    @FieldBody(field = "idCardEncrypted", clearValue = true)
    private String idCard;
    
    private String idCardEncrypted;
    
    // getters and setters
}
```

### 2. 请求体解密

对传入的加密请求体进行自动解密：

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    // 自动解密AES加密的请求体
    @PostMapping("/update")
    public String updateUser(@RequestBody @AESDecryptBody UserInfo userInfo) {
        // userInfo中的加密字段会被自动解密
        userService.updateUser(userInfo);
        return "更新成功";
    }
}
```

### 3. 支持的注解

#### 加密注解
- [@AESEncryptBody](cn/licoy/encryptbody/annotation/encrypt/AESEncryptBody.java)：AES 加密
- [@DESEncryptBody](cn/licoy/encryptbody/annotation/encrypt/DESEncryptBody.java)：DES 加密
- [@RSAEncryptBody](cn/licoy/encryptbody/annotation/encrypt/RSAEncryptBody.java)：RSA 加密
- [@MD5EncryptBody](cn/licoy/encryptbody/annotation/encrypt/MD5EncryptBody.java)：MD5 哈希
- [@SHAEncryptBody](cn/licoy/encryptbody/annotation/encrypt/SHAEncryptBody.java)：SHA 哈希

#### 解密注解
- [@AESDecryptBody](cn/licoy/encryptbody/annotation/decrypt/AESDecryptBody.java)：AES 解密
- [@DESDecryptBody](cn/licoy/encryptbody/annotation/decrypt/DESDecryptBody.java)：DES 解密
- [@RSADecryptBody](cn/licoy/encryptbody/annotation/decrypt/RSADecryptBody.java)：RSA 解密

#### 通用注解
- [@EncryptBody](cn/licoy/encryptbody/annotation/encrypt/EncryptBody.java)：通用加密注解
- [@DecryptBody](cn/licoy/encryptbody/annotation/decrypt/DecryptBody.java)：通解密注解
- [@FieldBody](cn/licoy/encryptbody/annotation/FieldBody.java)：字段级加解密注解

## 高级配置

### 1. 自定义加密密钥

```yaml
encrypt:
  body:
    enabled: true
    aes-key: 1468657f04e24e4d8c43f1b8b4032984 # 自定义AES密钥
    des-key: 1468657f04e24e4d # 自定义DES密钥
```

### 2. RSA 加密配置

对于 RSA 加密，可以指定公钥和私钥：

```java
@GetMapping("/rsa-data")
@RSAEncryptBody(key = "your-rsa-public-key", type = RSAKeyType.PUBLIC)
public UserInfo getRsaData() {
    // 返回数据将使用RSA公钥加密
    return userInfo;
}
```

### 3. SHA 类型配置

对于 SHA 加密，可以指定不同的哈希算法：

```java
@GetMapping("/sha-data")
@SHAEncryptBody(value = SHAEncryptType.SHA512)
public String getShaData() {
    return "需要哈希的数据";
}
```

## 与 Result 封装类集成

该 Starter 特别优化了与统一返回结果类 [T](../../common/src/main/java/com/company/common/api/Result.java) 的集成，能够正确处理泛型类型的加密：

```java
@GetMapping("/user-list")
@AESEncryptBody
public List<UserInfo> getUserList() {
    List<UserInfo> userList = userService.getUserList();
    return userList;
}
```

## 注意事项

1. AES 密钥需要 32 位字符长度
2. DES 密钥需要符合 DES 算法要求
3. 加密功能默认在生产环境开启，在开发环境关闭
4. 使用 RSA 加密时需要正确配置公钥和私钥
5. 字段级加密时，注意 [@FieldBody](src/main/java/cn/licoy/encryptbody/annotation/FieldBody.java) 注解的使用方式
6. 加密后的数据类型通常会变为字符串，需要在接收端正确处理
