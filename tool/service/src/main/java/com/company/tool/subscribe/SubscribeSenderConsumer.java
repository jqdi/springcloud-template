package com.company.tool.subscribe;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.company.framework.util.JsonUtil;
import com.company.framework.util.Utils;
import com.company.tool.entity.SubscribeTask;
import com.company.tool.entity.SubscribeTaskDetail;
import com.company.tool.entity.SubscribeTemplate;
import com.company.tool.entity.SubscribeTypeTemplateConfig;
import com.company.tool.enums.SubscribeTaskDetailEnum;
import com.company.tool.service.SubscribeTaskDetailService;
import com.company.tool.service.SubscribeTaskService;
import com.company.tool.service.SubscribeTemplateGrantService;
import com.company.tool.service.SubscribeTemplateService;
import com.company.tool.service.SubscribeTypeTemplateConfigService;
import com.company.tool.subscribe.tool.IMaTool;
import com.company.tool.subscribe.tool.dto.MaSubscribe;
import com.company.tool.subscribe.tool.dto.SubscribeMsgData;
import com.google.common.collect.Lists;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 订阅消息发送处理器（消费者逻辑）
 */
@Slf4j
@Component
public class SubscribeSenderConsumer {

	@Autowired
	private SubscribeTaskService subscribeTaskService;
	@Autowired
	private SubscribeTaskDetailService subscribeTaskDetailService;
	@Autowired
	private SubscribeTypeTemplateConfigService subscribeTypeTemplateConfigService;
	@Autowired
	private SubscribeTemplateService subscribeTemplateService;
	@Autowired
	private SubscribeTemplateGrantService subscribeTemplateGrantService;
	@Autowired
	private IMaTool maTool;

	@Value("${appid.wx.miniapp:wxeb6ffb1ebd72a4fd1}")
	private String appid;

	public void consumer(Integer subscribeTaskDetailId) {
		SubscribeTaskDetail subscribeTaskDetail = subscribeTaskDetailService.getById(subscribeTaskDetailId);
		SubscribeTask subscribeTask = subscribeTaskService.getById(subscribeTaskDetail.getTaskId());
		String remark = subscribeTaskDetail.getRemark();

		Integer status = subscribeTaskDetail.getStatus();
		SubscribeTaskDetailEnum.Status statusEnum = SubscribeTaskDetailEnum.Status.of(status);

		if (statusEnum == SubscribeTaskDetailEnum.Status.REQ_SUCCESS
				|| statusEnum == SubscribeTaskDetailEnum.Status.REQ_FAIL
				|| statusEnum == SubscribeTaskDetailEnum.Status.SEND_CANCEL) {
			log.info("状态不匹配 {}:{}", subscribeTaskDetailId, status);
			remark = Utils.rightRemark(remark, "状态不匹配:" + statusEnum.getDesc());
			subscribeTaskDetailService.updateRemark(remark, subscribeTaskDetailId);
			return;
		}

		remark = Utils.rightRemark(remark, SubscribeTaskDetailEnum.Status.CONS_MQ_SUCCESS.getDesc());
		subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.CONS_MQ_SUCCESS, remark,
				subscribeTaskDetailId);

		LocalDateTime overTime = subscribeTask.getOverTime();
		LocalDateTime now = LocalDateTime.now();
		String openid = subscribeTaskDetail.getOpenid();
		if (overTime.compareTo(now) < 0) {
			log.info("超时取消发送 {}:{} {}/{}", subscribeTaskDetailId, openid,
					DateUtil.formatLocalDateTime(overTime), DateUtil.formatLocalDateTime(now));
			remark = Utils.rightRemark(remark, "超时取消发送");
			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.SEND_CANCEL, remark,
					subscribeTaskDetailId);
			return;
		}

		List<String> valueList = JsonUtil.toList(subscribeTaskDetail.getTemplateParamJson(), String.class);

		String type = subscribeTask.getType();
		SubscribeTypeTemplateConfig subscribeTypeTemplateConfig = subscribeTypeTemplateConfigService
				.selectTemplateCodeByType(type);
		String templateCode = subscribeTypeTemplateConfig.getTemplateCode();
		if (templateCode == null) {
			log.info("业务-模板未配置:{}", type);
			remark = Utils.rightRemark(remark, "业务-模板未配置:" + type);
			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.SEND_CANCEL, remark,
					subscribeTaskDetailId);
			return;
		}

		SubscribeTemplate subscribeTemplate = subscribeTemplateService.selectByPriTmplId(templateCode);
		if (subscribeTemplate == null) {
			log.info("业务-模板未配置:{}", type);
			remark = Utils.rightRemark(remark, "业务-模板未配置:" + type);
			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.SEND_CANCEL, remark,
					subscribeTaskDetailId);
			return;
		}

		List<SubscribeMsgData> dataList = toSubscribeMsgData(valueList, subscribeTemplate.getContent(),
				subscribeTypeTemplateConfig.getParamIndex());
		if (dataList == null) {
			log.info("参数与模板变量不匹配:{}", type);
			remark = Utils.rightRemark(remark, "参数与模板变量不匹配:" + type);
			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.SEND_CANCEL, remark,
					subscribeTaskDetailId);
			return;
		}

		String priTmplId = subscribeTemplate.getPriTmplId();

		/* 这里判断是否有授权次数似乎没有意义，微信拒绝订阅消息后可能会重置授权次数，系统中记录的跟微信官方记录的不一致，所以不校验，无脑发 */
//		// 判断是否有授权次数
//		boolean hasGrant = subscribeTemplateGrantService.hasGrantByOpenidTemplateCode(openid, priTmplId);
//		if (!hasGrant) {
//			log.info("无授权:{},{}", openid, priTmplId);
//			remark = Utils.rightRemark(remark, "无授权:" + openid + "," + priTmplId);
//			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.SEND_CANCEL, remark,
//					subscribeTaskDetailId);
//			return;
//		}

		String content = JsonUtil.toJsonString(dataList);
		subscribeTaskDetailService.updateContentById(content, subscribeTaskDetailId);

		// 调用订阅消息发送API
		MaSubscribe response = maTool.sendSubscribeMsg(appid, openid, priTmplId, subscribeTaskDetail.getPage(),
				dataList);

		if (response.isSuccess()) {
			// 成功了增加使用次数
			subscribeTemplateGrantService.incrUseNum(openid, priTmplId);

			remark = Utils.rightRemark(remark, SubscribeTaskDetailEnum.Status.REQ_SUCCESS.getDesc());
			subscribeTaskDetailService.updateSendSuccessStatus(SubscribeTaskDetailEnum.Status.REQ_SUCCESS, remark,
					subscribeTaskDetailId);
		} else {
			String message = response.getMessage();
			if (message.contains("用户拒绝接受消息")) {// 如果是用户关闭了授权，则将totalNum设置为useNum，这样用户剩余授权次数为0
				subscribeTemplateGrantService.zeroNum(openid, priTmplId);
			}
//			else {// 其他失败就归还授权次数
//				subscribeTemplateGrantService.returnUseNum(openid, priTmplId);
//			}

			remark = Utils.rightRemark(remark, response.getMessage());
			subscribeTaskDetailService.updateStatusRemark(SubscribeTaskDetailEnum.Status.REQ_FAIL, remark,
					subscribeTaskDetailId);
		}
	}

	/**
	 * 参数与模板匹配
	 *
	 * @param valueList
	 * @param content
	 * @param paramIndex
	 * @return
	 */
	private static List<SubscribeMsgData> toSubscribeMsgData(List<String> valueList, String content, String paramIndex) {
		Pattern pattern = Pattern.compile("\\{\\{(\\w+).DATA}}");
		String str = content;
		Matcher matcher = pattern.matcher(str);

		List<String> nameList = Lists.newArrayList();
		while (matcher.find()) {
			String group = matcher.group(1);
			nameList.add(group);
		}

		List<Integer> paramIndexList = Lists.newArrayList();
		for (int i = 0; i < nameList.size(); i++) {
			paramIndexList.add(i);
		}

		if (StringUtils.isNotBlank(paramIndex)) {
			String[] paramIndexs = StringUtils.split(paramIndex, ",");
			paramIndexList = Arrays.asList(paramIndexs).stream().map(Integer::valueOf).collect(Collectors.toList());
		}

		if (paramIndexList.size() != nameList.size()) {
			return null;
		}

		List<SubscribeMsgData> dataList = Lists.newArrayList();
		for (int i = 0; i < paramIndexList.size(); i++) {
			SubscribeMsgData subscribeMsgData = new SubscribeMsgData();
			subscribeMsgData.setName(nameList.get(i));
			subscribeMsgData.setValue(
					subscribeValueLengthLimit(subscribeMsgData.getName(), valueList.get(paramIndexList.get(i))));
			dataList.add(subscribeMsgData);
		}
		return dataList;
	}

	/**
	 * 参数与模板结合
	 *
	 * @param dataList
	 * @param content
	 * @return
	 */
	private static String toSubscribeContent(List<SubscribeMsgData> dataList, String content) {
		String tmpContent = content;
		for (SubscribeMsgData subscribeMsgData : dataList) {
			tmpContent = tmpContent.replace(String.format("{{%s.DATA}}", subscribeMsgData.getName()),
					subscribeMsgData.getValue());
		}
		return tmpContent;
	}

	/**
	 * <pre>
	 * 订阅消息参数值内容限制说明
	 * 官网说明：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-message-management/subscribe-message/sendMessage.html
	 * </pre>
	 */
	private static String subscribeValueLengthLimit(String name, String value) {
		if (StringUtils.isBlank(name)) {
			return value;
		}
		if (name.startsWith("thing")) {// 事物	20个以内字符	可汉字、数字、字母或符号组合
			return StringUtils.left(value, 20);
		}
		if (name.startsWith("number")) {// 数字	32位以内数字	只能数字，可带小数
			return StringUtils.left(value, 32);
		}
		if (name.startsWith("symbol")) {// 字母	32位以内字母	只能字母
			return StringUtils.left(value, 32);
		}
		if (name.startsWith("letter")) {// 符号	5位以内符号	只能符号
			return StringUtils.left(value, 5);
		}
		if (name.startsWith("letter")) {// 字母	32位以内字母	只能字母
			return StringUtils.left(value, 32);
		}
		if (name.startsWith("character_string")) {// 字母	32位以内字母	只能字母
			return StringUtils.left(value, 32);
		}
		if (name.startsWith("time")) {// 24小时制时间格式（支持+年月日），支持填时间段，两个时间点之间用“~”符号连接	例如：15:01，或：2019年10月1日 15:01
			return value;
		}
		if (name.startsWith("date")) {// 年月日格式（支持+24小时制时间），支持填时间段，两个时间点之间用“~”符号连接	例如：2019年10月1日，或：2019年10月1日 15:01
			return value;
		}
		if (name.startsWith("amount")) {// 金额	1个币种符号+10位以内纯数字，可带小数，结尾可带“元”	可带小数
			return StringUtils.left(value, 10);
		}
		if (name.startsWith("phone_number")) {// 电话	17位以内，数字、符号	电话号码，例：+86-0766-66888866
			return StringUtils.left(value, 17);
		}
		if (name.startsWith("car_number")) {// 车牌	8位以内，第一位与最后一位可为汉字，其余为字母或数字	车牌号码：粤A8Z888挂
			return StringUtils.left(value, 8);
		}
		if (name.startsWith("name")) {// 姓名	10个以内纯汉字或20个以内纯字母或符号	中文名10个汉字内；纯英文名20个字母内；中文和字母混合按中文名算，10个字内
			return StringUtils.left(value, 10);
		}
		if (name.startsWith("phrase")) {// 汉字	5个以内汉字	5个以内纯汉字，例如：配送中
			return StringUtils.left(value, 5);
		}
		if (name.startsWith("enum")) {// 枚举值	只能上传枚举值范围内的字段值	调用接口获取参考枚举值
			return value;
		}
		return value;
	}

	public static void main(String[] args) {
		List<String> valueList = Lists.newArrayList(
				"新人券包32元",
				"代金券",
				"5",
				"2023-09-12 23:59:59",
				"您的新人券包已经到账，请立即使用"
				);
		String content = "优惠券名称:{{thing1.DATA}}优惠券金额:{{amount5.DATA}}数量:{{number7.DATA}}有效期:{{time8.DATA}}温馨提示:{{thing3.DATA}}";
		String paramIndex = null;

//		String content = "优惠券名称:{{thing1.DATA}}优惠券金额:{{amount5.DATA}}温馨提示:{{thing3.DATA}}有效期:{{time8.DATA}}";
//		String paramIndex = "0,2,4,3";

		List<SubscribeMsgData> subscribeMsgDataList = toSubscribeMsgData(valueList, content, paramIndex);
		System.out.println(subscribeMsgDataList);

		System.out.println(toSubscribeContent(subscribeMsgDataList, content));

	}
}
