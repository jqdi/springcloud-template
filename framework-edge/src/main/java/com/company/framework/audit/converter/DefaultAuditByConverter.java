package com.company.framework.audit.converter;

/**
 * 默认审计转换器
 */
public class DefaultAuditByConverter implements AuditByConverter {
    @Override
    public String convert(String userId) {
        return userId;
    }
}
