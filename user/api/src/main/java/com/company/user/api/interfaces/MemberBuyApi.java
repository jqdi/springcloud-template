package com.company.user.api.interfaces;


import com.company.user.api.request.MemberBuyOrderReq;
import com.company.user.api.response.MemberBuyOrderResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberBuyApi {

	/**
	 * 购买
	 * 
	 * @param memberBuyOrderReq
	 * @return
	 */
	@PostMapping("/buy")
	MemberBuyOrderResp buy(@RequestBody MemberBuyOrderReq memberBuyOrderReq);

}