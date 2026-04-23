package com.company.tool.messagedriven.strategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.messagedriven.BaseStrategy;
import com.company.framework.util.JsonUtil;
import com.company.tool.api.enums.SubscribeEnum;
import com.company.tool.entity.SubscribeTemplateGrant;
import com.company.tool.service.SubscribeTemplateGrantService;
import com.company.tool.subscribe.AsyncSubscribeSender;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.CouponFeign;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.response.UserCouponResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(StrategyConstants.COUPON_TOUSE_STRATEGY)
public class CouponTouseStrategy implements BaseStrategy<Map<String, Object>> {

	@Autowired
	private AsyncSubscribeSender asyncSubscribeSender;
	@Autowired
	private UserOauthFeign userOauthFeign;
	@Autowired
	private SubscribeTemplateGrantService subscribeTemplateGrantService;
	@Autowired
	private CouponFeign couponFeign;

	@Override
	public void doStrategy(Map<String, Object> params) {
		Integer userCouponId = MapUtils.getInteger(params, "userCouponId");

		// 读取授权表，获取业务参数
		SubscribeTemplateGrant subscribeTemplateGrant = subscribeTemplateGrantService.getById(1);
		String remark = subscribeTemplateGrant.getRemark();

		@SuppressWarnings("unchecked")
		Map<String, String> paramMap = JsonUtil.toEntity(remark, Map.class);

		Integer _userCouponId = MapUtils.getInteger(paramMap, "userCouponId");
		if (_userCouponId != null) {// 使用用户的优惠券判断
			if (!_userCouponId.equals(userCouponId)) {
				log.info("条件不满足{} {}", userCouponId, _userCouponId);
				return;
			}
		} else {// 使用优惠券模板判断
			Integer couponTemplateId = MapUtils.getInteger(paramMap, "couponTemplateId");
			Boolean isMatchTemplate = couponFeign.isMatchTemplate(userCouponId, couponTemplateId);
			if (!isMatchTemplate) {
				log.info("条件不满足{} {}", userCouponId, couponTemplateId);
				return;
			}
		}

		UserCouponResp userCouponResp = couponFeign.getUserCouponById(userCouponId);

		Integer userId = MapUtils.getInteger(params, "userId");
		String openid = userOauthFeign.selectIdentifier(userId, UserOauthEnum.IdentityType.WX_OPENID_MINIAPP).values().iterator().next();

		// 优惠券过期前3天
		LocalDateTime planSendTime = userCouponResp.getEndTime().minusDays(3);
		String page = "";
		List<String> valueList = null;
		SubscribeEnum.Type type = SubscribeEnum.Type.COUPON_USE;
		LocalDateTime overTime = planSendTime.plusHours(1);
		// 创建订阅消息任务
		asyncSubscribeSender.send(openid, page, valueList, type, planSendTime, overTime);
	}
}
