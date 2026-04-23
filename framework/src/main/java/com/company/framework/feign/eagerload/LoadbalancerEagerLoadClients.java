package com.company.framework.feign.eagerload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

/**
 * 替代没有内置 eager-load 配置（如 ribbon.eager-load.enabled 或 spring.cloud.loadbalancer.eager-load）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.cloud.loadbalancer.eager-load.clients")
public class LoadbalancerEagerLoadClients implements CommandLineRunner {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Value("${spring.cloud.loadbalancer.eager-load.clients:}")
    private String clients;

    @Override
    public void run(String... args) throws Exception {
        if (StringUtils.isBlank(clients)) {
            log.warn("No clients specified for eager loading. Please set 'spring.cloud.loadbalancer.eager-load.clients' property.");
            return;
        }
        String[] clientArr = StringUtils.split(clients, ",");
        for (String client : clientArr) {
            if (StringUtils.isBlank(client)) {
                continue;
            }
            client = client.trim();
            log.info("Eager loading client: {}", client);
            loadBalancerClient.choose(client);
        }
    }
}
