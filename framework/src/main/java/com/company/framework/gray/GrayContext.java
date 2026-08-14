package com.company.framework.gray;

import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * 灰度路由上下文（框架层，镜像 gateway 包）。
 *
 * <p>根据路由模式不同，使用不同字段：
 * developer 模式用 {@link #developerList}，release 模式用 {@link #version}。
 */
public class GrayContext {

    private final String version;
    private final List<String> developerList;
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
