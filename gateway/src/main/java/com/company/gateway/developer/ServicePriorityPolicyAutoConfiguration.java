package com.company.gateway.developer;

import com.company.gateway.developer.policy.ServicePriorityPolicy;
import com.company.gateway.developer.policy.ServicePriorityPolicyManager;
import com.company.gateway.developer.policy.impl.DeveloperSelfPriorityPolicy;
import com.company.gateway.developer.policy.impl.OnLineServicePriorityPolicy;
import com.company.gateway.developer.policy.impl.OtherDeveloperPriorityPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "developer.enabled", havingValue = "true")
public class ServicePriorityPolicyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({DeveloperSelfPriorityPolicy.class})
    public ServicePriorityPolicy developerSelfPriorityPolicy() {
        return new DeveloperSelfPriorityPolicy();
    }

    @Bean
    @ConditionalOnMissingBean({OtherDeveloperPriorityPolicy.class})
    public ServicePriorityPolicy otherDeveloperPriorityPolicy() {
        return new OtherDeveloperPriorityPolicy();
    }

    @Bean
    @ConditionalOnMissingBean({OnLineServicePriorityPolicy.class})
    public ServicePriorityPolicy onLineServicePriorityPolicy() {
        return new OnLineServicePriorityPolicy();
    }

    @Bean
    @ConditionalOnMissingBean({ServicePriorityPolicyManager.class})
    public ServicePriorityPolicyManager servicePriorityPolicyManager(List<ServicePriorityPolicy> servicePriorityPolicies) {
        return new ServicePriorityPolicyManager(servicePriorityPolicies);
    }
}
