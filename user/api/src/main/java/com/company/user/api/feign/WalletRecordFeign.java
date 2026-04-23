package com.company.user.api.feign;


import com.company.user.api.constant.Constants;
import com.company.user.api.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.response.WalletRecordResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/walletRecord", fallbackFactory = ThrowExceptionFallback.class)
public interface WalletRecordFeign {

	@GetMapping("/pageMain")
	List<WalletRecordResp> pageMain(@RequestParam("current") Integer current, @RequestParam("size") Integer size);
}
