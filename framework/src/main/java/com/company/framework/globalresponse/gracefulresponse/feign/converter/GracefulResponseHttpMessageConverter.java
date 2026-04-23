package com.company.framework.globalresponse.gracefulresponse.feign.converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Optional;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.feiniaojin.gracefulresponse.api.ResponseFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseImplStyle0;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseImplStyle1;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

/**
 * GracefulResponse HttpMessageConverter，用于处理RestTemplate Feign返回的数据适配GracefulResponse
 */
@Slf4j
public class GracefulResponseHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private final GracefulResponseProperties gracefulResponseProperties;
    private final ResponseFactory responseBeanFactory;

    public GracefulResponseHttpMessageConverter(GracefulResponseProperties gracefulResponseProperties,
        ResponseFactory responseBeanFactory) {
        this.gracefulResponseProperties = gracefulResponseProperties;
        this.responseBeanFactory = responseBeanFactory;
    }

    public GracefulResponseHttpMessageConverter(ObjectMapper objectMapper, GracefulResponseProperties gracefulResponseProperties,
        ResponseFactory responseBeanFactory) {
        super(objectMapper);
        this.gracefulResponseProperties = gracefulResponseProperties;
        this.responseBeanFactory = responseBeanFactory;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(type, contextClass);
        // return readJavaType(javaType, inputMessage);
        Pair<Boolean, Object> pair = gracefulResponseValue(inputMessage, javaType);
        if (pair.getKey()) {
            return pair.getValue();
        }
        return super.read(type, contextClass, inputMessage);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
        throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(clazz, null);
        // return readJavaType(javaType, inputMessage);
        Pair<Boolean, Object> pair = gracefulResponseValue(inputMessage, javaType);
        if (pair.getKey()) {
            return pair.getValue();
        }
        return super.readInternal(clazz, inputMessage);
    }

    private Pair<Boolean, Object> gracefulResponseValue(HttpInputMessage inputMessage, JavaType javaType) throws IOException {
        ObjectMapper objectMapper = getObjectMapper();
        InputStream inputStream = inputMessage.getBody();
        JsonNode responseJson = objectMapper.readTree(inputStream);

        if (responseJson == null) {
            return new Pair<>(false, null);
        }

        Response response = responseBeanFactory.newEmptyInstance();
        if (response instanceof DefaultResponseImplStyle0) {
            JsonNode statusNode = responseJson.get("status");
            String code = Optional.ofNullable(statusNode).map(v -> v.get("code")).map(JsonNode::asText).orElse(null);
            String msg = Optional.ofNullable(statusNode).map(v -> v.get("msg")).map(JsonNode::asText).orElse(null);
            ResponseStatus status = new DefaultResponseStatus(code, msg);
            response.setStatus(status);
            response.setPayload(responseJson.get("payload"));
        } else if (response instanceof DefaultResponseImplStyle1) {
            String code = Optional.ofNullable(responseJson.get("code")).map(JsonNode::asText).orElse(null);
            String msg = Optional.ofNullable(responseJson.get("msg")).map(JsonNode::asText).orElse(null);
            ResponseStatus status = new DefaultResponseStatus(code, msg);
            response.setStatus(status);
            response.setPayload(responseJson.get("data"));
        } else {
            String responseClassFullName = gracefulResponseProperties.getResponseClassFullName();
            Class<?> responseClass;
            try {
                responseClass = Class.forName(responseClassFullName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            response = (Response)objectMapper.readValue(responseJson.toString(), responseClass);
        }

        if (response == null) {
            return new Pair<>(false, null);
        }
        ResponseStatus status = response.getStatus();
        if (status == null) {
            return new Pair<>(false, null);
        }

        // 是否包含code字段
        String code = status.getCode();
        if (!StringUtils.hasText(code)) {
            return new Pair<>(false, null);
        }

        // 是否包含msg字段或者data字段
        String msg = status.getMsg();
        Object data = response.getPayload();
        if (!StringUtils.hasText(msg) && data == null) {
            return new Pair<>(false, null);
        }

        // 是GracefulResponse包装
        String defaultSuccessCode = gracefulResponseProperties.getDefaultSuccessCode();
        if (!defaultSuccessCode.equals(code)) {
            GracefulResponse.raiseException(code, msg);
            return new Pair<>(false, null);
        }
        if (data == null) {
            return new Pair<>(true, null);
        }
        return new Pair<>(true, objectMapper.readValue(objectMapper.writeValueAsString(data), javaType));
    }
}
