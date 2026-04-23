package com.company.job.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.company.framework.trace.TraceManager;
import com.xxl.job.core.context.XxlJobHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 打印入参切面
 *
 * @author JQ棣
 */
@Slf4j
@Aspect
@Component
@Order(20) // 优先级要比TraceAspect底
public class LogParamAspect {

    // 在jobHander执行之前打印入参
    @Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void logParam() {
        String param = XxlJobHelper.getJobParam();
        XxlJobHelper.log("param:{}", param);
        log.info("param:{}", param);
    }
}
