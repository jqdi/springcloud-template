package com.company.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.PropertyUtils;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.request.UserOauthReq;
import com.company.user.api.response.UserOauthResp;
import com.company.user.entity.UserOauth;
import com.company.user.mapper.user.UserOauthMapper;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/useroauth")
public class UserOauthController implements UserOauthFeign {

	@Autowired
	private UserOauthMapper userOauthMapper;

	@Override
	public UserOauthResp selectOauth(UserOauthEnum.IdentityType identityType, String identifier) {

		UserOauth userOauth = userOauthMapper.selectByIdentityTypeIdentifier(identityType, identifier);
		return PropertyUtils.copyProperties(userOauth, UserOauthResp.class);
	}

	@Override
	public Map<String, String> selectIdentifier(Integer userId, UserOauthEnum.IdentityType identityType) {
		UserOauth userOauth = userOauthMapper.selectByUserIdIdentityType(userId, identityType);
        return Collections.singletonMap("value", userOauth.getIdentifier());
	}

	@Override
	public Map<String, String> selectCertificate(Integer userId, UserOauthEnum.IdentityType identityType) {
		UserOauth userOauth = userOauthMapper.selectByUserIdIdentityType(userId, identityType);
        return Collections.singletonMap("value", userOauth.getCertificate());
	}

	@Override
	public Boolean bindOauth(@RequestBody @Valid UserOauthReq userInfoReq) {
//		userOauthMapper.bindOauth(userInfoReq.getUserId(), userInfoReq.getIdentityType(), userInfoReq.getIdentifier(),
//				userInfoReq.getCertificate());
		UserOauthEnum.IdentityType identityType = userInfoReq.getIdentityType();
		UserOauth userOauth = userOauthMapper.selectByIdentityTypeIdentifier(identityType, userInfoReq.getIdentifier());
        if (userOauth != null) {
            return true;
        }
        userOauth = new UserOauth().setUserId(userInfoReq.getUserId()).setIdentityType(userInfoReq.getIdentityType().getCode())
            .setIdentifier(userInfoReq.getIdentifier()).setCertificate(userInfoReq.getCertificate());
        userOauthMapper.insert(userOauth);
		return true;
	}
}
