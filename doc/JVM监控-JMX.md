# JVM监控-JMX

## 概述

JMX（Java Management Extensions）是一种为应用程序植入管理功能的框架，常用于JVM监控和性能调优。

## 安装与配置

### 下载地址
- JMX Prometheus Java Agent: https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/

### 项目存储位置
- 本项目存放于：`cicd/plugins/prometheus`

## 使用方法

### 启动参数配置
在应用启动参数中添加以下参数：

```bash
-javaagent:/path/to/jmx_prometheus_javaagent-1.0.1.jar=29010:/path/to/jmx_prometheus_javaagent-config.yaml
```

### 访问监控页面
- 监控前端访问地址：http://localhost:29010/
- Prometheus端点：http://localhost:29010/

## 配置说明

- 需要准备一个JMX配置文件（yaml格式），用于定义需要收集的指标
- 端口号可以根据需要进行调整
- 配置文件的路径需要根据实际部署情况进行修改

## 注意事项

- 确保JMX Agent版本与您的应用环境兼容
- 配置文件需要正确设置以获取所需的JVM指标
- 在生产环境中需考虑安全性配置