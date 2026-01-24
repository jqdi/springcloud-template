package com.company.app.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.company.app.constants.Constants;
import com.company.app.req.LoginByMobileReq;
import com.company.app.req.LoginByWeixinAppReq;
import com.company.app.resp.LoginResp;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.messagedriven.MessageSender;
import com.company.framework.messagedriven.constants.BroadcastConstants;
import com.company.framework.util.RegexUtil;
import com.company.token.TokenService;
import com.company.token.accesscontrol.annotation.RequireLogin;
import com.company.tool.api.feign.VerifyCodeFeign;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.response.UserOauthResp;
import com.google.common.collect.Maps;
import com.jqdi.easylogin.core.LoginClient;
import com.jqdi.easylogin.core.LoginParams;
import com.jqdi.easylogin.core.exception.LoginException;
import com.jqdi.easylogin.spring.boot.starter.LoginType;

@Validated
@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private UserOauthFeign userOauthFeign;

	@Autowired
	private TokenService tokenService;
	@Autowired
	private MessageSender messageSender;
	@Autowired
	private VerifyCodeFeign verifyCodeFeign;

	@Autowired
	@Qualifier(LoginType.WEIXIN_APP)
	private LoginClient weixinAppLoginClient;

	@Autowired
	@Qualifier(LoginType.MOBILE_CODE_BIND)
	private LoginClient mobileCodeBindLoginClient;

	@Value("${token.name}")
	private String headerToken;

	@Value("${token.prefix:}")
	private String tokenPrefix;

	@PostMapping(value = "/login/weixinapp")
	public LoginResp loginByWeixinapp(@Valid @RequestBody LoginByWeixinAppReq loginByWeixinAppReq) {
		String wxcode = loginByWeixinAppReq.getWxcode();

		String userId = null;
		try {
			userId = weixinAppLoginClient.login(LoginParams.builder().weixinApp(wxcode).build());
		} catch (LoginException e) {
			ExceptionUtil.throwException(e.getMessage());
		}

		LoginResp loginResp = new LoginResp();
		if (userId == null) {
			// 用户ID为null代表未登录成功，一般是指通过authcode没有找到对应的用户，需要通过XxxCodeBind来注册用户账号
			loginResp.setNeedBind(true);
			loginResp.setBindCode(wxcode);
			return loginResp;
		}

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
        UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.MOBILE, identifier);
		if (userOauthResp == null || userOauthResp.getUserId() == null) {
			ExceptionUtil.throwException("手机号未注册，请前往注册！");
		}

        return verifyCodeFeign.sms(mobile, Constants.VerifyCodeType.LOGIN);
	}

	@PostMapping(value = "/login/mobile")
	public LoginResp loginByMobile(@Valid @RequestBody LoginByMobileReq loginByMobileReq) {
		String mobile = loginByMobileReq.getMobile();
		String code = loginByMobileReq.getCode();
		String bindCode = loginByMobileReq.getBindCode();

		String identifier = mobile;
		String userId = null;
		try {
			userId = mobileCodeBindLoginClient.login(LoginParams.builder().mobileCodeBind(identifier, code, bindCode).build());
		} catch (LoginException e) {
			ExceptionUtil.throwException(e.getMessage());
		}

		LoginResp loginResp = new LoginResp();
		loginResp.setNeedBind(false);
		loginResp.setToken(token(userId));

		return loginResp;
	}

	private String token(String userId) {
		String device = "APP"; // 此次登录的客户端设备类型, 用于[同端互斥登录]时指定此次登录的设备类型

		String tokenValue = tokenService.generate(userId, device);
		if (StringUtils.isNoneBlank(tokenPrefix)) {
			tokenValue = tokenPrefix + " " + tokenValue;
		}

		// 发布登录事件
		Map<String, Object> params = Maps.newHashMap();
		params.put("device", device);
		params.put("userId", userId);
		params.put("httpContextHeader", HeaderContextUtil.httpContextHeader());
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		messageSender.sendBroadcastMessage(params, BroadcastConstants.USER_LOGOUT.EXCHANGE);

        return Collections.singletonMap("value", "登出成功");
	}
}
