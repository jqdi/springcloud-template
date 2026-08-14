package com.company.framework.gray;

import com.company.framework.context.HttpContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 灰度标识 Feign 透传拦截器（第2层，仅 framework 层）。
 *
 * <p>在 Feign 调用时，从当前 HTTP 请求中提取灰度版本头，透传到下游服务，
 * 确保全链路灰度不串流。
 *
 * <p>注：项目已有的 {@link com.company.framework.feign.HttpHeaderInterceptor} 透传
 * {@link com.company.framework.context.HeaderContextUtil} 中的固定头列表，但不包含灰度头。
 * 本拦截器专门透传 {@code gray.headers} 配置的灰度头。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "gray.enabled", havingValue = "true")
public class GrayFeignInterceptor implements RequestInterceptor {

    private final GrayProperties grayProperties;

    public GrayFeignInterceptor(GrayProperties grayProperties) {
        this.grayProperties = grayProperties;
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = HttpContextUtil.request();
        if (request == null) {
            return;
        }

        String headers = grayProperties.getHeaders();
        if (StringUtils.isBlank(headers)) {
            return;
        }

        Arrays.stream(headers.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .forEach(headerName -> {
                    String value = request.getHeader(headerName);
                    if (StringUtils.isNotBlank(value)) {
                        template.header(headerName, value);
                        if (log.isDebugEnabled()) {
                            log.debug("Gray feign: propagate {}={} to downstream", headerName, value);
                        }
                    }
                });
    }
}
