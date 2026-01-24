package com.company.tool.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.cache.ICache;
import com.company.framework.util.JsonUtil;
import com.company.tool.entity.AppInfo;
import com.company.tool.mapper.AppInfoMapper;

@Component
public class AppInfoCache {
    private static final String KEY_PATTERN = "tool:appinfo:%s";

    @Autowired
    private ICache cache;
    @Autowired
    private AppInfoMapper appInfoMapper;

    public AppInfo getById(Integer id) {
        String key = String.format(KEY_PATTERN, id);
        return cache.get(key, () -> {
            AppInfo appInfo = appInfoMapper.selectById(id);
            if (appInfo == null) {
                appInfo = new AppInfo();
            }
            return JsonUtil.toJsonString(appInfo);
        }, AppInfo.class);
    }

    public void del(Integer id) {
        String key = String.format(KEY_PATTERN, id);
        cache.del(key);
    }
}
