package com.company.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.framework.constant.CommonConstants;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.filter.request.HeaderMapRequestWrapper;

/**
 * 如果请求头中没有appCode，则填充默认值
 */
@Component
@Order(CommonConstants.FilterOrdered.HTTP_CONTEXT + 1)
public class AppCodeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String appCode = request.getHeader(HeaderConstants.HEADER_APPCODE);
        if (StringUtils.isNotBlank(appCode)) {
            chain.doFilter(request, response);
            return;
        }
        HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);
        headerRequest.addHeader(HeaderConstants.HEADER_APPCODE, "WEB");
        chain.doFilter(headerRequest, response);
    }
}
