# 熔断器-Sentinel

## 概述

Sentinel是阿里巴巴开源的面向分布式服务架构的流量控制组件，主要以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。

## 官方资源

- GitHub地址：https://github.com/alibaba/Sentinel
- 官方文档：https://github.com/alibaba/spring-cloud-alibaba/wiki/Sentinel

## 功能特性

- **流量控制**：基于QPS、线程数等方式进行流量控制
- **熔断降级**：支持多种熔断策略，防止雪崩效应
- **系统负载保护**：根据系统整体负载情况保护系统稳定
- **实时监控**：提供实时的监控界面
- **离群实例摘除**：自动识别并隔离异常实例

## 安装部署

### 下载地址
- Sentinel项目地址：https://github.com/alibaba/Sentinel

### Dashboard启动
启动Sentinel Dashboard服务：

```bash
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.8.jar
```

## 集成方式

### Spring Cloud Alibaba集成
通过Spring Cloud Alibaba项目可以方便地集成Sentinel，实现服务的熔断降级功能。

### 控制台访问
- 默认访问地址：http://localhost:8080
- 登录账号：sentinel
- 登录密码：sentinel

## 核心概念

- **资源(Resource)**：可保护的业务逻辑单元
- **规则(Rule)**：作用于资源的各种控制规则
- **Slot Chain**：责任链模式的实现，各个Slot完成不同功能

## 规则类型

- **流量控制规则(Flow Rule)**：限制资源的流量
- **熔断降级规则(Degrade Rule)**：对资源进行熔断降级保护
- **系统规则(System Rule)**：根据系统指标进行保护
- **授权规则(Authority Rule)**：黑白名单控制

## 最佳实践

- 合理设置流量控制阈值，平衡系统性能和稳定性
- 针对不同业务场景选择合适的熔断策略
- 定期检查和调整规则配置
- 结合监控数据优化规则设置

## 注意事项

- 生产环境中需要合理配置各项规则参数
- 建议启用持久化功能保存规则配置
- 关注系统资源使用情况，避免过度限制影响业务
- 及时更新版本以获得更好的功能和性能