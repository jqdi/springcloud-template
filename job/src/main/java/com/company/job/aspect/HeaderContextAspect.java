package com.company.job.aspect;

import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.HttpContextUtil;
import com.company.framework.util.HostUtil;
import com.google.common.collect.Maps;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.company.framework.trace.TraceManager;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * HeaderContext切面
 *
 * @author JQ棣
 */
@Slf4j
@Aspect
@Component("headerContextAspect")
@Order(10)
public class HeaderContextAspect {

	// 在jobHander执行之前塞入HeaderContext
    @Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void setHeaderContext() {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put(HeaderConstants.HEADER_PLATFORM, "xxljob");
        headerMap.put(HeaderConstants.HEADER_OPERATOR, "xxljob");
        headerMap.put(HeaderConstants.HEADER_VERSION, "1.0.0");
        headerMap.put(HeaderConstants.HEADER_DEVICEID, "xxljob");
        headerMap.put(HeaderConstants.HEADER_APPCODE, "xxljob");
        headerMap.put(HeaderConstants.HEADER_CHANNEL, "xxljob");
        headerMap.put(HeaderConstants.HEADER_REQUESTIP, HostUtil.ip());
        headerMap.put(HeaderConstants.HEADER_SOURCE, "xxljob");
        HeaderContextUtil.setHeaderMap(headerMap);
        log.info("初始化HeaderContext");
    }

	// 执行完毕之后清理HeaderContext
	@After("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
	public void clearHeaderContext() {
		log.info("清除HeaderContext");
        HeaderContextUtil.remove();
	}

}
