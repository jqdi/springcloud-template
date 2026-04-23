package com.company.user.api.feign;


import com.company.user.api.constant.Constants;
import com.company.user.api.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.request.MemberBuyOrderReq;
import com.company.user.api.response.MemberBuyOrderResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/memberBuy", fallbackFactory = ThrowExceptionFallback.class)
public interface MemberBuyFeign {

	/**
	 * 购买
	 * 
	 * @param memberBuyOrderReq
	 * @return
	 */
	@PostMapping("/buy")
	MemberBuyOrderResp buy(@RequestBody MemberBuyOrderReq memberBuyOrderReq);

}
