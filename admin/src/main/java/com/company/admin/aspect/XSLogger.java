package com.company.admin.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 日志
 * Created by JQ棣 on 2017/10/20.
 */
public class XSLogger {

    private static final Logger logger = LoggerFactory.getLogger(XSLogger.class);

    private String requestIp;

    private String host;

    private Date requestTime;

    private String requestMethod;

    private String requestUrl;

    private Boolean status;

    private List<?> parameters;

    private String result;

    private String executionDuration;

    public void log() {
        logger.info("{}", this);
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<?> getParameters() {
        return parameters;
    }

    public void setParameters(List<?> parameters) {
        this.parameters = parameters;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(String executionDuration) {
        this.executionDuration = executionDuration;
    }
}
