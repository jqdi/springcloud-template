package com.company.user.api.feign;


import com.company.user.api.constant.Constants;
import com.company.user.api.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.request.DistributeBuyOrderReq;
import com.company.user.api.response.DistributeBuyOrderResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/distributeOrder", fallbackFactory = ThrowExceptionFallback.class)
public interface DistributeOrderFeign {

	/**
	 * 购买
	 * 
	 * @param distributeBuyOrderReq
	 * @return
	 */
	@PostMapping("/buy")
	DistributeBuyOrderResp buy(@RequestBody DistributeBuyOrderReq distributeBuyOrderReq);

}
