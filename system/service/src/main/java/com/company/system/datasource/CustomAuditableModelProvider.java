package com.company.system.datasource;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;
import com.company.framework.constant.HeaderConstants;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.SpringContextUtil;

@Component
public class CustomAuditableModelProvider implements AuditableModelProvider {

    @Override
    public AuditableModel<?> getAuditableModel() {
        String userAudit = HeaderContextUtil.head(HeaderConstants.HEADER_CURRENT_USER_AUDIT);
        if (StringUtils.isBlank(userAudit)) {
            userAudit = SpringContextUtil.getProperty("spring.application.name");
        }
        LocalDateTime now = LocalDateTime.now();

        AuditableModel<?> auditableModel = new AuditableModel<>();
        auditableModel.setCreateTime(now);
        auditableModel.setCreateBy(userAudit);
        auditableModel.setUpdateTime(now);
        auditableModel.setUpdateBy(userAudit);
        return auditableModel;
    }
}
