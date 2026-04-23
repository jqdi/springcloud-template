# 配置中心-Apollo

## 概述

Apollo（阿波罗）是携程框架部门研发的分布式配置中心，能够集中化管理应用不同环境、不同集群的配置，配置修改后能够实时推送到应用端，并且具备规范的权限、流程治理等特性。

## 官方资源

- Apollo Quick Start：https://github.com/apolloconfig/apollo-quick-start
- 下载地址：https://github.com/apolloconfig/apollo/tags

## 安装部署

### 版本选择
以v1.9.2版本为例：
- Admin Service: https://github.com/apolloconfig/apollo/releases/download/v1.9.2/apollo-adminservice-1.9.2-github.zip
- Config Service: https://github.com/apolloconfig/apollo/releases/download/v1.9.2/apollo-configservice-1.9.2-github.zip
- Portal: https://github.com/apolloconfig/apollo/releases/download/v1.9.2/apollo-portal-1.9.2-github.zip

### 安装步骤
1. 将上述三个组件解压至 `springcloud-template\apollo-quick-start` 目录（该目录已在.gitignore中排除，不会被提交）
2. 准备数据库，执行相应的SQL脚本

### 数据库配置
SQL脚本地址：
- https://github.com/apolloconfig/apollo/tree/v1.9.2/scripts/sql/apolloconfigdb.sql
- https://github.com/apolloconfig/apollo/tree/v1.9.2/scripts/sql/apolloportaldb.sql

### 数据库连接配置
修改以下文件中的数据库连接配置（特别注意设置serverTimezone参数，否则可能会报错）：

- `springcloud-template\apollo-quick-start\apollo-adminservice-1.9.2-github\config\application-github.properties`
- `springcloud-template\apollo-quick-start\apollo-configservice-1.9.2-github\config\application-github.properties`
- `springcloud-template\apollo-quick-start\apollo-portal-1.9.2-github\config\application-github.properties`

示例配置：
```properties
spring.datasource.url = jdbc:mysql://localhost:3306/apolloconfigdb?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&useSSL=false&tinyInt1isBit=false
```

### 日志路径配置
修改以下文件中的日志路径配置：

- `springcloud-template\apollo-quick-start\apollo-adminservice-1.9.2-github\scripts\startup.sh`
- `springcloud-template\apollo-quick-start\apollo-configservice-1.9.2-github\scripts\startup.sh`
- `springcloud-template\apollo-quick-start\apollo-portal-1.9.2-github\scripts\startup.sh`

示例配置：
```bash
LOG_DIR=D:/opt/logs/100003172
```

## 启动服务

按以下顺序启动服务：

1. 启动Config Service：
   ```
   springcloud-template\apollo-quick-start\apollo-configservice-1.9.2-github\scripts\startup.sh
   ```

2. 启动Admin Service：
   ```
   springcloud-template\apollo-quick-start\apollo-adminservice-1.9.2-github\scripts\startup.sh
   ```

3. 启动Portal：
   ```
   springcloud-template\apollo-quick-start\apollo-portal-1.9.2-github\scripts\startup.sh
   ```

## 核心组件

- **Config Service**：提供配置的读取、推送等功能，服务对象是Eureka
- **Admin Service**：提供配置的修改、发布等功能，服务对象是Portal
- **Portal**：提供Web界面供用户管理配置

## 功能特点

- 统一配置管理：集中管理不同环境、不同集群的配置
- 实时推送：配置变更后实时推送到客户端
- 权限控制：完善的用户权限管理
- 版本发布：支持配置版本管理和灰度发布
- 环境隔离：支持多环境配置隔离

## 最佳实践

- 合理规划命名空间，避免配置冲突
- 设置合适的权限控制，确保配置安全
- 定期备份配置数据
- 监控配置中心运行状态

## 注意事项

- 启动顺序很重要，必须先启动Config Service，再启动Admin Service，最后启动Portal
- 确保数据库连接正常，特别是时区设置
- 生产环境建议部署高可用集群
- 合理配置日志路径，便于问题排查