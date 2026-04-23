package com.company.tool.sms;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.tool.entity.SmsRecord;
import com.company.tool.service.SmsRecordService;
import io.github.jqdi.smssender.core.SendPostProcessor;
import io.github.jqdi.smssender.core.SendResponse;

@Component
public class MysqlSendPostProcessor implements SendPostProcessor {

	@Autowired
	private SmsRecordService smsRecordService;

	@Override
	public void afterSend(String channel, String mobile, String signName, String templateCode,
			LinkedHashMap<String, String> templateParamMap, String content, SendResponse sendResponse) {

		SmsRecord smsRecord = new SmsRecord();
		smsRecord.setChannel(channel);
		smsRecord.setMobile(mobile);
		smsRecord.setSignName(signName);
		smsRecord.setTemplateCode(templateCode);
		smsRecord.setTemplateParamJson(JsonUtil.toJsonString(templateParamMap));
		smsRecord.setContent(content);

		if (sendResponse.isSuccess()) {
			smsRecord.setResult("success");
		} else {
			smsRecord.setResult("fail");
			smsRecord.setMessage(sendResponse.getMessage());
		}
		smsRecord.setRequestId(sendResponse.getRequestId());
		smsRecordService.save(smsRecord);
	}
}
