package com.company.framework.trace.feign;

import com.company.framework.trace.TraceManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * feign调用过程中传递traceId
 */
@Component
public class TraceHeaderInterceptor implements RequestInterceptor {

    @Autowired
    private TraceManager traceManager;

    @Override
    public void apply(RequestTemplate template) {
        traceManager.headers().forEach((template::header));
    }
}
