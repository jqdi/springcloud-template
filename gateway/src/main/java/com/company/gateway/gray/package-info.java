/**
 * 灰度流量路由模块（网关层）。
 *
 * <p>三层架构：
 * <ol>
 *   <li>流量染色：{@link com.company.gateway.gray.GrayDyeFilter} 按规则给请求注入灰度版本头</li>
 *   <li>标记透传：framework 包的 GrayFeignInterceptor 把灰度头透传到下游 Feign 调用</li>
 *   <li>智能路由：{@link com.company.gateway.gray.GrayLoadbalancer} 按版本号匹配实例 metadata，泳道隔离 fallback 到基线</li>
 * </ol>
 *
 * <p>与 developer 模块互斥：通过 {@code gray.enabled} 与 {@code developer.enabled} 配置开关控制，
 * prod 环境使用 gray，dev/test/pre 使用 developer。
 */
package com.company.gateway.gray;
