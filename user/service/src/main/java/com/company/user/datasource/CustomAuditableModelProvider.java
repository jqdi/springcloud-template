package com.company.user.datasource;

import java.time.LocalDateTime;
import java.util.Optional;

import com.company.user.entity.UserInfo;
import com.company.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.datasource.AuditableModelProvider;
import com.company.datasource.mybatisplus.activerecord.AuditableModel;
import com.company.framework.context.HeaderContextUtil;

@Component
public class CustomAuditableModelProvider implements AuditableModelProvider {
    private static final String DEFAULT_CURRENT_USER = "system";

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public AuditableModel<?> getAuditableModel() {
        String currentUser;
        Integer userId = HeaderContextUtil.currentUserIdInt();
        if (userId != null) {
            UserInfo userInfo = userInfoService.selectByIdCache(userId);
            currentUser = Optional.ofNullable(userInfo).map(UserInfo::getNickname).orElse(userId.toString());
        } else {
            currentUser = DEFAULT_CURRENT_USER;
        }
        LocalDateTime now = LocalDateTime.now();

        String user = String.format("%s(%s)", currentUser, "C端用户");

        AuditableModel<?> auditableModel = new AuditableModel<>();
        auditableModel.setCreateTime(now);
        auditableModel.setCreateBy(user);
        auditableModel.setUpdateTime(now);
        auditableModel.setUpdateBy(user);
        return auditableModel;
    }
}
