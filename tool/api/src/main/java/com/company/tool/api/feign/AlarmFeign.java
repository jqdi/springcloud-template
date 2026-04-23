package com.company.tool.api.feign;

import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.request.AlarmReq;
import com.company.tool.api.request.WebhookAlarmReq;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/alarm", fallbackFactory = ThrowExceptionFallback.class)
public interface AlarmFeign {

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
