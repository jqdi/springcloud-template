package com.company.tool.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.enums.EmailEnum;
import com.company.tool.api.enums.SmsEnum;
import com.company.tool.api.enums.WebhookEnum;
import com.company.tool.api.feign.AlarmFeign;
import com.company.tool.api.request.AlarmReq;
import com.company.tool.api.request.WebhookAlarmReq;
import com.company.tool.email.AsyncEmailSender;
import com.company.tool.email.dto.EmailTemplateParam;
import com.company.tool.sms.AsyncSmsSender;
import com.company.tool.sms.dto.MobileTemplateParam;
import com.company.tool.webhook.AsyncWebhookSender;
import com.google.common.collect.Lists;

@RestController
@RequestMapping(value = "/alarm")
public class AlarmController implements AlarmFeign {

	@Autowired
	private AsyncWebhookSender asyncWebhookSender;
	@Autowired
	private AsyncSmsSender asyncSmsSender;
	@Autowired
	private AsyncEmailSender asyncEmailSender;

	@Override
	public Void webhook(@RequestBody WebhookAlarmReq webhookAlarmReq) {
		WebhookEnum.Type type = webhookAlarmReq.getType();
		Map<String, String> templateParamMap = webhookAlarmReq.getTemplateParamMap();
		asyncWebhookSender.send(type, templateParamMap);
		return null;
	}

	@Override
	public Void email(@RequestBody AlarmReq alarmReq) {
		// TODO
		List<EmailTemplateParam> emailTemplateParamList = Lists.newArrayList();
		EmailEnum.Type type = EmailEnum.Type.of(alarmReq.getType());
		asyncEmailSender.send(emailTemplateParamList, type);
		return null;
	}

	@Override
	public Void sms(@RequestBody AlarmReq alarmReq) {
		// TODO
		List<MobileTemplateParam> mobileTemplateParamList = Lists.newArrayList();
		SmsEnum.Type type = SmsEnum.Type.of(alarmReq.getType());
		asyncSmsSender.send(mobileTemplateParamList, type);
		return null;
	}

	@Override
	public Void warn(@RequestBody AlarmReq alarmReq) {
		// TODO
		return null;
	}
}
