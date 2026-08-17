/**
 * 灰度流量路由模块（网关层），整合原 developer 与 gray 两个模块。
 *
 * <p>通过 {@code gray.mode} 切换路由策略：
 * <ul>
 *   <li>{@code developer}：开发调试路由，将开发者请求路由到本地实例（原 developer 模块）</li>
 *   <li>{@code release}：灰度发布路由，按版本号泳道隔离（原 gray 模块）</li>
 * </ul>
 *
 * <p>三层架构（release 模式）：
 * <ol>
 *   <li>流量染色：{@link com.company.gateway.gray.GrayDyeFilter} 按规则给请求注入灰度版本头</li>
 *   <li>标记透传：framework 包的 GrayFeignInterceptor 把灰度头透传到下游 Feign 调用</li>
 *   <li>智能路由：{@link com.company.gateway.gray.GrayLoadbalancer} 按版本号匹配实例 metadata，泳道隔离 fallback 到基线</li>
 * </ol>
 *
 * <p>dev/test/pre 环境使用 developer 模式本地联调，pre/prod 环境使用 release 模式灰度发布。
 */
package com.company.gateway.gray;
