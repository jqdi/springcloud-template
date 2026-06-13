package com.company.framework.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.OnRetryNotEnabledCondition;
import org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import feign.Client;

/**
 * copy from org.springframework.cloud.openfeign.loadbalancer.DefaultFeignLoadBalancerConfiguration
 * 
 * Configuration instantiating a {@link LoadBalancerClient}-based {@link Client} object
 * that uses {@link Client.Default} under the hood.
 *
 * @author Olga Maciaszek-Sharma
 * @since 2.2.0
 */
//@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoadBalancerClientsProperties.class)
@Deprecated // FeignLoggerClient的写死初始化，会导致无法使用feign-httpclient、feign-okhttp连接池
class DefaultFeignLoadBalancerConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@Conditional(OnRetryNotEnabledCondition.class)
	public Client feignClient(LoadBalancerClient loadBalancerClient,
			LoadBalancerClientFactory loadBalancerClientFactory) {
		return new FeignBlockingLoadBalancerClient(new FeignLoggerClient(null, null), loadBalancerClient,
				loadBalancerClientFactory);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate")
	@ConditionalOnBean(LoadBalancedRetryFactory.class)
	@ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "true",
			matchIfMissing = true)
	public Client feignRetryClient(LoadBalancerClient loadBalancerClient,
			LoadBalancedRetryFactory loadBalancedRetryFactory, LoadBalancerClientFactory loadBalancerClientFactory) {
		return new RetryableFeignBlockingLoadBalancerClient(new FeignLoggerClient(null, null), loadBalancerClient,
				loadBalancedRetryFactory, loadBalancerClientFactory);
	}

}
