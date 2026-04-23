package com.company.user.api.feign;

import com.company.user.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.company.user.api.constant.Constants;
import com.company.user.api.response.UserCouponResp;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/coupon", fallbackFactory = ThrowExceptionFallback.class)
public interface CouponFeign {

	@RequestMapping("/getUserCouponById")
	UserCouponResp getUserCouponById(@RequestParam("userCouponId") Integer userCouponId);

	@RequestMapping("/isMatchTemplate")
	Boolean isMatchTemplate(@RequestParam("userCouponId") Integer userCouponId,
			@RequestParam("couponTemplateId") Integer couponTemplateId);

}
