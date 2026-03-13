package com.company.app.audit;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.company.framework.audit.converter.AuditByConverter;
import com.company.user.api.feign.UserInfoFeign;
import com.company.user.api.response.UserInfoResp;

/**
 * C端用户审计转换器
 */
@Component
public class UserInfoAuditByConverter implements AuditByConverter {

    @Autowired
    private UserInfoFeign userInfoFeign;

    @Cacheable(cacheNames = "user:info", key = "#userId")
    @Override
    public String convert(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        UserInfoResp userInfoResp = userInfoFeign.getById(Integer.valueOf(userId));
        String auditBy = Optional.ofNullable(userInfoResp).map(UserInfoResp::getNickname).orElse(userId);
        return String.format("%s_%s", "C端", auditBy);
    }
}
