package com.company.web.easylogin.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserInfoFeign;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.request.UserInfoReq;
import com.company.user.api.request.UserOauthReq;
import com.company.user.api.response.UserInfoResp;
import com.company.user.api.response.UserOauthResp;
import io.github.jqdi.easylogin.core.repository.OauthRepository;

@Component
public class MysqlOauthRepository implements OauthRepository {

	@Autowired
	private UserInfoFeign userInfoFeign;
	@Autowired
	private UserOauthFeign userOauthFeign;

	@Override
	public String getUserId(String identityType, String identifier) {
        UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.of(identityType), identifier);
        if (userOauthResp == null) {
            return null;
        }
        Integer userId = userOauthResp.getUserId();
        if (userId == null) {
            return null;
        }
        return String.valueOf(userId);
    }

	@Override
	public void bindOauth(String userId, String identityType, String identifier, String certificate) {
		UserOauthReq userInfoReq = new UserOauthReq();
		userInfoReq.setUserId(Integer.valueOf(userId));
		userInfoReq.setIdentityType(UserOauthEnum.IdentityType.of(identityType));
		userInfoReq.setIdentifier(identifier);
		userInfoReq.setCertificate(certificate);
		userOauthFeign.bindOauth(userInfoReq);
	}

	@Override
	public String registerUser(String identityType, String identifier, String nickname, String avatar) {
		UserInfoReq userInfoReq = new UserInfoReq();
		userInfoReq.setIdentityType(UserOauthEnum.IdentityType.of(identityType));
		userInfoReq.setIdentifier(identifier);
		// userInfoReq.setCertificate(certificate);
		userInfoReq.setNickname(nickname);
		userInfoReq.setAvatar(avatar);

        UserInfoResp userInfoResp = userInfoFeign.findOrCreateUser(userInfoReq);
        if (userInfoResp == null) {
            return null;
        }
        Integer id = userInfoResp.getId();
        if (id == null) {
            return null;
        }
        return String.valueOf(id);
	}
}
