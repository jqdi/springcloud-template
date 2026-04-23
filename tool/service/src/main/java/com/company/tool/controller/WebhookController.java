package com.company.tool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.feign.WebhookFeign;
import com.company.tool.webhook.AsyncWebhookSender;

@RestController
@RequestMapping(value = "/webhook")
public class WebhookController implements WebhookFeign {

	@Autowired
	private AsyncWebhookSender asyncWebhookSender;

	@Override
	public List<Integer> select4PreTimeSend(Integer limit) {
		List<Integer> idList = asyncWebhookSender.select4PreTimeSend(limit);
		return idList;
	}
	
	@Override
	public Void exePreTimeSend(Integer id) {
		asyncWebhookSender.exePreTimeSend(id);
		return null;
	}
}
