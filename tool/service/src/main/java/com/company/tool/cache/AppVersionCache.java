package com.company.tool.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.cache.ICache;
import com.company.framework.util.JsonUtil;
import com.company.tool.entity.AppVersion;
import com.company.tool.service.AppVersionService;

@Component
public class AppVersionCache {
    private static final String KEY_PATTERN = "tool:appversion:%s";

    @Autowired
    private ICache cache;
    @Autowired
    private AppVersionService appVersionService;

    public AppVersion selectLastByAppCode(String appCode) {
        String key = String.format(KEY_PATTERN, appCode);
        return cache.get(key, () -> {
            AppVersion appVersion = appVersionService.selectLastByAppCode(appCode);
            if (appVersion == null) {
                appVersion = new AppVersion();
            }
            return JsonUtil.toJsonString(appVersion);
        }, AppVersion.class);
    }

    public void del(String appCode) {
        String key = String.format(KEY_PATTERN, appCode);
        cache.del(key);
    }
}
