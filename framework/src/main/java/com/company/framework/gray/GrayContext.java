package com.company.framework.gray;

import org.springframework.http.HttpHeaders;

/**
 * 灰度路由上下文，封装从请求中提取的灰度版本号和原始请求头。
 *
 * <p>不可变对象，由 {@link GrayLoadbalancer} 构建后传递给 {@link com.company.framework.gray.strategy.GrayStrategy}。
 */
public class GrayContext {

    /** 从请求头提取的灰度版本号，可能为 null（表示无灰度标，走基线） */
    private final String version;

    /** 原始 HTTP 请求头 */
    private final HttpHeaders httpHeaders;

    public GrayContext(String version, HttpHeaders httpHeaders) {
        this.version = version;
        this.httpHeaders = httpHeaders;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
