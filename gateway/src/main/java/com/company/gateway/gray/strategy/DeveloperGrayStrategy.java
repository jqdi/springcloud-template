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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 开发调试灰度策略（developer 模式）。
 *
 * <p>原 developer 模块路由逻辑的简化实现，按优先级分组取最高组，组内轮询：
 * <ol>
 *   <li>metadata.developer 匹配请求头 → 优先级 1000（开发者自己的本地实例）</li>
 *   <li>metadata.developer_route_tag 含 ONLINE → 优先级 2000（环境在线服务）</li>
 *   <li>无 developer metadata → 优先级 10000（兜底普通实例）</li>
 *   <li>metadata.developer 存在但不匹配 → 优先级 20000（其他开发者本地实例）</li>
 * </ol>
 */
@Slf4j
public class DeveloperGrayStrategy implements GrayStrategy {

    private final AtomicInteger position;
    private final String serviceId;

    public DeveloperGrayStrategy(String serviceId) {
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

        List<String> developerList = context.getDeveloperList();

        // 按优先级分组
        Map<Integer, List<ServiceInstance>> groups = instances.stream()
                .collect(Collectors.groupingBy(v -> getServerOrder(v, developerList, properties)));

        // 取最高优先级组
        Optional<Integer> minOrder = groups.keySet().stream().min(Integer::compareTo);
        List<ServiceInstance> highest = minOrder.map(groups::get).orElse(instances);

        return roundRobin(highest);
    }

    /**
     * 计算实例优先级（数字越小优先级越高）。
     */
    private int getServerOrder(ServiceInstance instance, List<String> developerList, GrayProperties properties) {
        Map<String, String> metadata = instance.getMetadata();
        if (metadata == null || metadata.isEmpty()) {
            return 10000;
        }
        String developerMetadataKey = properties.getDeveloperMetadataKey();
        String developer = metadata.get(developerMetadataKey);
        if (StringUtils.isNotBlank(developer)) {
            if (developerList != null && developerList.contains(developer)) {
                return 1000;
            }
            return 20000;
        }
        String tag = metadata.get("developer_route_tag");
        if (StringUtils.isNotBlank(tag) && tag.contains("ONLINE")) {
            return 2000;
        }
        return 10000;
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
