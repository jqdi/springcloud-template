package com.company.framework.feign;

import com.company.framework.util.JsonUtil;
import com.company.framework.context.SpringContextUtil;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

/**
 * 打印feign请求(耗时、请求方法、目标机器、请求头、请求体、响应体)信息
 * <p>
 * 备注：feign.Logger+feign.Logger.Level也可以打印feign请求信息，但是无法打印在同一行，可尝试借助ThreadLocal实现
 */
@Slf4j
@Deprecated // 初始化会导致
public class FeignLoggerClient extends Client.Default {
    /**
     * Null parameters imply platform defaults.
     *
     * @param sslContextFactory
     * @param hostnameVerifier
     */
    public FeignLoggerClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
        super(sslContextFactory, hostnameVerifier);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        long start = System.currentTimeMillis();
        Response response = super.execute(request, options);
        long end = System.currentTimeMillis();

        try {
            int arrMaxLength = SpringContextUtil.getIntegerProperty("template.requestFilter.arrMaxLength", 1000);
            String headers = JsonUtil.toJsonStringReplaceProperties(request.headers(), arrMaxLength);

            String body = "{}";
            // 参考feign.Logger.logRequest逻辑
            if (request.body() != null) {
                JsonNode byteJsonNode = JsonUtil.toJsonNode(request.body());
                body = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
            }

            String result = "{}";
            // 参考feign.Logger.logAndRebufferResponse逻辑
            int status = response.status();
            if (response.body() != null && !(status == 204 || status == 205)) {
                // HTTP 204 No Content "...response MUST NOT include a message-body"
                // HTTP 205 Reset Content "...response MUST NOT include an entity"
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                int bodyLength = bodyData.length;
                if (bodyLength > 0) {
                    JsonNode byteJsonNode = JsonUtil.toJsonNode(bodyData);
                    result = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
                }
                response = response.toBuilder().body(bodyData).build();
            }
            log.info("{}ms {} {} headers:{},body:{},result:{}", end - start, request.httpMethod().name(), request.url(), headers, body, result);
        } catch (Exception e) {
            log.error("feign log error", e);
        }
        return response;
    }
}
