package com.company.framework.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.company.framework.audit.converter.AuditByConverter;
import com.company.framework.audit.converter.DefaultAuditByConverter;
import com.company.framework.audit.filter.AuditByFilter;
import com.company.framework.constant.CommonConstants;

@Configuration(proxyBeanMethods = false)
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditByConverter auditByConverter() {
        return new DefaultAuditByConverter();
    }

    @Bean
    @Order(CommonConstants.FilterOrdered.AUDIT_BY) // 注：一定要在TokenFilter之后执行,HeaderContextFilter之前执行
    public AuditByFilter auditByFilter(AuditByConverter auditByConverter) {
        return new AuditByFilter(auditByConverter);
    }
}
