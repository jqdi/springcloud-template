package com.company.framework.filter;

/**
 * 审计转换器
 */
public interface UserAuditConverter {
    String convert(String userId);
}
