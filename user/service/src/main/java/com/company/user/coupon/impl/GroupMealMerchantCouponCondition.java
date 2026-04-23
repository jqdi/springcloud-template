package com.company.user.coupon.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.user.coupon.SeeParam;
import com.company.user.coupon.UseCondition;
import com.company.user.coupon.UseParam;
import com.company.user.coupon.dto.MatchResult;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

/**
 * 外卖团餐-商家券
 */
@Slf4j
@Component("GroupMealMerchantCouponCondition")
public class GroupMealMerchantCouponCondition implements UseCondition {

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

		canSee = "merchantCoupon".equals(couponType);
		if (!canSee) {
			log.info("{}条件不满足,当前不是外卖团餐商家券:{}", seeParam.getUserCouponId(), runtimeAttach);
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
        canUse = "merchantCoupon".equals(couponType);
        if (!canUse) {
            log.info("{}条件不满足,当前不是外卖团餐商家券:{}", useParam.getUserCouponId(), runtimeAttach);
            return MatchResult.builder().canUse(false).reason("仅限外卖团餐商家券可用").build();
        }

        String useConditionValue = useParam.getUseConditionValue();
        JsonNode useConditionJson = JsonUtil.toJsonNode(useConditionValue);
        String conditionShopCodes = JsonUtil.getString(useConditionJson, "shopCodes");
        Set<String> shopCodes = Arrays.stream(conditionShopCodes.split(",")).collect(Collectors.toSet());

        String shopCode = runtimeAttach.get("shopCode");
        if (StringUtils.isBlank(shopCode)) {
            log.info("{}条件不满足,门店{}不是可用门店:{}", useParam.getUserCouponId(), shopCode, useConditionValue);
            return MatchResult.builder().canUse(false).reason("该门店不符合此优惠券使用的条件").build();
        }
        canUse = shopCodes.contains(shopCode);
        if (!canUse) {
            log.info("{}条件不满足,门店{}不是可用门店:{}", useParam.getUserCouponId(), shopCode, useConditionValue);
            return MatchResult.builder().canUse(false).reason("该门店不符合此优惠券使用的条件").build();
        }

        return MatchResult.builder().canUse(true).build();
    }
}
