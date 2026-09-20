package com.company.adminapi.cache;

import com.company.adminapi.constants.Constants;
import com.company.adminapi.feign.UserInfoFeign;
import com.company.user.api.response.UserInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UserInfoCache {

	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private UserInfoFeign userInfoFeign;

	public UserInfoResp getById(Integer id) {
		Cache cache = cacheManager.getCache(Constants.CacheName.USER_INFO);
		if (cache == null) {
			return userInfoFeign.getById(id);
		}
		return cache.get(id, () -> userInfoFeign.getById(id));
	}

    /**
     * 等价于getById
     */
    @Cacheable(value = Constants.CacheName.USER_INFO, key = "#id")
    public UserInfoResp getById2(Integer id) {
        return userInfoFeign.getById(id);
    }
}