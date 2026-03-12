package com.company.framework.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 默认审计转换器
 */
@Component
@ConditionalOnMissingBean(UserAuditConverter.class)
public class DefaultUserAuditConverter implements UserAuditConverter {
    @Override
    public String convert(String userId) {
        return userId;
    }
}
