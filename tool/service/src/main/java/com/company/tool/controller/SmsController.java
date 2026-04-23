package com.company.tool.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.enums.SmsEnum;
import com.company.tool.api.feign.SmsFeign;
import com.company.tool.api.request.SendSmsReq;
import com.company.tool.sms.AsyncSmsSender;

@RestController
@RequestMapping(value = "/sms")
public class SmsController implements SmsFeign {

	@Autowired
	private AsyncSmsSender asyncSmsSender;

	@Override
	public List<Integer> select4PreTimeSend(Integer limit) {
		List<Integer> idList = asyncSmsSender.select4PreTimeSend(limit);
		return idList;
	}
	
	@Override
	public Void exePreTimeSend(Integer id) {
		asyncSmsSender.exePreTimeSend(id);
		return null;
	}

	@Override
	public Void send(@RequestBody SendSmsReq sendSmsReq) {
		String mobile = sendSmsReq.getMobile();
		Map<String, String> templateParamMap = sendSmsReq.getTemplateParamMap();
		LocalDateTime planSendTime = sendSmsReq.getPlanSendTime();
		SmsEnum.Type type = sendSmsReq.getType();
		if (planSendTime == null) {
			planSendTime = LocalDateTime.now();
		}
		LocalDateTime overTime = sendSmsReq.getOverTime();
		if (overTime == null) {
			overTime = planSendTime.plusDays(1);
		}

		asyncSmsSender.send(mobile, templateParamMap, type, planSendTime, overTime);
		return null;
	}
}
