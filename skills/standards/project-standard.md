# SpringCloud Template 项目整体规范

## 项目概述
springcloud-template 是一个企业级的Spring Cloud微服务架构模板，整合了大量常用组件和最佳实践，帮助快速搭建微服务项目。

## 技术栈
- Spring Cloud 2021.0.9
- Spring Cloud Alibaba 2021.0.6.2
- Spring Boot 2.7.18
- Eureka/Nacos 注册中心
- Gateway 网关
- Apollo/Nacos 配置中心
- MySQL + MyBatis Plus 数据持久化
- Redis 缓存
- RocketMQ/RabbitMQ 消息队列
- Sa-Token 认证授权
- Sentinel 熔断限流
- SkyWalking 链路追踪

## 端口规范
- 7XX0: 公共组件（如注册中心、网关、配置中心）
- 8XX0: 内部微服务（如系统服务、用户服务、订单服务）
- 9XX0: 边缘微服务（如Web端、App端、管理后台）

## 模块结构
- boot-starter: 自定义starter框架
- framework: 内部微服务框架代码
- framework-edge: 边缘微服务框架代码
- eureka: 注册中心
- gateway: 网关服务
- config: 配置中心
- system/user/order/tool/im: 业务微服务
- app/web/admin/adminapi/openapi: 接入层服务

## 代码规范
- 包名采用 com.company.{module} 结构
- Controller 层负责接收请求和返回响应
- Service 层负责业务逻辑处理
- Mapper/DAO 层负责数据访问
- 使用统一异常处理和响应包装
- 日志使用SLF4J + Logback，集成MDC链路追踪

## 配置规范
- 环境配置通过 application-{env}.yml 管理
- 敏感配置使用Apollo/Nacos配置中心管理
- 支持动态刷新配置
- 统一配置前缀规范

## 部署规范
- 支持Docker容器化部署
- 支持主机部署
- 优雅启停机制
- 健康检查机制