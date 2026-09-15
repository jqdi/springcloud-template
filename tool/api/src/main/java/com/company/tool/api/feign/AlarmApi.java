package com.company.tool.api.feign;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.company.tool.api.request.AlarmReq;
import com.company.tool.api.request.WebhookAlarmReq;

public interface AlarmApi {

	/**
	 * 企微机器人告警
	 * 
	 * @param webhookAlarmReq
	 * @return
	 */
	@PostMapping("/webhook")
	Void webhook(@RequestBody WebhookAlarmReq webhookAlarmReq);

	/**
	 * 邮件告警
	 * 
	 * @param alarmReq
	 * @return
	 */
	@Deprecated // 待实现
	@PostMapping("/email")
	Void email(@RequestBody AlarmReq alarmReq);
	
	/**
	 * 短信告警
	 * 
	 * @param alarmReq
	 * @return
	 */
	@Deprecated // 待实现
	@PostMapping("/sms")
	Void sms(@RequestBody AlarmReq alarmReq);
	
	/**
	 * 告警(根据告警等级自主判断企微、邮件、短信告警)
	 * 
	 * @param alarmReq
	 * @return
	 */
	@Deprecated // 待实现
	@PostMapping("/warn")
	Void warn(@RequestBody AlarmReq alarmReq);
}