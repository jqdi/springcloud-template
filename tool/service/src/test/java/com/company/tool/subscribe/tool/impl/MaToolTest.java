package com.company.tool.subscribe.tool.impl;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.company.tool.subscribe.dto.SubscribeTemplateInfo;
import com.company.tool.subscribe.tool.dto.MaSubscribe;
import com.company.tool.subscribe.tool.dto.SubscribeMsgData;
import com.company.tool.wx.miniapp.config.WxMaConfiguration;
import com.company.tool.wx.miniapp.config.WxMaProperties;
import com.google.common.collect.Lists;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;

public class MaToolTest {

	@Test
	public void wxMaConfigurationInit() {
		Dict dict = YamlUtil.loadByPath("classpath:application-dev.yml");
		WxMaProperties properties = dict.getByPath("wx.miniapp", WxMaProperties.class);
		System.out.println("properties:" + properties);
		new WxMaConfiguration(properties).init();
		System.out.println("init success");
	}

	@Test
	public void getTemplateList() {
		MaTool maTool = new MaTool();
		String appid = "111";
		List<SubscribeTemplateInfo> templateList = maTool.getTemplateList(appid);
		System.out.println(templateList);
	}

//	@Test
	public void sendSubscribeMsg() {
		MaTool maTool = new MaTool();
		String appid = "111";
		String openid = "111";
		String templateId = "111";
		String page = "111";
		List<SubscribeMsgData> dataList = Lists.newArrayList();
		MaSubscribe maSubscribe = maTool.sendSubscribeMsg(appid, openid, templateId, page, dataList);
		System.out.println(maSubscribe);
	}
}
