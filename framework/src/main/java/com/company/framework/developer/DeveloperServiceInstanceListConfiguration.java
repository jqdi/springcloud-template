package com.company.framework.developer;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RetryAwareServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplierBuilder;
import org.springframework.cloud.loadbalancer.support.LoadBalancerEnvironmentPropertyUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.company.framework.developer.policy.ServicePriorityPolicyManager;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 开发者路由负载均衡配置，模仿官方
 * {@link org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration.BlockingSupportConfiguration}
 * 的写法，使用官方 {@link ServiceInstanceListSupplierBuilder} 构建供应商链路。
 *
 * <pre>
 * 链路结构（与官方 zone-preference 一致，仅把 ZonePreference 替换为 DeveloperServiceInstanceListSupplier）：
 *
 *   DiscoveryClientServiceInstanceListSupplier   base，从注册中心拉取实例
 *        ↑ delegate
 *   CachingServiceInstanceListSupplier          官方缓存，LoadBalancerCacheManager 可用时自动启用
 *        ↑ delegate
 *   DeveloperServiceInstanceListSupplier        自定义：按请求头做开发者灰度过滤（最外层装饰器）
 * </pre>
 *
 * @see org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration
 */
@ConditionalOnDiscoveryEnabled
public class DeveloperServiceInstanceListConfiguration implements BeanPostProcessor
//        , BeanDefinitionRegistryPostProcessor
//    , BeanFactoryPostProcessor
//    , ImportBeanDefinitionRegistrar
{

    private final ServicePriorityPolicyManager servicePriorityPolicyManager;
    private final String developerHeaders;

    public DeveloperServiceInstanceListConfiguration(ServicePriorityPolicyManager servicePriorityPolicyManager,
        @Value("${developer.headers}") String developerHeaders) {
        this.servicePriorityPolicyManager = servicePriorityPolicyManager;
        this.developerHeaders = developerHeaders;
    }

    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) {
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof ServiceInstanceListSupplier && !(bean instanceof RetryAwareServiceInstanceListSupplier)) {
            /*
             * 将DeveloperServiceInstanceListSupplier插入到Supplier链中
             *
             * 普通链：
             * RetryAwareServiceInstanceListSupplier->CachingServiceInstanceListSupplier->DiscoveryClientServiceInstanceListSupplier
             *
             * 插入DeveloperServiceInstanceListSupplier：
             * RetryAwareServiceInstanceListSupplier->DeveloperServiceInstanceListSupplier->CachingServiceInstanceListSupplier->DiscoveryClientServiceInstanceListSupplier
             */
            ServiceInstanceListSupplier delegate = (ServiceInstanceListSupplier)bean;
            return new DeveloperServiceInstanceListSupplier(delegate, servicePriorityPolicyManager, developerHeaders);
        }
        return bean;
    }

//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
//        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry, importBeanNameGenerator);
//    }
//
//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
//    }
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println("postProcessBeanFactory");
//    }

//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        System.out.println("postProcessBeanFactory");
//    }
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//        System.out.println("postProcessBeanDefinitionRegistry");
//    }


    /**
     * 模仿官方 {@code discoveryClientServiceInstanceListSupplier} /
     * {@code zonePreferenceDiscoveryClientServiceInstanceListSupplier}， 使用官方 {@link ServiceInstanceListSupplierBuilder}
     * 构建供应商层级，通过 {@link ServiceInstanceListSupplierBuilder#with(ServiceInstanceListSupplierBuilder.DelegateCreator)} 注入自定义的
     * {@link DeveloperServiceInstanceListSupplier} 作为最外层过滤装饰器， 位置等同官方 {@code withZonePreference()}。
     */
//    @Bean
//    @ConditionalOnBean(DiscoveryClient.class)
//    @ConditionalOnMissingBean
//    @Conditional(DeveloperConfigurationCondition.class)
//    @ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
    public ServiceInstanceListSupplier developerDiscoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext context,
        ServicePriorityPolicyManager servicePriorityPolicyManager, @Value("${developer.headers}") String developerHeaders) {

        ServiceInstanceListSupplierBuilder builder = ServiceInstanceListSupplier.builder()
            // base：阻塞式 DiscoveryClient，从注册中心拉取实例（与官方 BlockingSupportConfiguration 一致）
            .withBlockingDiscoveryClient()
            // 官方缓存层：LoadBalancerCacheManager 可用时自动包装 CachingServiceInstanceListSupplier
            .withCaching();

        // 自定义装饰器：按请求头做开发者灰度过滤
        builder.with((delegateContext, delegate) -> new DeveloperServiceInstanceListSupplier(delegate,
            servicePriorityPolicyManager, developerHeaders));

        // 需保留
        boolean zonePreferenceEnable = LoadBalancerEnvironmentPropertyUtils.equalToForClientOrDefault(context.getEnvironment(),
            "configurations", "zone-preference");
        if (zonePreferenceEnable) {
            builder.withZonePreference();
        }
        boolean healthCheckEnable = LoadBalancerEnvironmentPropertyUtils.equalToForClientOrDefault(context.getEnvironment(),
            "configurations", "health-check");
        if (healthCheckEnable) {
            builder.withBlockingHealthChecks();
        }
        return builder.build(context);

    }

    static class DeveloperConfigurationCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return LoadBalancerEnvironmentPropertyUtils.equalToForClientOrDefault(context.getEnvironment(),
                    "configurations", "developer");
        }

    }

//    @Bean
//    @ConditionalOnBean(DiscoveryClient.class)
//    @ConditionalOnMissingBean
//    @Conditional(DeveloperConfigurationCondition.class)
//    @ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
    public void developerDiscoveryClientServiceInstanceListSupplier2(ServiceInstanceListSupplier delegate,
                                                                                           ServicePriorityPolicyManager servicePriorityPolicyManager, @Value("${developer.headers}") String developerHeaders) {

//        while (delegate != null) {
//            if (delegate instanceof DelegatingServiceInstanceListSupplier) {
//                delegate = ((DelegatingServiceInstanceListSupplier)delegate).get();
//            }
//        }
        
        ServiceInstanceListSupplierBuilder builder = ServiceInstanceListSupplier.builder()
                // base：阻塞式 DiscoveryClient，从注册中心拉取实例（与官方 BlockingSupportConfiguration 一致）
                .withBlockingDiscoveryClient()
                // 官方缓存层：LoadBalancerCacheManager 可用时自动包装 CachingServiceInstanceListSupplier
                .withCaching();

        // 自定义装饰器：按请求头做开发者灰度过滤
//        builder.with((delegateContext, delegate) -> new DeveloperServiceInstanceListSupplier(delegate,
//                servicePriorityPolicyManager, developerHeaders));

//        return builder.build(context);

    }
}
