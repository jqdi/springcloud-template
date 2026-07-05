package com.company.system.api.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.exception.ResultException;
import com.company.common.fallback.FallbackUtil;

import feign.FeignException;
import feign.Request;
import lombok.extern.slf4j.Slf4j;

/**
 * 通用抛异常降级
 */
@Slf4j
@Component("systemThrowExceptionFallback")
public class ThrowExceptionFallback<Object> implements FallbackFactory<Object> {

    @Override
    public Object create(final Throwable t) {
        Throwable e = t;
        while (e != null) {
            if (e instanceof ResultException) {
                throw (ResultException)e;
            }
            Throwable cause = e.getCause();
            if (cause == null) {
                break;
            }
            e = cause;
        }
        if (e instanceof FeignException) {
            FeignException fe = (FeignException) e;
            Request request = fe.request();
            String url = request.url();
            log.error("feign fallback,url:{},message:{}", url, fe.getMessage());
        } else {
            log.error("fallback error,message:{}", e.getMessage());
        }
        return FallbackUtil.create();
    }
}
