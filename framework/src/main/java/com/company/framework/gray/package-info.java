/**
 * 灰度流量路由模块（框架层，用于 Feign 内部调用）。
 *
 * <p>镜像 gateway 包的灰度路由能力，用于微服务间 Feign 调用也走灰度路由。
 * 配合 {@link com.company.framework.gray.GrayFeignInterceptor} 透传灰度标识，
 * 实现全链路灰度不串流。
 *
 * <p>与 developer 模块互斥：通过 {@code gray.enabled} 与 {@code developer.enabled} 配置开关控制。
 */
package com.company.framework.gray;
