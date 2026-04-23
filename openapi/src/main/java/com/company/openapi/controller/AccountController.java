package com.company.openapi.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.openapi.annotation.NoSign;
import com.google.common.collect.Maps;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

@NoSign
@RestController
@RequestMapping("/account")
public class AccountController {

	@GetMapping(value = "/appsecret")
	public Map<String, String> appsecret() {
		String appid = RandomUtil.randomNumbers(8);
		String appsecret = IdUtil.fastSimpleUUID();

		Map<String, String> result = Maps.newHashMap();
		result.put("appid", appid);
		result.put("appsecret", appsecret);
		return result;
	}

	public static void main(String[] args) {
		String appid = RandomUtil.randomNumbers(8);
		String appsecret = IdUtil.fastSimpleUUID();
		System.out.println(appid);
		System.out.println(appsecret);
	}
}
