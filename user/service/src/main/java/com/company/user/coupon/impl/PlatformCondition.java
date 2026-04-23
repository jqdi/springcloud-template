package com.company.user.coupon.impl;

import java.util.Arrays;
import java.util.List;
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
 * 仅{platform}平台可见可用
 */
@Slf4j
@Component("PlatformCondition")
public class PlatformCondition implements UseCondition {

	@Override
	public boolean canSee(SeeParam seeParam) {
		Map<String, String> runtimeAttach = seeParam.getRuntimeAttach();
		String platform = runtimeAttach.get("platform");

		String useConditionJson = seeParam.getUseConditionValue();

		@SuppressWarnings("unchecked")
		Map<String, String> platformMap = JsonUtil.toEntity(useConditionJson, Map.class);
		String platforms = platformMap.get("platforms");
		List<String> platformList = Arrays.asList(StringUtils.split(platforms, ","));

		boolean canSee = platformList.contains(platform);
		if (!canSee) {
			log.info("{}条件不满足,配置平台{},实际平台:{}", seeParam.getUserCouponId(), platformList,
					platform);
			return false;
		}
		return canSee;
	}

	@Override
	public MatchResult canUse(UseParam useParam) {
		Map<String, String> runtimeAttach = useParam.getRuntimeAttach();
		String platform = runtimeAttach.get("platform");

		String useConditionJson = useParam.getUseConditionValue();

		@SuppressWarnings("unchecked")
		Map<String, String> platformMap = JsonUtil.toEntity(useConditionJson, Map.class);
		String platforms = platformMap.get("platforms");
		List<String> platformList = Arrays.asList(StringUtils.split(platforms, ","));

		boolean canUse = platformList.contains(platform);
		if (!canUse) {
			log.info("{}条件不满足,配置平台{},实际平台:{}", useParam.getUserCouponId(), platformList,
					platform);
			return MatchResult.builder().canUse(false).reason("该优惠券只允许在APP上使用").build();
		}
		return MatchResult.builder().canUse(true).build();
	}
}
