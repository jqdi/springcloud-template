package com.company.system.api.feign;


import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.system.api.constant.Constants;
import com.company.system.api.feign.fallback.ThrowExceptionFallback;
import com.company.system.api.request.SaveNewPasswordReq;
import com.company.system.api.response.SysUserPasswordTipsResp;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/sysUserPassword", fallbackFactory = ThrowExceptionFallback.class)
public interface SysUserPasswordFeign {

	@GetMapping("/getPasswordBySysUserId")
    Map<String, String> getPasswordBySysUserId(@RequestParam("sysUserId") Integer sysUserId);

	@GetMapping("/getPasswordTipsBySysUserId")
	SysUserPasswordTipsResp getPasswordTipsBySysUserId(@RequestParam("sysUserId") Integer sysUserId);

	@PostMapping("/saveNewPassword")
	Void saveNewPassword(@RequestBody SaveNewPasswordReq saveNewPasswordReq);
}
