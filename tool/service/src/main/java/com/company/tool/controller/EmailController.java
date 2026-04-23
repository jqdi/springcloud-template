package com.company.tool.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.enums.EmailEnum;
import com.company.tool.api.feign.EmailFeign;
import com.company.tool.api.request.SendEmailReq;
import com.company.tool.email.AsyncEmailSender;

@RestController
@RequestMapping(value = "/email")
public class EmailController implements EmailFeign {

	@Autowired
	private AsyncEmailSender asyncEmailSender;

	@Override
	public List<Integer> select4PreTimeSend(Integer limit) {
		List<Integer> idList = asyncEmailSender.select4PreTimeSend(limit);
		return idList;
	}
	
	@Override
	public Void exePreTimeSend(Integer id) {
		asyncEmailSender.exePreTimeSend(id);
		return null;
	}

	@Override
	public Void send(@RequestBody SendEmailReq sendEmailReq) {
		String email = sendEmailReq.getEmail();
		Map<String, String> templateParamMap = sendEmailReq.getTemplateParamMap();
		LocalDateTime planSendTime = sendEmailReq.getPlanSendTime();
		EmailEnum.Type type = sendEmailReq.getType();
		if (planSendTime == null) {
			planSendTime = LocalDateTime.now();
		}
		LocalDateTime overTime = sendEmailReq.getOverTime();
		if (overTime == null) {
			overTime = planSendTime.plusDays(1);
		}

		asyncEmailSender.send(email, templateParamMap, type, planSendTime, overTime);
		return null;
	}
}
