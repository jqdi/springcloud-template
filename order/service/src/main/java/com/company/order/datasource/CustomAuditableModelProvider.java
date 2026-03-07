package com.company.order.datasource;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;
import com.company.framework.context.HeaderContextUtil;

@Component
public class CustomAuditableModelProvider implements AuditableModelProvider {
    private static final int DEFAULT_CURRENT_USER_ID = 0;

    @Override
    public AuditableModel<?> getAuditableModel() {
        Integer userId = HeaderContextUtil.currentUserIdInt();
        if (userId == null) {
            userId = DEFAULT_CURRENT_USER_ID;
        }
        LocalDateTime now = LocalDateTime.now();

        AuditableModel<?> auditableModel = new AuditableModel<>();
        auditableModel.setCreateTime(now);
        auditableModel.setCreateBy(userId);
        auditableModel.setUpdateTime(now);
        auditableModel.setUpdateBy(userId);
        return auditableModel;
    }
}
