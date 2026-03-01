package com.company.web.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.company.framework.context.HeaderContextUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.util.RegexUtil;
import com.company.token.TokenService;
import com.company.token.accesscontrol.annotation.RequireLogin;
import com.company.tool.api.feign.VerifyCodeFeign;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserInfoFeign;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.request.UserInfoReq;
import com.company.user.api.response.UserInfoResp;
import com.company.user.api.response.UserOauthResp;
import com.company.web.constants.Constants;
import com.company.web.req.LoginByEmailReq;
import com.company.web.req.LoginByMobileReq;
import com.company.web.req.RegByEmailReq;
import com.company.web.req.RegByMobileReq;
import com.company.web.resp.LoginResp;
import com.company.web.util.PassWordUtil;
import com.google.common.collect.Maps;

import io.github.jqdi.easylogin.core.LoginClient;
import io.github.jqdi.easylogin.core.LoginParams;
import io.github.jqdi.easylogin.core.exception.LoginException;
import io.github.jqdi.easylogin.spring.boot.starter.LoginType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private UserInfoFeign userInfoFeign;
	@Autowired
	private UserOauthFeign userOauthFeign;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private MessageSender messageSender;
	@Autowired
	private VerifyCodeFeign verifyCodeFeign;

	@Autowired
	@Qualifier(LoginType.EMAIL_PASSWORD_CODE)
	private LoginClient emailPasswordCodeLoginClient;
	@Autowired
	@Qualifier(LoginType.MOBILE_PASSWORD_CODE)
	private LoginClient mobilePasswordCodeLoginClient;

	@Value("${token.name}")
	private String headerToken;

	@Value("${token.prefix:}")
	private String tokenPrefix;

	@GetMapping(value = "/reg/verify/email")
	public Map<String, String> regVerifyByEmail(@NotBlank(message = "邮箱不能为空") String email) {
		if (!RegexUtil.checkEmail(email)) {
            ExceptionUtil.throwException("邮箱格式错误");
		}

		String identifier = email;
		UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.EMAIL, identifier);
		if (userOauthResp != null && userOauthResp.getUserId() != null) {
			ExceptionUtil.throwException("邮箱已注册，可以直接登录！");
		}

		return verifyCodeFeign.email(email, Constants.VerifyCodeType.REGISTER);
	}

	@PostMapping(value = "/reg/email")
	public Map<String, String> regByEmail(@Valid @RequestBody RegByEmailReq regByEmailReq) {
		String email = regByEmailReq.getEmail();
		if (!RegexUtil.checkEmail(email)) {
			ExceptionUtil.throwException("邮箱格式错误");
		}

		String identifier = email;
		UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.EMAIL, identifier);
		if (userOauthResp != null && userOauthResp.getUserId() != null) {
			ExceptionUtil.throwException("邮箱已注册，可以直接登录！");
		}

		Boolean verifyPass = verifyCodeFeign.verify(Constants.VerifyCodeType.REGISTER, email, regByEmailReq.getCode());
		if (!verifyPass) {
			ExceptionUtil.throwException("验证码错误");
		}

		// TODO 密码复杂度校验

		UserInfoReq userInfoReq = new UserInfoReq();
		userInfoReq.setIdentityType(UserOauthEnum.IdentityType.EMAIL);
		userInfoReq.setIdentifier(email);
		userInfoReq.setCertificate(regByEmailReq.getCode());
		userInfoReq.setPassword(PassWordUtil.md5(regByEmailReq.getPassword()));
//		userInfoReq.setAvatar(null);
//		userInfoReq.setNickname(null);
		UserInfoResp userInfoResp = userInfoFeign.findOrCreateUser(userInfoReq);
		log.info("userId:{}", userInfoResp.getId());

		return Collections.singletonMap("value", "注册成功");
	}

	@GetMapping(value = "/reg/verify/mobile")
	public Map<String, String> regVerifyByMobile(@NotBlank(message = "手机号不能为空") String mobile) {
		if (!RegexUtil.checkMobile(mobile)) {
			ExceptionUtil.throwException("手机号格式错误");
		}

		String identifier = mobile;
        UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.MOBILE, identifier);
		if (userOauthResp != null && userOauthResp.getUserId() != null) {
			ExceptionUtil.throwException("手机号已注册，可以直接登录！");
		}

		return verifyCodeFeign.sms(mobile, Constants.VerifyCodeType.REGISTER);
	}

	@PostMapping(value = "/reg/mobile")
	public Map<String, String> regByMobile(@Valid @RequestBody RegByMobileReq regByMobileReq) {
		String mobile = regByMobileReq.getMobile();
		if (!RegexUtil.checkMobile(mobile)) {
			ExceptionUtil.throwException("手机号格式错误");
		}

		String identifier = mobile;
		UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.MOBILE, identifier)
				;
		if (userOauthResp != null && userOauthResp.getUserId() != null) {
			ExceptionUtil.throwException("手机号已注册，可以直接登录！");
		}

		Boolean verifyPass = verifyCodeFeign.verify(Constants.VerifyCodeType.REGISTER, mobile, regByMobileReq.getCode());
		if (!verifyPass) {
			ExceptionUtil.throwException("验证码错误");
		}

		// TODO 密码复杂度校验

		UserInfoReq userInfoReq = new UserInfoReq();
		userInfoReq.setIdentityType(UserOauthEnum.IdentityType.MOBILE);
		userInfoReq.setIdentifier(identifier);
		userInfoReq.setCertificate(regByMobileReq.getCode());
		userInfoReq.setPassword(PassWordUtil.md5(regByMobileReq.getPassword()));
//		userInfoReq.setAvatar(null);
//		userInfoReq.setNickname(null);
		UserInfoResp userInfoResp = userInfoFeign.findOrCreateUser(userInfoReq);
		log.info("userId:{}", userInfoResp.getId());

		return Collections.singletonMap("value", "注册成功");
	}

	@GetMapping(value = "/login/verify/email")
	public Map<String, String> loginVerifyByEmail(@NotBlank(message = "邮箱不能为空") String email) {
		if (!RegexUtil.checkEmail(email)) {
			ExceptionUtil.throwException("邮箱格式错误");
		}

		String identifier = email;
		UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.EMAIL, identifier);
		if (userOauthResp == null) {
			ExceptionUtil.throwException("邮箱未注册，请前往注册！");
		}

		return verifyCodeFeign.email(email, Constants.VerifyCodeType.LOGIN);
	}

	@PostMapping(value = "/login/email")
	public LoginResp loginByEmail(@Valid @RequestBody LoginByEmailReq loginByEmailReq) {
		String email = loginByEmailReq.getEmail();
		String password = loginByEmailReq.getPassword();
		String code = loginByEmailReq.getCode();

		String identifier = email;
		String userId = null;
		try {
			userId = emailPasswordCodeLoginClient.login(LoginParams.builder().emailPasswordCode(identifier, password, code).build());
		} catch (LoginException e) {
			ExceptionUtil.throwException(e.getMessage());
		}

		LoginResp loginResp = new LoginResp();
		loginResp.setNeedBind(false);
		loginResp.setToken(token(userId));

		return loginResp;
	}

	@GetMapping(value = "/login/verify/mobile")
	public Map<String, String> loginVerifyByMobile(@NotBlank(message = "手机号不能为空") String mobile) {
		if (!RegexUtil.checkMobile(mobile)) {
			ExceptionUtil.throwException("手机号格式错误");
		}

		String identifier = mobile;
		UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.MOBILE, identifier)
				;
		if (userOauthResp == null) {
			ExceptionUtil.throwException("手机号未注册，请前往注册！");
		}

		return verifyCodeFeign.sms(mobile, Constants.VerifyCodeType.LOGIN);
	}

	@PostMapping(value = "/login/mobile")
	public LoginResp loginByMobile(@Valid @RequestBody LoginByMobileReq loginByMobileReq) {
		String mobile = loginByMobileReq.getMobile();
		String password = loginByMobileReq.getPassword();
		String code = loginByMobileReq.getCode();

		String identifier = mobile;
		String userId = null;
		try {
			userId = mobilePasswordCodeLoginClient.login(LoginParams.builder().mobilePasswordCode(identifier, password, code).build());
		} catch (LoginException e) {
			ExceptionUtil.throwException(e.getMessage());
		}

		LoginResp loginResp = new LoginResp();
		loginResp.setNeedBind(false);
		loginResp.setToken(token(userId));

		return loginResp;
	}

	private String token(String userId) {
		String device = "WEB"; // 此次登录的客户端设备类型, 用于[同端互斥登录]时指定此次登录的设备类型

		String tokenValue = tokenService.generate(userId, device);
		if (StringUtils.isNoneBlank(tokenPrefix)) {
			tokenValue = tokenPrefix + " " + tokenValue;
		}

		// 发布登录事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("device", device);
		params.put("userId", userId);
		params.put("httpContextHeader", HeaderContextUtil.httpContextHeader());
		messageSender.sendBroadcastMessage(params, BroadcastConstants.USER_LOGIN.EXCHANGE);

		return tokenValue;
	}

	@RequireLogin
	@PostMapping(value = "/logout")
	public Map<String, String> logout(HttpServletRequest request) {
		String token = request.getHeader(headerToken);
		if (StringUtils.isBlank(token)) {
			return Collections.singletonMap("value", "登出成功");
		}

		String device = tokenService.invalid(token);

		// 发布登出事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("device", device);
		params.put("userId", HeaderContextUtil.currentUserId());
		params.put("httpContextHeader", HeaderContextUtil.httpContextHeader());
		messageSender.sendBroadcastMessage(params, BroadcastConstants.USER_LOGOUT.EXCHANGE);

		return Collections.singletonMap("value", "登出成功");
	}
}
