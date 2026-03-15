package com.company.system.datasource;

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
        String userId = HeaderContextUtil.currentUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringContextUtil.getProperty("spring.application.name");
        }
        String device = HeaderContextUtil.currentDevice();
        if (StringUtils.isBlank(device)) {
            device = "none";
        }

        String auditBy = device + ":" + userId;
        LocalDateTime now = LocalDateTime.now();

        AuditableModel<?> auditableModel = new AuditableModel<>();
        auditableModel.setCreateTime(now);
        auditableModel.setCreateBy(auditBy);
        auditableModel.setUpdateTime(now);
        auditableModel.setUpdateBy(auditBy);
        return auditableModel;
    }
}
