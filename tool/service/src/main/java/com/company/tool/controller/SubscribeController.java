package com.company.tool.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.JsonUtil;
import com.company.tool.api.enums.SubscribeEnum;
import com.company.tool.api.feign.SubscribeFeign;
import com.company.tool.api.request.SubscribeGrantReq;
import com.company.tool.api.request.SubscribeSendReq;
import com.company.tool.entity.SubscribeTemplate;
import com.company.tool.entity.SubscribeTypeTemplateConfig;
import com.company.tool.service.SubscribeGroupTypeService;
import com.company.tool.service.SubscribeTemplateGrantService;
import com.company.tool.service.SubscribeTemplateService;
import com.company.tool.service.SubscribeTypeTemplateConfigService;
import com.company.tool.subscribe.AsyncSubscribeSender;
import com.company.tool.subscribe.SubscribeType;
import com.company.tool.subscribe.SubscribeTypeBeanFactory;
import com.company.tool.subscribe.dto.SubscribeSendDto;
import com.company.tool.subscribe.dto.SubscribeTemplateInfo;
import com.company.tool.subscribe.tool.IMaTool;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.response.UserOauthResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/subscribe")
public class SubscribeController implements SubscribeFeign {

	@Autowired
	private SubscribeTemplateGrantService subscribeTemplateGrantService;
	@Autowired
	private SubscribeGroupTypeService subscribeGroupTypeService;
	@Autowired
	private SubscribeTypeTemplateConfigService subscribeTypeTemplateConfigService;
	@Autowired
	private SubscribeTemplateService subscribeTemplateService;
	@Autowired
	private AsyncSubscribeSender asyncSubscribeSender;
	@Autowired
	private UserOauthFeign userOauthFeign;
	@Autowired
	private IMaTool maTool;

	@Value("${appid.wx.miniapp:1111}")
	private String appid;

	// 'accept'表示用户同意订阅该条id对应的模板消息，'reject'表示用户拒绝订阅该条id对应的模板消息，'ban'表示已被后台封禁，'filter'表示该模板因为模板标题同名被后台过滤
	private static final String SUBSCRIBEMESSAGE_TEMPLATE_ACCEPT = "accept";

	@Override
	public List<String> selectTemplateCodeByGroup(String group) {
		List<SubscribeEnum.Type> typeList = subscribeGroupTypeService.selectTypesByGroup(group);

		// 配置约定，最多3个
		List<String> templateCodeList = typeList.stream().map(type -> {
			SubscribeTypeTemplateConfig subscribeTypeTemplateConfig = subscribeTypeTemplateConfigService
					.selectTemplateCodeByType(type.getCode());
			return subscribeTypeTemplateConfig.getTemplateCode();
		}).collect(Collectors.toList());

		return templateCodeList;
	}

	@Override
	public Void grant(@RequestBody SubscribeGrantReq subscribeGrantReq) {
		String openid = subscribeGrantReq.getOpenid();
		String resJson = subscribeGrantReq.getResJson();

		@SuppressWarnings("unchecked")
		Map<String, String> templateCodeResultMap = JsonUtil.toEntity(resJson, Map.class);

		List<String> templateCodeList = templateCodeResultMap.entrySet().stream()
				.filter(v -> SUBSCRIBEMESSAGE_TEMPLATE_ACCEPT.equals(v.getValue())).map(v -> v.getKey())
				.collect(Collectors.toList());

		// 记录授权
		for (String templateCode : templateCodeList) {
			subscribeTemplateGrantService.grant(openid, templateCode);
		}

		/**
		 * <pre>
		 * 触发‘订阅消息’的方式有2种
		 * 1.当前订阅跟前一个步骤的操作紧密关联，需要通过runtimeAttach获取参数
		 * 2.当前订阅跟当前操作无关联，这样的话runtimeAttach的参数也没有太大意义了
		 * </pre>
		 */
		Integer userId = HeaderContextUtil.currentUserIdInt();
		if (userId == null) {// 未登录情况下尝试通过openid查到用户ID
			UserOauthResp userOauthResp = userOauthFeign
					.selectOauth(UserOauthEnum.IdentityType.WX_OPENID_MINIAPP, openid);
			userId = Optional.ofNullable(userOauthResp).map(UserOauthResp::getUserId).orElse(null);
		}

		String group = subscribeGrantReq.getGroup();
		Map<String, String> runtimeAttach = subscribeGrantReq.getRuntimeAttach();

		// 即时创建订阅消息发送任务
		for (String templateCode : templateCodeList) {
			SubscribeEnum.Type type = subscribeGroupTypeService.selectTypeByGroupTemplateCode(group, templateCode);
			if (type == null) {
				log.info("type is null,group:{},templateCode:{}", group, templateCode);
				continue;
			}
			SubscribeType subscribeType = SubscribeTypeBeanFactory.from(group, type);
			if (subscribeType == null) {
				log.info("subscribeType is null,group:{},type:{}", group, type);
				continue;
			}
			SubscribeSendDto subscribeSendDto = subscribeType.wrapParam(userId, runtimeAttach);
			if (subscribeSendDto == null) {
				log.info("subscribeSendDto is null");
				continue;
			}
			String page = subscribeSendDto.getPage();
			List<String> valueList = subscribeSendDto.getValueList();
			LocalDateTime planSendTime = subscribeSendDto.getPlanSendTime();
			LocalDateTime overTime = Optional.ofNullable(subscribeSendDto.getOverTime())
					.orElse(subscribeSendDto.getPlanSendTime().plusHours(1));// 默认1小时没发出去就超时

			// 发送订阅消息
			asyncSubscribeSender.send(openid, page, valueList, type, planSendTime, overTime);
		}

		return null;
	}

	@Override
	public Void send(@RequestBody SubscribeSendReq subscribeSendReq) {
		String openid = subscribeSendReq.getOpenid();
		String page = subscribeSendReq.getPage();
		List<String> valueList = subscribeSendReq.getValueList();
		SubscribeEnum.Type type = subscribeSendReq.getType();
		LocalDateTime planSendTime = subscribeSendReq.getPlanSendTime();
		LocalDateTime overTime = Optional.ofNullable(subscribeSendReq.getOverTime())
				.orElse(subscribeSendReq.getPlanSendTime().plusHours(1));// 默认1小时没法出去就超时

		// 发送订阅消息
		asyncSubscribeSender.send(openid, page, valueList, type, planSendTime, overTime);

		return null;
	}

	@Override
	public List<Integer> select4PreTimeSend(Integer limit) {
		List<Integer> idList = asyncSubscribeSender.select4PreTimeSend(limit);
		return idList;
	}

	@Override
	public Void exePreTimeSend(Integer id) {
		asyncSubscribeSender.exePreTimeSend(id);
		return null;
	}

	/**
	 * 同步模板数据并保存到数据库
	 *
	 * @return
	 */
	@Override
	public Void syncTemplate() {
		List<SubscribeTemplateInfo> templateList = maTool.getTemplateList(appid);
		for (SubscribeTemplateInfo subscribeTemplateInfo : templateList) {
			String priTmplId = subscribeTemplateInfo.getPriTmplId();
			SubscribeTemplate subscribeTemplate = subscribeTemplateService.selectByPriTmplId(priTmplId);
			if (subscribeTemplate == null) {
				subscribeTemplate = new SubscribeTemplate();
				subscribeTemplate.setPriTmplId(priTmplId);
				subscribeTemplate.setTitle(subscribeTemplateInfo.getTitle());
				subscribeTemplate.setContent(subscribeTemplateInfo.getContent());
				subscribeTemplate.setExample(subscribeTemplateInfo.getExample());
				subscribeTemplate.setType(subscribeTemplateInfo.getType());

				subscribeTemplateService.save(subscribeTemplate);
			} else {
				SubscribeTemplate subscribeTemplate4Update = new SubscribeTemplate();
				subscribeTemplate4Update.setId(subscribeTemplate.getId());

				subscribeTemplate4Update.setTitle(subscribeTemplateInfo.getTitle());
				subscribeTemplate4Update.setContent(subscribeTemplateInfo.getContent());
				subscribeTemplate4Update.setExample(subscribeTemplateInfo.getExample());
				subscribeTemplate4Update.setType(subscribeTemplateInfo.getType());

				subscribeTemplateService.updateById(subscribeTemplate4Update);
			}
		}
		return null;
	}
}
