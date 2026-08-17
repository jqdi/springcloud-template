/**
 * 灰度流量路由模块（框架层，用于 Feign 内部调用），整合原 developer 与 gray 两个模块。
 *
 * <p>镜像 gateway 包的灰度路由能力，用于微服务间 Feign 调用也走灰度路由。
 * 配合 {@link com.company.framework.gray.GrayFeignInterceptor} 透传灰度标识，
 * 实现全链路灰度不串流。
 *
 * <p>通过 {@code gray.mode} 切换路由策略：{@code developer}（开发调试）| {@code release}（灰度发布）。
 */
package com.company.framework.gray;
