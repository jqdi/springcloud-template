package com.company.framework.audit.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.company.framework.audit.converter.AuditByConverter;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.filter.request.HeaderMapRequestWrapper;

/**
 * 审计
 */
public class AuditByFilter extends OncePerRequestFilter {

    private final AuditByConverter auditByConverter;

    public AuditByFilter(AuditByConverter auditByConverter) {
        this.auditByConverter = auditByConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String userId = request.getHeader(HeaderConstants.HEADER_CURRENT_USER_ID);
        String userAudit = auditByConverter.convert(userId);
        if (userAudit == null) {
            chain.doFilter(request, response);
            return;
        }
        HeaderMapRequestWrapper headerRequest = new HeaderMapRequestWrapper(request);
        headerRequest.addHeader(HeaderConstants.HEADER_CURRENT_USER_AUDIT, userAudit);
        chain.doFilter(headerRequest, response);
    }
}
