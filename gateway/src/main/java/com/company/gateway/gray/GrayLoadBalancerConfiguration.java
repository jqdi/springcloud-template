package com.company.gateway.gray;

import com.company.gateway.gray.strategy.GrayRuleStrategy;
import com.company.gateway.gray.strategy.GrayStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * 灰度负载均衡器 Bean 注册。
 *
 * <p>参考 {@link com.company.gateway.developer.DeveloperLoadBalancerConfiguration} 结构，
 * 每个服务独立创建 {@link GrayLoadbalancer} 和 {@link GrayStrategy} 实例，
 * 确保 {@link GrayRuleStrategy} 的 position 和 serviceId 按服务隔离。
 */
@Order(GrayLoadBalancerConfiguration.DYNAMIC_ROUTE_ORDER - 1)
@ConditionalOnDiscoveryEnabled
public class GrayLoadBalancerConfiguration {

    public static final int DYNAMIC_ROUTE_ORDER = -2147482648;

    @Bean
    @ConditionalOnMissingBean
    public ReactorLoadBalancer<ServiceInstance> grayServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            GrayProperties grayProperties) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        // 每个服务独立创建 GrayStrategy，确保 position 按服务隔离
        GrayStrategy grayStrategy = new GrayRuleStrategy(name);
        return new GrayLoadbalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name,
                grayStrategy,
                grayProperties,
                grayProperties.getHeaders());
    }
}
