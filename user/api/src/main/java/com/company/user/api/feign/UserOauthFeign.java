package com.company.user.api.feign;


import com.company.user.api.constant.Constants;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.request.UserOauthReq;
import com.company.user.api.response.UserOauthResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/useroauth", fallbackFactory = ThrowExceptionFallback.class)
public interface UserOauthFeign {

	@RequestMapping("/selectOauth")
	UserOauthResp selectOauth(@RequestParam("identityType") UserOauthEnum.IdentityType identityType,
			@RequestParam("identifier") String identifier);

	@RequestMapping("/selectIdentifier")
    Map<String, String> selectIdentifier(@RequestParam("userId") Integer userId,
                                         @RequestParam("identityType") UserOauthEnum.IdentityType identityType);

	@RequestMapping("/selectCertificate")
    Map<String, String> selectCertificate(@RequestParam("userId") Integer userId,
			@RequestParam("identityType") UserOauthEnum.IdentityType identityType);
	
	@RequestMapping("/bindOauth")
	Boolean bindOauth(@RequestBody UserOauthReq userInfoReq);

}
