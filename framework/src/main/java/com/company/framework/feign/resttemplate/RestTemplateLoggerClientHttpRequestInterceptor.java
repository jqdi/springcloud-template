package com.company.framework.feign.resttemplate;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import feign.Util;
import lombok.extern.slf4j.Slf4j;

/**
 * 打印restTemplate请求(耗时、请求方法、目标机器、请求头、请求体、响应体)信息
 */
@Slf4j
public class RestTemplateLoggerClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    public RestTemplateLoggerClientHttpRequestInterceptor() {}

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bodyBytes, ClientHttpRequestExecution execution)
        throws IOException {
        long start = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, bodyBytes);
        long end = System.currentTimeMillis();

        ClientHttpResponse responseWrapper = new BodyReaderClientHttpResponseWrapper(response);
        try {
            int arrMaxLength = SpringContextUtil.getIntegerProperty("template.requestFilter.arrMaxLength", 1000);
            String headers = JsonUtil.toJsonStringReplaceProperties(request.getHeaders(), arrMaxLength);

            String body = "{}";
            // 参考feign.Logger.logRequest逻辑
            if (bodyBytes.length > 0) {
                JsonNode byteJsonNode = JsonUtil.toJsonNode(bodyBytes);
                body = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
            }

            String result = "{}";
            // 参考feign.Logger.logAndRebufferResponse逻辑
            HttpStatus statusCode = responseWrapper.getStatusCode();
            if (responseWrapper.getBody() != null && !(statusCode == HttpStatus.NO_CONTENT || statusCode == HttpStatus.RESET_CONTENT)) {
                // HTTP 204 No Content "...response MUST NOT include a message-body"
                // HTTP 205 Reset Content "...response MUST NOT include an entity"
                byte[] bodyData = Util.toByteArray(responseWrapper.getBody());
                int bodyLength = bodyData.length;
                if (bodyLength > 0) {
                    JsonNode byteJsonNode = JsonUtil.toJsonNode(bodyData);
                    result = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
                }
            }
            log.info("{}ms {} {} headers:{},body:{},result:{}", end - start, request.getMethod(), request.getURI(),
                headers, body, result);
        } catch (Exception e) {
            log.error("restTemplate log error", e);
        }
        return responseWrapper;
    }
}
