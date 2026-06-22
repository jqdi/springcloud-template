package com.company.framework.feign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;

/**
 * 打印feign请求(耗时、请求方法、目标机器、请求头、请求体、响应体)信息
 * 
 * <pre>
 *     Logger日志换行太多，自定义的FeignLogger只打印两行（请求一行，响应一行）
 * </pre>
 * 
 * @author JQ棣
 */
@Slf4j
public class FeignLogger extends Logger {
    private final int arrMaxLength;

    public FeignLogger(int arrMaxLength) {
        this.arrMaxLength = arrMaxLength;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        List<Object> args = new ArrayList<>();
        StringBuilder formatAppender = new StringBuilder("--->");
        formatAppender.append(" %s");
        args.add(request.httpMethod().name());
        formatAppender.append(" %s");
        args.add(request.url());
        if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
            String headers = JsonUtil.toJsonStringReplaceProperties(request.headers(), arrMaxLength);
            formatAppender.append(",headers:%s");
            args.add(headers);

            if (request.body() != null) {
                int bodyLength = request.length();
                formatAppender.append(",(%s-byte body)");
                args.add(bodyLength);
                if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                    JsonNode byteJsonNode = JsonUtil.toJsonNode(request.body());
                    String body = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
                    formatAppender.append(",body:%s");
                    args.add(body);
                }
            }
        }
        log(configKey, formatAppender.toString(), args.toArray());
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
        throws IOException {
        int status = response.status();
        List<Object> args = new ArrayList<>();
        StringBuilder formatAppender = new StringBuilder("<---");
        formatAppender.append(" %sms");
        args.add(elapsedTime);
        if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
            if (response.body() != null && !(status == 204 || status == 205)) {
                // HTTP 204 No Content "...response MUST NOT include a message-body"
                // HTTP 205 Reset Content "...response MUST NOT include an entity"
                byte[] bodyData = Util.toByteArray(response.body().asInputStream());
                int bodyLength = bodyData.length;
                formatAppender.append(",(%s-byte body)");
                args.add(bodyLength);
                if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0) {
                    JsonNode byteJsonNode = JsonUtil.toJsonNode(bodyData);
                    String result = JsonUtil.toJsonStringReplaceProperties(byteJsonNode, arrMaxLength);
                    formatAppender.append(",result:%s");
                    args.add(result);
                }
                log(configKey, formatAppender.toString(), args.toArray());
                return response.toBuilder().body(bodyData).build();
            }
        }
        log(configKey, formatAppender.toString(), args.toArray());
        return response;
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        List<Object> args = new ArrayList<>();
        StringBuilder formatAppender = new StringBuilder("<---");
        formatAppender.append(" %sms");
        args.add(elapsedTime);
        formatAppender.append(",ERROR %s: %s");
        args.add(ioe.getClass().getSimpleName());
        args.add(ioe.getMessage());
        log(configKey, formatAppender.toString(), args.toArray());
        return ioe;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info("{}", String.format(methodTag(configKey) + format, args));
    }
}
