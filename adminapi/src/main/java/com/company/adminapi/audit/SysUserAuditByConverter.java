package com.company.adminapi.audit;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.company.framework.audit.converter.AuditByConverter;
import com.company.system.api.feign.SysUserFeign;
import com.company.system.api.response.SysUserResp;

/**
 * C端用户审计转换器
 */
@Component
public class SysUserAuditByConverter implements AuditByConverter {

    @Autowired
    private SysUserFeign sysUserFeign;

    @Cacheable(cacheNames = "sys:user", key = "#userId")
    @Override
    public String convert(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        SysUserResp sysUserResp = sysUserFeign.getById(Integer.valueOf(userId));
        String auditBy = Optional.ofNullable(sysUserResp).map(SysUserResp::getAccount).orElse(userId);
        return String.format("%s_%s", "运营", auditBy);
    }
}
