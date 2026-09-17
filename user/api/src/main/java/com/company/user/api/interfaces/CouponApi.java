package com.company.user.api.interfaces;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.company.user.api.response.UserCouponResp;

public interface CouponApi {

	@RequestMapping("/getUserCouponById")
	UserCouponResp getUserCouponById(@RequestParam("userCouponId") Integer userCouponId);

	@RequestMapping("/isMatchTemplate")
	Boolean isMatchTemplate(@RequestParam("userCouponId") Integer userCouponId,
			@RequestParam("couponTemplateId") Integer couponTemplateId);

}