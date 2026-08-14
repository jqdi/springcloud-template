package com.company.gateway.gray;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 灰度流量染色过滤器（第1层，仅 release 模式生效）。
 *
 * <p>在负载均衡之前执行，按 {@link GrayProperties#getDyeRules()} 配置的染色规则判断请求是否应路由到灰度版本，
 * 命中则向请求头注入 {@code x-gray-version} 标识，供后续 {@link GrayLoadbalancer} 消费。
 *
 * <p>染色规则类型：
 * <ul>
 *   <li>{@code header}：请求头 headerName == headerValue 时命中</li>
 *   <li>{@code whitelist}：请求头 userHeader 的值在 userIds 白名单中时命中</li>
 *   <li>{@code percent}：按请求标识（x-deviceid）取 hash % 100 < percent 时命中</li>
 * </ul>
 *
 * <p>多条规则按配置顺序匹配，命中第一个即染色。无命中则不注入（走基线）。
 * <p>developer 模式下此过滤器不执行染色（直接放行）。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "gray.enabled", havingValue = "true")
public class GrayDyeFilter implements GlobalFilter, Ordered {

    /** 染色注入的请求头名 */
    private static final String GRAY_VERSION_HEADER = "x-gray-version";

    /** percent 规则取 hash 用的请求标识头 */
    private static final String DEVICE_ID_HEADER = "x-deviceid";

    private final GrayProperties grayProperties;

    public GrayDyeFilter(GrayProperties grayProperties) {
        this.grayProperties = grayProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // developer 模式不染色，直接放行
        if (!grayProperties.isReleaseMode()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String version = determineVersion(request);

        if (StringUtils.isNotBlank(version)) {
            ServerHttpRequest mutated = request.mutate()
                    .header(GRAY_VERSION_HEADER, version)
                    .build();
            exchange = exchange.mutate().request(mutated).build();
            if (log.isDebugEnabled()) {
                log.debug("Gray dye: inject {}={} for request {}", GRAY_VERSION_HEADER, version, request.getId());
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 早于 ReactiveLoadBalancerClientFilter（Ordered.HIGHEST_PRECEDENCE + 10）
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 遍历染色规则，返回第一个命中的版本号。
     */
    private String determineVersion(ServerHttpRequest request) {
        List<GrayProperties.DyeRule> rules = grayProperties.getDyeRules();
        if (rules == null || rules.isEmpty()) {
            return null;
        }
        for (GrayProperties.DyeRule rule : rules) {
            if (matchRule(request, rule)) {
                return rule.getVersion();
            }
        }
        return null;
    }

    /**
     * 按规则类型判断是否命中。
     */
    private boolean matchRule(ServerHttpRequest request, GrayProperties.DyeRule rule) {
        String type = rule.getType();
        if (StringUtils.isBlank(type)) {
            return false;
        }
        switch (type) {
            case "header":
                return matchHeader(request, rule);
            case "whitelist":
                return matchWhitelist(request, rule);
            case "percent":
                return matchPercent(request, rule);
            default:
                log.warn("Unknown dye rule type: {}", type);
                return false;
        }
    }

    /**
     * header 规则：请求头 headerName == headerValue。
     */
    private boolean matchHeader(ServerHttpRequest request, GrayProperties.DyeRule rule) {
        String value = request.getHeaders().getFirst(rule.getHeaderName());
        return rule.getHeaderValue() != null && rule.getHeaderValue().equalsIgnoreCase(value);
    }

    /**
     * whitelist 规则：请求头 userHeader 的值在 userIds 白名单中。
     */
    private boolean matchWhitelist(ServerHttpRequest request, GrayProperties.DyeRule rule) {
        String userId = request.getHeaders().getFirst(rule.getUserHeader());
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(rule.getUserIds())) {
            return false;
        }
        return Arrays.stream(rule.getUserIds().split(","))
                .map(String::trim)
                .anyMatch(userId::equals);
    }

    /**
     * percent 规则：按请求标识取 hash % 100 < percent。
     */
    private boolean matchPercent(ServerHttpRequest request, GrayProperties.DyeRule rule) {
        String deviceId = request.getHeaders().getFirst(DEVICE_ID_HEADER);
        if (StringUtils.isBlank(deviceId)) {
            // 无设备标识时按请求 URI 取 hash
            deviceId = request.getId() != null ? request.getId() : request.getURI().getPath();
        }
        int hash = Math.abs(deviceId.hashCode());
        return hash % 100 < rule.getPercent();
    }
}
