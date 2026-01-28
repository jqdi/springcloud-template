# SpringCloud Template 各模块规范

## 模块分类

### 1. 核心框架模块
- **boot-starter**: 提供自定义starter，包含：
  - datasource: 数据源管理
  - encryptbody: 请求/响应加解密
  - token: 认证授权（JWT、Sa-Token）

- **framework**: 内部微服务框架代码
  - autoconfigure: 自动配置
  - cache: 缓存抽象层
  - canal: 数据变更监听
  - config: 动态配置刷新
  - deploy: 优雅发版机制
  - developer: 环境流量路由到本机调试
  - feign: 微服务间请求头传递
  - globalresponse: 统一异常处理和响应结构
  - lock: 分布式锁实现
  - message: 国际化文案
  - messagedriven: 消息驱动异步处理
  - sequence: 唯一ID生成
  - threadpool: 自定义线程池
  - trace: 日志追踪链路

- **framework-edge**: 边缘微服务框架代码
  - filter: HTTP公共参数设置
  - globalresponse: 统一异常处理和日志打印
  - interceptor: 访问控制
  - jackson: JSON序列化定制

### 2. 基础服务模块
- **eureka**: 注册中心
- **gateway**: API网关
- **config**: 配置中心

### 3. 业务服务模块
- **system**: 系统服务
  - 权限管理
  - 用户管理
  - 角色管理
  - 菜单管理

- **user**: 用户服务
  - 用户认证
  - 钱包功能
  - 优惠券功能
  - 设备信息管理

- **order**: 订单服务
  - 订单管理
  - 支付功能
  - 收银台功能

- **tool**: 工具服务
  - 文件存储（阿里OSS、腾讯COS、MinIO等）
  - 短信发送
  - 邮件发送
  - 弹窗功能
  - 订阅消息

- **im**: 即时通讯服务
  - 消息推送
  - 在线状态管理

### 4. 接入层服务模块
- **app**: APP端接入层
- **web**: WEB端接入层
- **admin**: 管理后台（前后端一体）
- **adminapi**: 管理后台API（前后端分离）
- **openapi**: 开放平台接入层

### 5. 支撑服务模块
- **job**: 定时任务
- **monitor**: 监控服务

## 模块开发规范

### 服务命名规范
- 内部微服务：`{service}-service`
- API接口模块：`{service}-api`

### 依赖关系
- 所有模块均依赖 `common` 模块
- 业务模块按需依赖 `framework` 或 `framework-edge`
- 内部微服务依赖 `framework`
- 边缘微服务依赖 `framework-edge`

### 代码组织
- Controller: 接收请求、参数校验、返回响应
- Service: 业务逻辑处理
- Mapper/Repository: 数据访问层
- DTO: 数据传输对象
- Entity/Model: 实体对象
- VO: 视图对象
- Constants: 常量定义
- Exception: 自定义异常

### 配置文件
- application.yml: 通用配置
- application-{env}.yml: 环境特定配置
- bootstrap.yml: 配置中心连接配置
- logback-spring.xml: 日志配置

### 测试规范
- 单元测试放在 `src/test/java` 目录
- 集成测试使用@SpringBootTest注解
- 接口测试使用MockMvc