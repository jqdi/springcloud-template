package com.company.framework.messagedriven.springevent.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * ApplicationListener异步执行配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "template.enable", name = "message-driven", havingValue = "springevent")
public class AsyncExecutorConfig {

	/**
	 * <pre>
	 * 替换默认的SimpleApplicationEventMulticaster
	 * 默认SimpleApplicationEventMulticaster没有配置taskExecutor，所以是同步的
	 * </pre>
	 */
	@Bean
	public SimpleApplicationEventMulticaster applicationEventMulticaster(Executor taskExecutor) {
		SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
		simpleApplicationEventMulticaster.setTaskExecutor(taskExecutor);
		return simpleApplicationEventMulticaster;
	}
}
