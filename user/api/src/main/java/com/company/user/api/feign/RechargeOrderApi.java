package com.company.user.api.feign;


import com.company.user.api.request.RechargeOrderReq;
import com.company.user.api.response.RechargeOrderResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface RechargeOrderApi {

	/**
	 * 购买
	 * 
	 * @param rechargeOrderReq
	 * @return
	 */
	@PostMapping("/buy")
	RechargeOrderResp buy(@RequestBody RechargeOrderReq rechargeOrderReq);

}