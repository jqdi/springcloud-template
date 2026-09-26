package com.company.app.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.company.app.constants.Constants;

import io.github.jqdi.easylogin.core.model.BindAuthCode;
import io.github.jqdi.easylogin.core.repository.OauthTempRepository;

@Component
public class RedisTempOauthRepository implements OauthTempRepository {

    @Autowired
    private CacheManager cacheManager;

	@Override
	public void saveBindAuthCode(String authcode, BindAuthCode bindAuthCode) {
        Cache cache = cacheManager.getCache(Constants.CacheName.AUTH_CODE);
        if (cache == null) {
            return;
        }
        cache.put(authcode, bindAuthCode);
	}

	@Override
	public BindAuthCode getBindAuthCode(String authcode) {
        Cache cache = cacheManager.getCache(Constants.CacheName.AUTH_CODE);
        if (cache == null) {
            return null;
        }
        return cache.get(authcode, BindAuthCode.class);
	}

}
