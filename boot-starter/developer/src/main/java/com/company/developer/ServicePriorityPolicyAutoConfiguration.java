package com.company.developer;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.company.developer.policy.ServicePriorityPolicy;
import com.company.developer.policy.ServicePriorityPolicyManager;
import com.company.developer.policy.impl.DeveloperSelfPriorityPolicy;
import com.company.developer.policy.impl.OnLineServicePriorityPolicy;
import com.company.developer.policy.impl.OtherDeveloperPriorityPolicy;

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
