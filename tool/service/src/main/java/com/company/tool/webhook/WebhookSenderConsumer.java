package com.company.tool.webhook;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.company.framework.trace.TraceManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.framework.context.SpringContextUtil;
import com.company.framework.util.IpUtil;
import com.company.tool.entity.WebhookTask;
import com.company.tool.entity.WebhookTemplate;
import com.company.tool.enums.WebhookTaskEnum;
import com.company.tool.service.WebhookTaskService;
import com.company.tool.service.WebhookTemplateService;

import cn.felord.WeComException;
import cn.felord.api.WorkWeChatApi;
import cn.felord.domain.WeComResponse;
import cn.felord.domain.webhook.WebhookBody;
import cn.felord.domain.webhook.WebhookTextBody;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 企微机器人发送处理器（消费者逻辑）
 */
@Slf4j
@Component
public class WebhookSenderConsumer {

	@Autowired
	private WebhookTaskService webhookTaskService;
	@Autowired
	private WebhookTemplateService webhookTemplateService;
	@Autowired
	private TraceManager traceManager;

	public void consumer(Integer webhookTaskId) {
		WebhookTask webhookTask = webhookTaskService.getById(webhookTaskId);
		String remark = webhookTask.getRemark();

		Integer status = webhookTask.getStatus();
		WebhookTaskEnum.Status statusEnum = WebhookTaskEnum.Status.of(status);

		if (statusEnum == WebhookTaskEnum.Status.REQ_SUCCESS || statusEnum == WebhookTaskEnum.Status.REQ_FAIL
				|| statusEnum == WebhookTaskEnum.Status.SEND_CANCEL) {
			log.info("状态不匹配 {}:{}", webhookTaskId, status);
			remark = Utils.rightRemark(remark, "状态不匹配:" + statusEnum.getDesc());
			webhookTaskService.updateRemark(remark, webhookTaskId);
			return;
		}

		remark = Utils.rightRemark(remark, WebhookTaskEnum.Status.CONS_MQ_SUCCESS.getDesc());
		webhookTaskService.updateStatusRemark(WebhookTaskEnum.Status.CONS_MQ_SUCCESS, remark, webhookTaskId);

		LocalDateTime overTime = webhookTask.getOverTime();
		LocalDateTime now = LocalDateTime.now();
		if (overTime.compareTo(now) < 0) {
			log.info("超时取消发送 {}:{} {}/{}", webhookTaskId, webhookTask.getKey(), DateUtil.formatLocalDateTime(overTime),
					DateUtil.formatLocalDateTime(now));
			remark = Utils.rightRemark(remark, "超时取消发送");
			webhookTaskService.updateStatusRemark(WebhookTaskEnum.Status.SEND_CANCEL, remark, webhookTaskId);
			return;
		}

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> templateParamMap = JsonUtil.toEntity(webhookTask.getTemplateParamJson(),
				LinkedHashMap.class);

		String type = webhookTask.getType();
		WebhookTemplate webhookTemplate = webhookTemplateService.selectByType(type);
		if (webhookTemplate == null) {
			remark = Utils.rightRemark(remark, String.format("模板webhook_template未配置,type:%s", type));
			webhookTaskService.updateStatusRemark(WebhookTaskEnum.Status.SEND_CANCEL, remark, webhookTaskId);
			return;
		}

		String templateContent = webhookTemplate.getTemplateContent();

		// 插入系统公共参数
		templateParamMap.putIfAbsent("taskid", String.valueOf(webhookTaskId));
		templateParamMap.putIfAbsent("type", type);
		templateParamMap.putIfAbsent("time", DateUtil.now());
		templateParamMap.putIfAbsent("host", IpUtil.getHostIp());
		templateParamMap.putIfAbsent("traceid", traceManager.get());
		templateParamMap.putIfAbsent("application", SpringContextUtil.getProperty("spring.application.name"));
		String content = fillTemplateContent(templateContent, templateParamMap);

		String key = webhookTemplate.getKey();

		String mentionedListStr = webhookTemplate.getMentionedList();
		List<String> mentionedList = null;
		if (StringUtils.isNotBlank(mentionedListStr)) {
			mentionedList = Arrays.asList(StringUtils.split(mentionedListStr, ","));
		}

		String mentionedMobileListStr = webhookTemplate.getMentionedMobileList();
		List<String> mentionedMobileList = null;
		if (StringUtils.isNotBlank(mentionedMobileListStr)) {
			mentionedMobileList = Arrays.asList(StringUtils.split(mentionedMobileListStr, ","));
		}

		webhookTaskService.updateKeyContentMentionById(key, content, mentionedListStr, mentionedMobileListStr,
				webhookTaskId);

		// 调用企微机器人发送API
		try {
			WebhookBody body = WebhookTextBody.from(content, mentionedList, mentionedMobileList);
			WeComResponse response = WorkWeChatApi.webhookApi().send(key, body);
			log.info("response:{}", response);
			if (response.isError()) {
				remark = Utils.rightRemark(remark, response.getErrmsg());
				webhookTaskService.updateStatusRemark(WebhookTaskEnum.Status.REQ_FAIL, remark, webhookTaskId);
				return;
			}
			remark = Utils.rightRemark(remark, WebhookTaskEnum.Status.REQ_SUCCESS.getDesc());
			webhookTaskService.updateSendSuccessStatus(WebhookTaskEnum.Status.REQ_SUCCESS, remark, webhookTaskId);
		} catch (WeComException e) {
			log.error("send error", e);
			remark = Utils.rightRemark(remark, e.getMessage());
			webhookTaskService.updateStatusRemark(WebhookTaskEnum.Status.REQ_FAIL, remark, webhookTaskId);
		}
	}

	/**
	 * 填充模板内容
	 *
	 * @param templateContent
	 * @param templateParamMap
	 * @return
	 */
	private String fillTemplateContent(String templateContent, LinkedHashMap<String, String> templateParamMap) {
		if (templateContent == null) {
			return null;
		}

		String content = templateContent;

		Set<Entry<String, String>> entrySet = templateParamMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			content = content.replace(String.format("${%s}", key), value);
		}

		return content;
	}
}
