package com.company.tool.datasource;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.SpringContextUtil;

@Component
public class CustomAuditableModelProvider implements AuditableModelProvider {

    @Override
    public AuditableModel<?> getAuditableModel() {
        String auditBy = HeaderContextUtil.currentUserAudit();
        if (StringUtils.isBlank(auditBy)) {
            auditBy = SpringContextUtil.getProperty("spring.application.name");
        }
        LocalDateTime now = LocalDateTime.now();

        AuditableModel<?> auditableModel = new AuditableModel<>();
        auditableModel.setCreateTime(now);
        auditableModel.setCreateBy(auditBy);
        auditableModel.setUpdateTime(now);
        auditableModel.setUpdateBy(auditBy);
        return auditableModel;
    }
}
