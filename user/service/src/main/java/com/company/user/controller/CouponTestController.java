package com.company.user.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.user.coupon.UseCouponService;
import com.company.user.coupon.dto.UserCouponCanUse;
import com.company.user.coupon.dto.UserCouponCanUseBatch;
import com.company.user.coupon.dto.UserCouponCanUseParam;
import com.company.user.coupon.dto.UserCouponCanUsePay;
import com.company.user.coupon.dto.UserCouponMe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/coupontest")
public class CouponTestController {

	@Autowired
	private UseCouponService useCouponService;

	/**
	 * <pre>
	 * 用户优惠券列表（未使用、即将过期、已使用、已过期）
	 * 应用场景：用户查看自己的优惠券列表
	 * </pre>
	 */
	@RequestMapping("/listCouponByAppUserId")
	public List<UserCouponMe> listCouponByAppUserId(Integer userId) {
		String status = "nouse";

		Map<String, String> seeRuntimeAttach = Maps.newHashMap();
		seeRuntimeAttach.put("platform", "app");

		List<UserCouponMe> userCouponMeList = useCouponService.listCouponByAppUserId(userId, status, seeRuntimeAttach);

		return userCouponMeList;
	}

	/**
	 * <pre>
	 * 用户优惠券列表
	 * 应用场景：支付时展示选择优惠券的列表
	 * </pre>
	 */
	@RequestMapping("/listCouponCanUseByAppUserId")
	public List<UserCouponCanUsePay> listCouponCanUseByAppUserId(Integer userId) {
		Map<String, String> runtimeAttach = Maps.newHashMap();
		runtimeAttach.put("productCode", "AB3301");
		runtimeAttach.put("business", "groupmeal");
		runtimeAttach.put("couponType", "coupon");
//		runtimeAttach.put("platform", "app");

		BigDecimal orderAmount = new BigDecimal("10");
		List<UserCouponCanUsePay> UserCouponCanUsePayList = useCouponService.listCouponCanUseByAppUserId(userId, orderAmount,
				runtimeAttach);

		return UserCouponCanUsePayList;
	}
	
	/**
	 * <pre>
	 * 用户最优的可用优惠券
	 * 应用场景：支付时自动为用户选择优惠券
	 * </pre>
	 */
	@RequestMapping("/bestCouponCanUse")
	public UserCouponCanUse bestCouponCanUse(Integer userId) {
		Map<String, String> runtimeAttach = Maps.newHashMap();
		runtimeAttach.put("productCode", "AB3301");

		BigDecimal orderAmount = new BigDecimal("20");

		UserCouponCanUse bestCouponCanUse = useCouponService.bestCouponCanUse(userId, orderAmount, runtimeAttach);

		return bestCouponCanUse;
	}
	
	/**
	 * <pre>
	 * 用户最优的可用优惠券(批量)
	 * 应用场景：商品展示券后价
	 * </pre>
	 */
	@RequestMapping("/bestCouponCanUseBatch")
	public List<UserCouponCanUseBatch> bestCouponCanUseBatch(Integer userId) {
		Map<String, String> seeRuntimeAttach = Maps.newHashMap();
		seeRuntimeAttach.put("business", "groupmeal");
		seeRuntimeAttach.put("couponType", "coupon");
		
		List<UserCouponCanUseParam> userCouponCanUseParamList = Lists.newArrayList();
		for (int i = 0; i < 50; i++) {
			UserCouponCanUseParam userCouponCanUseParam = new UserCouponCanUseParam();
			userCouponCanUseParam.setUniqueCode("AB3301-" + String.format("%02d", i));// 用productCode做唯一
			userCouponCanUseParam.setOrderAmount(new BigDecimal(10 + i));
			
			Map<String, String> runtimeAttach = Maps.newHashMap();
			runtimeAttach.put("business", "groupmeal");
//			runtimeAttach.put("business", "youxuan");
			runtimeAttach.put("couponType", "coupon");
			runtimeAttach.put("productCode", "AB3301");
			userCouponCanUseParam.setRuntimeAttach(runtimeAttach);
			userCouponCanUseParamList.add(userCouponCanUseParam);
		}
		
		List<UserCouponCanUseBatch> userCouponCanUseBatchList = useCouponService.bestCouponCanUseBatch(userId, seeRuntimeAttach, userCouponCanUseParamList );

		return userCouponCanUseBatchList;
	}

	/**
	 * <pre>
	 * 判断优惠券能否使用
	 * 应用场景：创建订单时校验优惠券
	 * </pre>
	 */
	@RequestMapping("/canUse")
	public UserCouponCanUse canUse(Integer userId, Integer userCouponId) {
		Map<String, String> runtimeAttach = Maps.newHashMap();
		runtimeAttach.put("productCode", "AB3301");
		runtimeAttach.put("business", "groupmeal");
		runtimeAttach.put("couponType", "coupon");
		
		BigDecimal orderAmount = new BigDecimal("20");
		UserCouponCanUse canUse = useCouponService.canUse(userCouponId, userId, orderAmount, runtimeAttach);

		return canUse;
	}
}
