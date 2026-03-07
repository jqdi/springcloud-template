package com.company.web.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserOauthFeign;
import com.company.web.util.PassWordUtil;
import io.github.jqdi.easylogin.core.repository.PasswordRepository;

@Component
public class MysqlPasswordRepository implements PasswordRepository {
	@Autowired
	private UserOauthFeign userOauthFeign;

	@Override
	public boolean checkPassword(String userId, String password) {
        String passwordDB = userOauthFeign.selectCertificate(Integer.valueOf(userId), UserOauthEnum.IdentityType.PASSWORD).values()
            .iterator().next();
        String md5Password = PassWordUtil.md5(password);
        return md5Password.equals(passwordDB);
	}
}
