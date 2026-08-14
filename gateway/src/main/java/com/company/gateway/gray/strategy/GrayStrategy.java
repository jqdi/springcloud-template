package com.company.gateway.gray.strategy;

import com.company.gateway.gray.GrayContext;
import com.company.gateway.gray.GrayProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;

import java.util.List;

/**
 * 灰度路由策略接口。
 *
 * <p>不同实现提供不同的路由算法（如规则灰度、权重灰度）。
 * 当前默认实现 {@link GrayRuleStrategy} 提供基于版本号的泳道隔离路由。
 */
public interface GrayStrategy {

    /**
     * 从全量实例中选出目标实例。
     *
     * @param instances 全量服务实例
     * @param context   灰度上下文（版本号 + 请求头）
     * @param properties 灰度配置
     * @return 选中的实例响应
     */
    Response<ServiceInstance> choose(List<ServiceInstance> instances,
                                     GrayContext context,
                                     GrayProperties properties);
}
