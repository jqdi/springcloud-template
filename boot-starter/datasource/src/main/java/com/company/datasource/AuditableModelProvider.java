package com.company.datasource;

import com.company.datasource.mybatisplus.activerecord.AuditableModel;

/**
 * 审计字段提供者
 */
public interface AuditableModelProvider {
    AuditableModel<?> getAuditableModel();
}
