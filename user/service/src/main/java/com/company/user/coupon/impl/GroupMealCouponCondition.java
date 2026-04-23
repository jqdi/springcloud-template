package com.company.user.coupon.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.user.coupon.SeeParam;
import com.company.user.coupon.UseCondition;
import com.company.user.coupon.UseParam;
import com.company.user.coupon.dto.MatchResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 外卖团餐-优惠券
 */
@Slf4j
@Component("GroupMealCouponCondition")
public class GroupMealCouponCondition implements UseCondition {

	@Override
	public boolean canSee(SeeParam seeParam) {
		Map<String, String> runtimeAttach = seeParam.getRuntimeAttach();
		String business = runtimeAttach.get("business");

		String couponType = runtimeAttach.get("couponType");
		if (StringUtils.isAnyBlank(business, couponType)) {
			// 无值代表不过滤
			return true;
		}

		boolean canSee = "groupmeal".equals(business);
		if (!canSee) {
			log.info("{}条件不满足,当前不是外卖团餐下单:{}", seeParam.getUserCouponId(), runtimeAttach);
			return false;
		}

		canSee = "coupon".equals(couponType);
		if (!canSee) {
			log.info("{}条件不满足,当前不是外卖团餐优惠券:{}", seeParam.getUserCouponId(), runtimeAttach);
			return false;
		}
		return true;
	}

	@Override
	public MatchResult canUse(UseParam useParam) {
		Map<String, String> runtimeAttach = useParam.getRuntimeAttach();
		String business = runtimeAttach.get("business");
		boolean canUse = "groupmeal".equals(business);
		if (!canUse) {
			log.info("{}条件不满足,当前不是外卖团餐下单:{}", useParam.getUserCouponId(), runtimeAttach);
			return MatchResult.builder().canUse(false).reason("仅限外卖团餐可用").build();
		}

		String couponType = runtimeAttach.get("couponType");
		canUse = "coupon".equals(couponType);
		if (!canUse) {
			log.info("{}条件不满足,当前不是外卖团餐优惠券:{}", useParam.getUserCouponId(), runtimeAttach);
			return MatchResult.builder().canUse(false).reason("仅限外卖团餐优惠券可用").build();
		}
		return MatchResult.builder().canUse(true).build();
	}

}
