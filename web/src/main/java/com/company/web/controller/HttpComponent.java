package com.company.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.company.framework.globalresponse.BusinessException;
import com.company.framework.globalresponse.ExceptionUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpComponent implements InitializingBean, DisposableBean {
    private CloseableHttpClient httpClient;

    public String put(String url, byte[] fileBytes) {
        HttpPut httpPut = new HttpPut(url);
        HttpEntity entity = new ByteArrayEntity(fileBytes);
        httpPut.setEntity(entity);
        return execute(httpPut);
    }

    public String put(String url, File file) {
        HttpPut httpPut = new HttpPut(url);
        HttpEntity entity = new FileEntity(file);
        httpPut.setEntity(entity);
        return execute(httpPut);
    }

    private String execute(HttpUriRequest request) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("响应状态码：{}", statusCode);
            if (statusCode != 200) {
                ExceptionUtil.throwException("dddddd");
            }
            return IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("execute error", e);
            ExceptionUtil.throwException("dddddd");
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        httpClient = HttpClients.createDefault();
    }

    @Override
    public void destroy() throws Exception {
        HttpClientUtils.closeQuietly(httpClient);
    }
}
