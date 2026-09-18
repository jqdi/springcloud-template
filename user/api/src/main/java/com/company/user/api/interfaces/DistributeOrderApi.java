package com.company.user.api.interfaces;


import com.company.user.api.request.DistributeBuyOrderReq;
import com.company.user.api.response.DistributeBuyOrderResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DistributeOrderApi {

	/**
	 * 购买
	 * 
	 * @param distributeBuyOrderReq
	 * @return
	 */
	@PostMapping("/buy")
	DistributeBuyOrderResp buy(@RequestBody DistributeBuyOrderReq distributeBuyOrderReq);

}