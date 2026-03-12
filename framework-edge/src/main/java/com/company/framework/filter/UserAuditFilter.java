package com.company.framework.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.framework.constant.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.framework.constant.HeaderConstants;
import com.company.framework.filter.request.HeaderMapRequestWrapper;

/**
 * 审计
 */
@Component
@Order(CommonConstants.FilterOrdered.TOKEN + 1)
public class UserAuditFilter extends OncePerRequestFilter {

    @Autowired
    private UserAuditConverter userAuditConverter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String userId = request.getHeader(HeaderConstants.HEADER_CURRENT_USER_ID);
        String userAudit = userAuditConverter.convert(userId);
        HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);
        headerRequest.addHeader(HeaderConstants.HEADER_CURRENT_USER_AUDIT, userAudit);
        chain.doFilter(headerRequest, response);
    }
}
