package com.company.framework.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.loadbalancer.core.RetryAwareServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import com.company.framework.developer.policy.ServicePriorityPolicyManager;

/**
 * 将DeveloperServiceInstanceListSupplier插入到Supplier链中
 *
 * 普通链：
 * RetryAwareServiceInstanceListSupplier->CachingServiceInstanceListSupplier->DiscoveryClientServiceInstanceListSupplier
 *
 * 插入DeveloperServiceInstanceListSupplier后：
 * RetryAwareServiceInstanceListSupplier->DeveloperServiceInstanceListSupplier->CachingServiceInstanceListSupplier->DiscoveryClientServiceInstanceListSupplier
 */
@ConditionalOnDiscoveryEnabled
public class DeveloperServiceInstanceListConfiguration implements BeanPostProcessor {

    private final ServicePriorityPolicyManager servicePriorityPolicyManager;
    private final String developerHeaders;

    public DeveloperServiceInstanceListConfiguration(ServicePriorityPolicyManager servicePriorityPolicyManager,
        @Value("${developer.headers}") String developerHeaders) {
        this.servicePriorityPolicyManager = servicePriorityPolicyManager;
        this.developerHeaders = developerHeaders;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!(bean instanceof ServiceInstanceListSupplier)) {
            return bean;
        }
        if (bean instanceof RetryAwareServiceInstanceListSupplier) {
            return bean;
        }
        ServiceInstanceListSupplier delegate = (ServiceInstanceListSupplier)bean;
        return new DeveloperServiceInstanceListSupplier(delegate, servicePriorityPolicyManager, developerHeaders);
    }
}
