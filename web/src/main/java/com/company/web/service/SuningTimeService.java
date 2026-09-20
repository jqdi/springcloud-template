package com.company.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.company.web.constants.Constants;

import cn.hutool.http.HttpUtil;

@Service
public class SuningTimeService implements TimeService {

    @Autowired
    private CacheManager cacheManager;

	@Override
	public String getTime() {
		return HttpUtil.get("http://quan.suning.com/getSysTime.do");
	}

    @Override
    public String getCacheTime() {
        Cache cache = cacheManager.getCache(Constants.CacheName.TIME);
        if (cache == null) {
            return HttpUtil.get("http://quan.suning.com/getSysTime.do");
        }
        return cache.get("t", () -> HttpUtil.get("http://quan.suning.com/getSysTime.do"));
    }
}
