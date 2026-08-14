package com.company.gateway.gray.strategy;

import com.company.gateway.gray.GrayContext;
import com.company.gateway.gray.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Response;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 灰度发布策略（release 模式）：基于版本号的泳道隔离路由。
 *
 * <p>路由算法：
 * <ol>
 *   <li>请求无灰度版本号 → 路由到基线实例（无 version metadata 的实例）</li>
 *   <li>请求有灰度版本号 → 先匹配同版本实例，命中则组内轮询</li>
 *   <li>无同版本实例且 fallbackToBaseline=true → 回退到基线实例（不跨版本）</li>
 * </ol>
 */
@Slf4j
public class ReleaseGrayStrategy implements GrayStrategy {

    private final AtomicInteger position;
    private final String serviceId;

    public ReleaseGrayStrategy(String serviceId) {
        this.serviceId = serviceId;
        this.position = new AtomicInteger(new java.util.Random().nextInt(1000));
    }

    @Override
    public Response<ServiceInstance> choose(List<ServiceInstance> instances,
                                            GrayContext context,
                                            GrayProperties properties) {
        if (instances == null || instances.isEmpty()) {
            log.warn("No servers available for service: {}", serviceId);
            return new EmptyResponse();
        }

        String reqVersion = context.getVersion();

        // 无灰度标 → 只选基线实例
        if (StringUtils.isBlank(reqVersion)) {
            List<ServiceInstance> baseline = filterBaseline(instances, properties);
            return roundRobin(baseline);
        }

        // 有灰度标 → 先选同版本实例
        List<ServiceInstance> matched = filterByVersion(instances, reqVersion, properties);
        if (!matched.isEmpty()) {
            return roundRobin(matched);
        }

        // 无同版本 → fallback 到基线（不跨版本）
        if (properties.isFallbackToBaseline()) {
            List<ServiceInstance> baseline = filterBaseline(instances, properties);
            if (baseline.isEmpty()) {
                log.warn("No baseline servers for service: {}, all instances have version metadata", serviceId);
                return new EmptyResponse();
            }
            return roundRobin(baseline);
        }

        return new EmptyResponse();
    }

    private List<ServiceInstance> filterByVersion(List<ServiceInstance> instances,
                                                   String version,
                                                   GrayProperties properties) {
        String key = properties.getVersionMetadataKey();
        return instances.stream()
                .filter(i -> version.equalsIgnoreCase(i.getMetadata().get(key)))
                .collect(Collectors.toList());
    }

    private List<ServiceInstance> filterBaseline(List<ServiceInstance> instances,
                                                  GrayProperties properties) {
        String key = properties.getVersionMetadataKey();
        return instances.stream()
                .filter(i -> StringUtils.isBlank(i.getMetadata().get(key)))
                .collect(Collectors.toList());
    }

    private Response<ServiceInstance> roundRobin(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: {}", serviceId);
            return new EmptyResponse();
        }
        if (instances.size() == 1) {
            return new DefaultResponse(instances.get(0));
        }
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
        ServiceInstance instance = instances.get(pos % instances.size());
        return new DefaultResponse(instance);
    }
}
