package com.company.tool.webhook;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.easyretry.extension.spring.aop.EasyRetryable;
import com.company.framework.messagedriven.properties.MessagedrivenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.framework.messagedriven.MessageSender;
import com.company.tool.messagedriven.Constants;
import com.company.tool.messagedriven.strategy.StrategyConstants;
import com.company.tool.messagedriven.strategy.dto.SendWebhookMQDto;
import com.company.tool.api.enums.WebhookEnum;
import com.company.tool.entity.WebhookTask;
import com.company.tool.enums.WebhookTaskEnum;
import com.company.tool.service.WebhookTaskService;

/**
 * 企微机器人发送器（异步）
 */
@Component
public class AsyncWebhookSender {

	@Autowired
	private MessageSender messageSender;
	@Autowired
	private MessagedrivenProperties messagedrivenProperties;
	@Autowired
	private WebhookTaskService webhookTaskService;

	/**
	 * 发送
	 *
	 * @param type             WebhookEnum.Type
	 * @param templateParamMap 建议添加参数：时间（time）、主机（host）、日志ID（traceid）、应用（application），方便快速定位
	 */
	public void send(WebhookEnum.Type type, Map<String, String> templateParamMap) {
		send(type, templateParamMap, null, null);
	}

	/**
	 *
	 * @param type             WebhookEnum.Type
	 * @param templateParamMap 建议添加参数：时间（time）、主机（host）、日志ID（traceid）、应用（application），方便快速定位
	 * @param planSendTime     计划发送时间
	 * @param overTime         超时时间
	 */
	public void send(WebhookEnum.Type type, Map<String, String> templateParamMap, LocalDateTime planSendTime,
			LocalDateTime overTime) {

		WebhookTask webhookTask = new WebhookTask();
		webhookTask.setType(type.getCode());
		if (planSendTime == null) {
			planSendTime = LocalDateTime.now();
		}
		webhookTask.setPlanSendTime(planSendTime);
		if (overTime == null) {
			overTime = planSendTime.plusDays(7);
		}
		webhookTask.setOverTime(overTime);
		if(templateParamMap == null) {
			templateParamMap = Collections.emptyMap();
		}
		webhookTask.setTemplateParamJson(JsonUtil.toJsonString(templateParamMap));
		webhookTask.setPlanSendTime(planSendTime);
		boolean sendNow = planSendTime.compareTo(LocalDateTime.now()) <= 0;
		WebhookTaskEnum.Status status = sendNow ? WebhookTaskEnum.Status.PRE_SEND
				: WebhookTaskEnum.Status.PRE_TIME_SEND;
		webhookTask.setStatus(status.getCode());
		webhookTaskService.save(webhookTask);

		if (sendNow) {
			asyncExe(webhookTask.getId(), status);
		}
	}

	/* 定时任务轮询 */
    @EasyRetryable
	public List<Integer> select4PreTimeSend(Integer limit) {
		List<Integer> webhookTaskIdList = webhookTaskService.select4PreTimeSend(WebhookTaskEnum.Status.PRE_TIME_SEND,
				limit);
		return webhookTaskIdList;
	}

	public void exePreTimeSend(Integer id) {
		asyncExe(id, WebhookTaskEnum.Status.PRE_TIME_SEND);
	}
	/* 定时任务轮询 */

	private void asyncExe(Integer webhookTaskId, WebhookTaskEnum.Status status) {
		// MQ异步处理
		SendWebhookMQDto params = new SendWebhookMQDto();
		params.setWebhookTaskId(webhookTaskId);

		messageSender.sendNormalMessage(StrategyConstants.SENDWEBHOOK_STRATEGY, params, messagedrivenProperties.getExchange().getDirect(),
				Constants.QUEUE.SEND_WEBHOOK.KEY);

		// 必须加状态条件，消费者代码可能会比下面的代码先执行
		WebhookTask webhookTask = webhookTaskService.getById(webhookTaskId);
		String remark = Utils.rightRemark(webhookTask.getRemark(), WebhookTaskEnum.Status.SEND_MQ_SUCCESS.getDesc());
		webhookTaskService.updateStatusByStatus(WebhookTaskEnum.Status.SEND_MQ_SUCCESS, remark, webhookTaskId, status);
	}
}
