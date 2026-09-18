package com.company.job.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.tool.api.enums.SubscribeEnum;
import com.company.job.feign.SubscribeFeign;
import com.company.tool.api.request.SubscribeSendReq;
import com.company.job.feign.CouponFeign;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

@Component
public class CouponExpireService {

	@Autowired
	private CouponFeign couponFeign;
	@Autowired
	private SubscribeFeign subscribeFeign;

	/**
	 * 优惠券过期提醒（订阅消息job触发demo）
	 * 
	 * @param param
	 * @return
	 */
	@XxlJob("couponExpireHandler")
	public ReturnT<String> couponExpireHandler() {
		String param = XxlJobHelper.getJobParam();
		XxlJobHelper.log("param:{}", param);

		// 查询3天后过期的优惠券
		couponFeign.getUserCouponById(1);
		
		SubscribeSendReq subscribeSendReq = new SubscribeSendReq();
		String openid = "";
		subscribeSendReq.setOpenid(openid);
		String page = "index";
		subscribeSendReq.setPage(page);
		List<String> valueList = Lists.newArrayList("【代金券】100-20", "2019-12-12 18:22:20", "100", "您的优惠券即将过期");
		subscribeSendReq.setValueList(valueList);
		subscribeSendReq.setType(SubscribeEnum.Type.COUPON_EXPIRE);
		LocalDateTime planSendTime = LocalDateTime.now();
		subscribeSendReq.setPlanSendTime(planSendTime);
		subscribeFeign.send(subscribeSendReq);

		return ReturnT.SUCCESS;
	}
}