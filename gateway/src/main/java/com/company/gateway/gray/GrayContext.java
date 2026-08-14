package com.company.gateway.gray;

import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * 灰度路由上下文，封装从请求中提取的路由标识和原始请求头。
 *
 * <p>不可变对象，由 {@link GrayLoadbalancer} 构建后传递给 {@link com.company.gateway.gray.strategy.GrayStrategy}。
 * 根据路由模式不同，使用不同字段：
 * <ul>
 *   <li>developer 模式：使用 {@link #developerList}</li>
 *   <li>release 模式：使用 {@link #version}</li>
 * </ul>
 */
public class GrayContext {

    /** release 模式：从请求头提取的灰度版本号，可能为 null */
    private final String version;

    /** developer 模式：从请求头提取的开发者标识列表，可能为 null */
    private final List<String> developerList;

    /** 原始 HTTP 请求头 */
    private final HttpHeaders httpHeaders;

    public GrayContext(String version, List<String> developerList, HttpHeaders httpHeaders) {
        this.version = version;
        this.developerList = developerList;
        this.httpHeaders = httpHeaders;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getDeveloperList() {
        return developerList;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }
}
