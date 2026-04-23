package com.company.tool.webhook;


import cn.felord.WeComException;
import cn.felord.api.WorkWeChatApi;
import cn.felord.domain.WeComResponse;
import cn.felord.domain.webhook.WebhookBody;
import cn.felord.domain.webhook.WebhookTextBody;

public class WeComSDKTest {

	public static void main(String[] args) {
		// 发纯文本
		WebhookBody textBody = WebhookTextBody.from("candi test:这里为纯文本");

		String robot_key = "1d32e387-1111-4694-9ce8-111111111111";

		try {
			WeComResponse send = WorkWeChatApi.webhookApi().send(robot_key, textBody);
			System.out.println(send);
		} catch (WeComException e) {
			e.printStackTrace();
		}
	}

}
