package com.company.gateway.gray;

import com.company.gateway.gray.strategy.DeveloperGrayStrategy;
import com.company.gateway.gray.strategy.GrayStrategy;
import com.company.gateway.gray.strategy.ReleaseGrayStrategy;
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
 * <p>根据 {@code gray.mode} 创建对应的策略实例：
 * <ul>
 *   <li>developer 模式 → {@link DeveloperGrayStrategy}</li>
 *   <li>release 模式 → {@link ReleaseGrayStrategy}</li>
 * </ul>
 * 每个服务独立创建策略实例，确保 position 按服务隔离。
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
        // 根据 mode 创建对应策略
        GrayStrategy grayStrategy;
        if (grayProperties.isReleaseMode()) {
            grayStrategy = new ReleaseGrayStrategy(name);
        } else {
            grayStrategy = new DeveloperGrayStrategy(name);
        }
        return new GrayLoadbalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name,
                grayStrategy,
                grayProperties);
    }
}
