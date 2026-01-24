package com.company.framework.trace.resttemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.company.framework.context.HeaderContextUtil;
import com.company.framework.trace.TraceManager;
import com.google.common.collect.Maps;

/**
 * restTemplate调用过程中传递traceId
 */
public class TraceHeaderClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private final TraceManager traceManager;

    public TraceHeaderClientHttpRequestInterceptor(TraceManager traceManager) {
        this.traceManager = traceManager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        Map<String, List<String>> headers = Maps.newHashMap();
        // 请求上下文中传递到下游的相关headers
        headers.putAll(HeaderContextUtil.httpContextHeaders());
        // 日志追踪ID
        headers.putAll(traceManager.headers());
        HttpHeaders httpHeaders = request.getHeaders();
        if (!headers.isEmpty()) {
            httpHeaders.putAll(headers);
        }
        return execution.execute(request, body);
    }
}
