package com.company.framework.globalresponse.gracefulresponse.feign;

import com.company.framework.globalresponse.gracefulresponse.feign.context.GracefulResponseExceptionContext;
import com.feiniaojin.gracefulresponse.GracefulResponseException;
import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GracefulResponse feign异常适配 切面
 *
 * @author JQ棣
 */
@Slf4j
@Aspect
@Component
public class GracefulResponseFeignExceptionAspect {

    @Autowired
    private GracefulResponseProperties gracefulResponseProperties;

    /**
     * 在Feign调用返回后
     *
     * @param result
     */
    @AfterReturning(pointcut = "@within(org.springframework.cloud.openfeign.FeignClient)", returning = "result")
    public void afterAnyFeignCall(Object result) {
        if (result != null) {
            return;
        }
        GracefulResponseException gracefulResponseException = GracefulResponseExceptionContext.getAndRemoveException();
        if (gracefulResponseException == null) {
            return;
        }
        String defaultSuccessCode = gracefulResponseProperties.getDefaultSuccessCode();
        if (!defaultSuccessCode.equals(gracefulResponseException.getCode())) {
            throw gracefulResponseException;
        }
    }
}
