package com.company.tool.sms;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.tool.entity.SmsTask;
import com.company.tool.entity.SmsTaskDetail;
import com.company.tool.entity.SmsTemplate;
import com.company.tool.entity.SmsTypeTemplateConfig;
import com.company.tool.enums.SmsTaskDetailEnum;
import com.company.tool.service.SmsTaskDetailService;
import com.company.tool.service.SmsTaskService;
import com.company.tool.service.SmsTemplateService;
import com.company.tool.service.SmsTypeTemplateConfigService;
import io.github.jqdi.smssender.core.SendResponse;
import io.github.jqdi.smssender.core.SmsSender;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送处理器（消费者逻辑）
 */
@Slf4j
@Component
public class SmsSenderConsumer {

	@Autowired
	private SmsTaskService smsTaskService;
	@Autowired
	private SmsTaskDetailService smsTaskDetailService;
	@Autowired
	private SmsTypeTemplateConfigService smsTypeTemplateConfigService;
	@Autowired
	private SmsTemplateService smsTemplateService;
	@Autowired
	private SmsSender smsSender;

	public void consumer(Integer smsTaskDetailId) {
		SmsTaskDetail smsTaskDetail = smsTaskDetailService.getById(smsTaskDetailId);
		SmsTask smsTask = smsTaskService.getById(smsTaskDetail.getTaskId());
		String remark = smsTaskDetail.getRemark();

		Integer status = smsTaskDetail.getStatus();
		SmsTaskDetailEnum.Status statusEnum = SmsTaskDetailEnum.Status.of(status);

		if (statusEnum == SmsTaskDetailEnum.Status.REQ_SUCCESS || statusEnum == SmsTaskDetailEnum.Status.REQ_FAIL
				|| statusEnum == SmsTaskDetailEnum.Status.SEND_CANCEL) {
			log.info("状态不匹配 {}:{}", smsTaskDetailId, status);
			remark = Utils.rightRemark(remark, "状态不匹配:" + statusEnum.getDesc());
			smsTaskDetailService.updateRemark(remark, smsTaskDetailId);
			return;
		}

		remark = Utils.rightRemark(remark, SmsTaskDetailEnum.Status.CONS_MQ_SUCCESS.getDesc());
		smsTaskDetailService.updateStatusRemark(SmsTaskDetailEnum.Status.CONS_MQ_SUCCESS, remark, smsTaskDetailId);

		LocalDateTime overTime = smsTask.getOverTime();
		LocalDateTime now = LocalDateTime.now();
		if (overTime.compareTo(now) < 0) {
			log.info("超时取消发送 {}:{} {}/{}", smsTaskDetailId, smsTaskDetail.getMobile(),
					DateUtil.formatLocalDateTime(overTime), DateUtil.formatLocalDateTime(now));
			remark = Utils.rightRemark(remark, "超时取消发送");
			smsTaskDetailService.updateStatusRemark(SmsTaskDetailEnum.Status.SEND_CANCEL, remark, smsTaskDetailId);
			return;
		}

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> templateParamMap = JsonUtil.toEntity(smsTaskDetail.getTemplateParamJson(),
				LinkedHashMap.class);

		String type = smsTask.getType();
		String channel = smsSender.channel();
		SmsTypeTemplateConfig smsTypeTemplateConfig = smsTypeTemplateConfigService.selectByTypeChannel(type, channel);
		if (smsTypeTemplateConfig == null) {
			log.info("业务-模板未配置:{}-{}", type, channel);
			remark = Utils.rightRemark(remark, "业务-模板未配置:" + type + "-" + channel);
			smsTaskDetailService.updateStatusRemark(SmsTaskDetailEnum.Status.SEND_CANCEL, remark, smsTaskDetailId);
			return;
		}
		String templateCode = smsTypeTemplateConfig.getTemplateCode();

		String content = null;
		SmsTemplate smsTemplate = smsTemplateService.selectByChannelTemplateCode(channel, templateCode);
		if (smsTemplate != null) {
			String templateContent = smsTemplate.getTemplateContent();
			content = fillTemplateContent(templateContent, templateParamMap);
		} else {
			if (StringUtils.isBlank(content)) {
				content = String.format("模板sms_template未配置,channel:%s,templateCode:%s", channel, templateCode);
			}
		}
		smsTaskDetailService.updateContentById(content, smsTaskDetailId);

		// 调用短信发送API
		SendResponse response = smsSender.send(smsTaskDetail.getMobile(), templateCode, templateParamMap, content);
		if (response.isSuccess()) {
			remark = Utils.rightRemark(remark, SmsTaskDetailEnum.Status.REQ_SUCCESS.getDesc());
			smsTaskDetailService.updateSendSuccessStatus(SmsTaskDetailEnum.Status.REQ_SUCCESS, remark, smsTaskDetailId);
		} else {
			remark = Utils.rightRemark(remark, response.getMessage());
			smsTaskDetailService.updateStatusRemark(SmsTaskDetailEnum.Status.REQ_FAIL, remark, smsTaskDetailId);
		}
	}

	/**
	 * 填充模板内容
	 *
	 * @param templateContent
	 * @param map
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
