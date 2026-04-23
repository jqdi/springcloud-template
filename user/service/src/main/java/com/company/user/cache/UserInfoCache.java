package com.company.user.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.cache.ICache;
import com.company.framework.util.JsonUtil;
import com.company.user.entity.UserInfo;
import com.company.user.service.UserInfoService;

@Component
public class UserInfoCache {
    private static final String KEY_PATTERN = "user:userinfo:%s";

    @Autowired
    private ICache cache;
    @Autowired
    private UserInfoService userInfoService;

    public UserInfo getById(Integer id) {
        String key = String.format(KEY_PATTERN, id);
        return cache.get(key, () -> {
            UserInfo userInfo = userInfoService.getById(id);
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            return JsonUtil.toJsonString(userInfo);
        }, UserInfo.class);
    }

    public void del(Integer id) {
        String key = String.format(KEY_PATTERN, id);
        cache.del(key);
    }
}
