package com.company.framework.globalresponse.gracefulresponse.feign;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import com.company.framework.globalresponse.gracefulresponse.feign.context.GracefulResponseExceptionContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseImplStyle0;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseImplStyle1;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;

import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign的GracefulResponse解码器
 */
@Slf4j
public class GracefulResponseDecoder extends SpringDecoder {

    private final ObjectMapper objectMapper;
    private final GracefulResponseProperties gracefulResponseProperties;
    private final ResponseFactory responseBeanFactory;

    public GracefulResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters, ObjectMapper objectMapper,
        GracefulResponseProperties gracefulResponseProperties, ResponseFactory responseBeanFactory) {
        super(messageConverters);
        this.objectMapper = objectMapper;
        this.gracefulResponseProperties = gracefulResponseProperties;
        this.responseBeanFactory = responseBeanFactory;
    }

    public GracefulResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
        ObjectProvider<HttpMessageConverterCustomizer> customizers, ObjectMapper objectMapper,
        GracefulResponseProperties gracefulResponseProperties, ResponseFactory responseBeanFactory) {
        super(messageConverters, customizers);
        this.objectMapper = objectMapper;
        this.gracefulResponseProperties = gracefulResponseProperties;
        this.responseBeanFactory = responseBeanFactory;
    }

    @Override
    public Object decode(final Response response, Type type) throws IOException, FeignException {
        InputStream inputStream = response.body().asInputStream();
        JsonNode responseJson = objectMapper.readTree(inputStream);
        if (responseJson == null) {
            return super.decode(response, type);
        }

        com.feiniaojin.gracefulresponse.data.Response grResponse = responseBeanFactory.newEmptyInstance();
        if (grResponse instanceof DefaultResponseImplStyle0) {
            JsonNode statusNode = responseJson.get("status");
            String code = Optional.ofNullable(statusNode).map(v -> v.get("code")).map(JsonNode::asText).orElse(null);
            String msg = Optional.ofNullable(statusNode).map(v -> v.get("msg")).map(JsonNode::asText).orElse(null);
            ResponseStatus status = new DefaultResponseStatus(code, msg);
            grResponse.setStatus(status);
            grResponse.setPayload(responseJson.get("payload"));
        } else if (grResponse instanceof DefaultResponseImplStyle1) {
            String code = Optional.ofNullable(responseJson.get("code")).map(JsonNode::asText).orElse(null);
            String msg = Optional.ofNullable(responseJson.get("msg")).map(JsonNode::asText).orElse(null);
            ResponseStatus status = new DefaultResponseStatus(code, msg);
            grResponse.setStatus(status);
            grResponse.setPayload(responseJson.get("data"));
        } else {
            String responseClassFullName = gracefulResponseProperties.getResponseClassFullName();
            Class<?> responseClass;
            try {
                responseClass = Class.forName(responseClassFullName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            grResponse = (com.feiniaojin.gracefulresponse.data.Response)objectMapper.readValue(responseJson.toString(), responseClass);
        }

        if (grResponse == null) {
            return super.decode(response, type);
        }
        ResponseStatus status = grResponse.getStatus();
        if (status == null) {
            return super.decode(response, type);
        }

        // 是否包含code字段
        String code = status.getCode();
        if (!org.springframework.util.StringUtils.hasText(code)) {
            return super.decode(response, type);
        }

        // 是否包含msg字段或者data字段
        String msg = status.getMsg();
        Object data = grResponse.getPayload();
        if (!org.springframework.util.StringUtils.hasText(msg) && data == null) {
            return super.decode(response, type);
        }

        // 是GracefulResponse包装
        String defaultSuccessCode = gracefulResponseProperties.getDefaultSuccessCode();
        if (!defaultSuccessCode.equals(code)) {
            GracefulResponseExceptionContext.setException(new GracefulResponseException(code, msg));
            return null;
        }
        if (data == null) {
            return null;
        }
        String dataStr = objectMapper.writeValueAsString(data);
        Response dataResponse = Response.builder()
                    .status(response.status())
                    .reason(response.reason())
                    .headers(response.headers())
                    .body(dataStr, StandardCharsets.UTF_8)
                    .request(response.request())
                    .protocolVersion(response.protocolVersion())
                    .build();
        return super.decode(dataResponse, type);
    }
}
