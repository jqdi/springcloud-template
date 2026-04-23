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
 * 优选套餐货架商品
 */
@Slf4j
@Component("YouxuanCondition")
public class YouxuanCondition implements UseCondition {

	@Override
	public boolean canSee(SeeParam seeParam) {
		Map<String, String> runtimeAttach = seeParam.getRuntimeAttach();
		String business = runtimeAttach.get("business");
		if (StringUtils.isAnyBlank(business)) {
			// 无值代表不过滤
			return true;
		}

		boolean canSee = "youxuan".equals(business);
		if (!canSee) {
			log.info("{}条件不满足,当前不是优选套餐货架下单:{}", seeParam.getUserCouponId(), runtimeAttach);
			return false;
		}
		return true;
	}

	@Override
	public MatchResult canUse(UseParam useParam) {
		Map<String, String> runtimeAttach = useParam.getRuntimeAttach();

		String business = runtimeAttach.get("business");
		boolean canUse = "youxuan".equals(business);
		if (!canUse) {
			log.info("{}条件不满足,当前不是优选套餐货架下单:{}", useParam.getUserCouponId(), runtimeAttach);
			return MatchResult.builder().canUse(false).reason("该商品不符合此优惠券使用的条件").build();
		}

		String productCode = runtimeAttach.get("productCode");
		if (StringUtils.isBlank(productCode)) {
			log.info("{}条件不满足,优选套餐货架不包含商品‘{}’", useParam.getUserCouponId(), productCode);
			return MatchResult.builder().canUse(false).reason("该商品不符合此优惠券使用的条件").build();
		}

		return MatchResult.builder().canUse(true).build();
	}
}
