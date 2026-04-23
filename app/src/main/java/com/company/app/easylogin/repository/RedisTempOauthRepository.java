package com.company.app.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.cache.ICache;
import io.github.jqdi.easylogin.core.model.BindAuthCode;
import io.github.jqdi.easylogin.core.repository.OauthTempRepository;

@Component
public class RedisTempOauthRepository implements OauthTempRepository {

	@Autowired
	private ICache cache;

	@Override
	public void saveBindAuthCode(String authcode, BindAuthCode bindAuthCode) {
		cache.set(authcode, bindAuthCode);
	}

	@Override
	public BindAuthCode getBindAuthCode(String authcode) {
		return cache.get(authcode, BindAuthCode.class);
	}

}
