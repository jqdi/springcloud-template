package com.company.framework.trace.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import com.company.framework.trace.TraceIdProvider;

public class SkywalkingProvider implements TraceIdProvider {

    private final TraceIdProvider fallbackProvider;

    public SkywalkingProvider(TraceIdProvider fallbackProvider) {
        this.fallbackProvider = fallbackProvider;
    }

    @Override
    public String generateTraceId() {
        String traceId = TraceContext.traceId();
        if (StringUtils.isBlank(traceId) || "Ignored_Trace".equalsIgnoreCase(traceId) || "N/A".equalsIgnoreCase(traceId)) {
            traceId = fallbackProvider.generateTraceId();
        }
        return traceId;
    }
}
