package com.company.job.feign;

import com.company.framework.context.SpringContextUtil;
import org.springframework.stereotype.Component;

import com.company.framework.constant.HeaderConstants;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 设置当前设备为job
 */
@Component
public class CurrentDeviceInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 请求上下文中传递到下游的相关headers
        template.header(HeaderConstants.HEADER_CURRENT_DEVICE, SpringContextUtil.getProperty("spring.application.name"));
    }
}
