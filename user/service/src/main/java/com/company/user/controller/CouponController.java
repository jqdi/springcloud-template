package com.company.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.PropertyUtils;
import com.company.user.api.feign.CouponFeign;
import com.company.user.api.response.UserCouponResp;
import com.company.user.entity.UserCoupon;
import com.company.user.service.market.UserCouponService;

@RestController
@RequestMapping("/coupon")
public class CouponController implements CouponFeign {

	@Autowired
	private UserCouponService userCouponService;

	@Override
	public UserCouponResp getUserCouponById(Integer userCouponId) {
		UserCoupon userCoupon = userCouponService.getById(userCouponId);
		return PropertyUtils.copyProperties(userCoupon, UserCouponResp.class);
	}

	@Override
	public Boolean isMatchTemplate(Integer userCouponId, Integer couponTemplateId) {
		UserCoupon userCoupon = userCouponService.getById(userCouponId);
		if (userCoupon == null) {
			return false;
		}
		return userCoupon.getCouponTemplateId().equals(couponTemplateId);
	}

//	@Override
	public UserCouponResp select4Expire(Integer days) {
		// days天后即将过期的优惠券
//		LocalDateTime time = null;
//		userCouponService.selectWillExpire(time);
		UserCoupon userCoupon = userCouponService.getById(1);
		return PropertyUtils.copyProperties(userCoupon, UserCouponResp.class);
	}
}
