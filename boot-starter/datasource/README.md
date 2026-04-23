# 数据源 Starter 使用指南

## 简介

数据源 Starter 是一个基于 Spring Boot 的自动化配置模块，集成了 MyBatis-Plus 和动态数据源功能。该 Starter 提供了以下核心功能：

1. 自动配置 MyBatis Mapper 扫描
2. 集成 MyBatis-Plus 插件（分页、SQL 限流、性能分析、审计字段填充）
3. 动态数据源支持（主从切换、多数据源）
4. 事务管理器自动配置
5. mybatis-plus字段加密
6. mybatis-i18n字段国际化

## 功能特性

### 1. MyBatis Mapper 自动扫描
- 自动扫描项目中所有 mapper 接口
- 默认扫描路径：`com.company.**.mapper`

### 2. MyBatis-Plus 插件
- **分页插件**：支持物理分页查询
- **SQL 限流插件**：防止全表扫描导致的慢 SQL
- **性能分析插件**：输出每条 SQL 语句及其执行时间
- **审计字段拦截器**：自动填充创建人、创建时间、更新人、更新时间

### 3. 动态数据源
- 支持主从数据源自动切换
- 支持多数据源配置
- 支持懒加载数据源

### 4. 事务管理
- 自动配置 DataSourceTransactionManager

### 5. 审计字段自动填充
- 支持实体类继承 `AuditableModel` 基类自动拥有审计字段
- 支持通过 `AuditableModelProvider` 自定义审计字段值来源
- 提供两种实现方案：
  - **MyBatis-Plus 方案**：`AuditableMetaObjectHandler` 处理器（推荐）
  - **SQL 拦截器方案**：`AuditableInterceptor` 拦截器（支持非 BaseMapper API 执行的 SQL）

## 快速开始

### 1. 添加依赖

在您的模块的 `pom.xml` 中添加以下依赖：

```xml
<!-- 数据源 -->
<dependency>
    <groupId>com.company</groupId>
    <artifactId>boot-starter-datasource</artifactId>
    <version>${boot-starter-datasource.version}</version>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 中导入默认数据源默认配置：

```yaml
spring:
  profiles:
    include: datasource
```

**如需自定义数据源配置**：复制[application-datasource.yml](src/main/resources/application-datasource.yml)到你的模块的 `resources` 目录下，并修改以下内容：

```yaml
custom:
  ip_port: 127.0.0.1:3306
  username: root
  password: 12345678
  lazy: true # 是否懒加载数据源
```

### 3. 使用主从数据源

在 Service 层方法上添加注解来指定使用主库或从库：

```java
@Service
public class UserService {
    
    @Master // 使用主数据源
    public void updateUser(User user) {
        // 更新操作
    }
    
    @Slave // 使用从数据源
    public User getUserById(Long id) {
        // 查询操作
        return userMapper.selectById(id);
    }
}
```

在 Mapper 层方法上添加注解来指定使用主库或从库：

```
public interface UserInfoMapper extends BaseMapper<UserInfo> {

//	@DS("slave_1") // 查询从库1
//	@DS("slave_2") // 查询从库2
//	@DS("slave") // 查询分组slave的数据
@Slave // 查询分组slave的数据
@Select("select * from user_info where id = #{id}")
UserInfo getById(@Param("id") Integer id);
}
```

### 4. SQL 限流配置

通过配置项控制 SQL 限流功能：

```yaml
template:
  sqllimit:
    max: 1000 # 每条 SQL 默认最多返回 1000 条记录
```

当设置大于 0 的值时，会自动为没有 limit 的查询 SQL 添加 limit 限制。

### 5. 审计字段自动填充使用

#### 5.1 实体类继承 AuditableModel

```java
@Data
@TableName("user_info")
public class UserInfo extends AuditableModel<UserInfo> {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    // 继承的审计字段：createTime, createBy, updateTime, updateBy
}
```

#### 5.2 实现 AuditableModelProvider 接口

```java
@Component
public class UserAuditableModelProvider implements AuditableModelProvider {
    
    @Override
    public AuditableModel<?> getAuditableModel() {
        AuditableModel<Model<?>> model = new AuditableModel<>();
        // 从当前登录用户获取信息
        model.setCreateBy(getCurrentUserId());
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateBy(getCurrentUserId());
        model.setUpdateTime(LocalDateTime.now());
        return model;
    }
}
```

### 6. mybatis-plus 字段加密

#### 6.1 实体类使用 @Encrypt 注解

```java
@Data
@TableName("user_info")
public class UserInfo implements Encrypted {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;

    @FieldEncrypt
    private String phone;

    @FieldEncrypt
    private String password;
}
```

#### 6.2 配置加密密钥

在 `application.yml` 中添加加密配置：

```yaml
encrypt:
  enable: true
  key: ublp45r318fr4xr7
  type: default

```

### 7. mybatis-i18n 字段国际化

#### 7.1 实体类使用 @I18nField 注解

```java
@Data
@TableName("user_info")
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;

    @I18nField(i18nTable = "user_info_i18n", i18nColumn = "nickname", i18nRelatedColumn = "user_info_id", relatedValueFromField = "id") // 独立国际化表方式
    private String nickname;

    @I18nField(i18nTable = "user_info_i18n", i18nColumn = "description", i18nRelatedColumn = "user_info_id", relatedValueFromField = "id", i18nDataProvider = CommonI18nDataProvider.class) // 统一国际化表方式
    private String description;
}
```

### 8. 日志打印

在 `logback-spring.xml` 中引用数据源日志配置文件：[logback-conf-datasource.xml](src/main/resources/logback-conf-datasource.xml)

```xml
<!--引用数据源日志 -->
<include resource="logback-conf-datasource.xml" />
```
