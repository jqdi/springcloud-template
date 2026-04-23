package com.company.adminapi.cache;

import com.company.framework.cache.ICache;
import com.company.framework.util.JsonUtil;
import com.company.user.api.feign.UserInfoFeign;
import com.company.user.api.response.UserInfoResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UserInfoCache {

	@Autowired
	private ICache cache;
	@Autowired
	private UserInfoFeign userInfoFeign;

	public UserInfoResp getById(Integer id) {
		String key = String.format("admin:userinfo:%s", id);
		return cache.get(key, () -> {
			UserInfoResp userInfoResp = userInfoFeign.getById(id);
			if (userInfoResp == null) {
				userInfoResp = new UserInfoResp();
			}
			return JsonUtil.toJsonString(userInfoResp);
		}, UserInfoResp.class);
	}

	@Cacheable(value = "adminapi:userinfo", key = "#id")
	public UserInfoResp getById2(Integer id) {
		return userInfoFeign.getById(id);
	}
}
