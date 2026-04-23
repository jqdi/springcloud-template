package com.company.tool.wx.miniapp.config;

import org.junit.jupiter.api.Test;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;

public class WxMaConfigurationTest {

	@Test
	public void wxMaConfigurationInit() {
		Dict dict = YamlUtil.loadByPath("classpath:application-dev.yml");
		WxMaProperties properties = dict.getByPath("wx.miniapp", WxMaProperties.class);
		System.out.println("properties:" + properties);
		new WxMaConfiguration(properties).init();
		System.out.println("init success");
	}

	@Test
	public void getMaService() {
		WxMaService maService = WxMaConfiguration.getMaService("1111");
		System.out.println(maService);
	}
}
