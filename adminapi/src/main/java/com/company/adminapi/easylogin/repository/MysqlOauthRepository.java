package com.company.adminapi.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.system.api.feign.SysUserFeign;
import com.company.system.api.response.SysUserResp;
import io.github.jqdi.easylogin.core.repository.OauthRepository;

@Component
public class MysqlOauthRepository implements OauthRepository {

	@Autowired
	private SysUserFeign sysUserFeign;

	@Override
	public String getUserId(String identityType, String identifier) {
		SysUserResp sysUserResp = sysUserFeign.getByAccount(identifier);
		if (sysUserResp == null || sysUserResp.getId() == null) {
			ExceptionUtil.throwException("账号不存在");
		}

		if (!"ON".equalsIgnoreCase(sysUserResp.getStatus())) {
			ExceptionUtil.throwException("账号已停用");
		}

		return String.valueOf(sysUserResp.getId());
	}

	@Override
	public void bindOauth(String userId, String identityType, String identifier, String certificate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String registerUser(String identityType, String identifier, String nickname, String avatar) {
		throw new UnsupportedOperationException();
	}
}
