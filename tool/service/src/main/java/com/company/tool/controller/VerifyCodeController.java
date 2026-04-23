package com.company.tool.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import com.company.framework.globalresponse.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.enums.EmailEnum;
import com.company.tool.api.enums.SmsEnum;
import com.company.tool.api.feign.VerifyCodeFeign;
import com.company.tool.api.response.CaptchaResp;
import com.company.tool.email.AsyncEmailSender;
import com.company.tool.entity.VerifyCode;
import com.company.tool.enums.VerifyCodeEnum;
import com.company.tool.service.VerifyCodeService;
import com.company.tool.sms.AsyncSmsSender;
import com.google.common.collect.Maps;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

@RestController
@RequestMapping(value = "/verifyCode")
public class VerifyCodeController implements VerifyCodeFeign {
	@Autowired
	private VerifyCodeService verifyCodeService;
	@Autowired
	private AsyncSmsSender asyncSmsSender;
	@Autowired
	private AsyncEmailSender asyncEmailSender;
	
	@Override
	public Map<String, String> sms(String mobile, String type) {
		VerifyCode verifyCode = verifyCodeService.selectLastByCertificateType(mobile, type);
		if (verifyCode != null) {
			if (VerifyCodeEnum.Status.UN_USE == VerifyCodeEnum.Status.of(verifyCode.getStatus())) {
				LocalDateTime validTime = verifyCode.getValidTime();
				LocalDateTime now = LocalDateTime.now();
				if (validTime.compareTo(now) >= 0) {// 未使用且未过期
					ExceptionUtil.throwException("验证码已发送，请勿重复操作！");
				}
			}
		}
		
		String code = RandomUtil.randomNumbers(6);

		verifyCodeService.save(type, mobile, code);

		// 发送短信
		Map<String, String> templateParamMap = Maps.newHashMap();
		templateParamMap.put("code", code);
		LocalDateTime planSendTime = LocalDateTime.now();
		LocalDateTime overTime = planSendTime.plusMinutes(5);

		asyncSmsSender.send(mobile, templateParamMap, SmsEnum.Type.VERIFYCODE, planSendTime, overTime);

        return Collections.singletonMap("value", "验证码发送成功");
	}

	@Override
	public Map<String, String> email(String email, String type) {
		VerifyCode verifyCode = verifyCodeService.selectLastByCertificateType(email, type);
		if (verifyCode != null) {
			if (VerifyCodeEnum.Status.UN_USE == VerifyCodeEnum.Status.of(verifyCode.getStatus())) {
				LocalDateTime validTime = verifyCode.getValidTime();
				LocalDateTime now = LocalDateTime.now();
				if (validTime.compareTo(now) >= 0) {// 未使用且未过期
					ExceptionUtil.throwException("验证码已发送，请勿重复操作！");
				}
			}
		}
		
		String code = RandomUtil.randomNumbers(6);

		verifyCodeService.save(type, email, code);

		// 发送邮件
		Map<String, String> templateParamMap = Maps.newHashMap();
		templateParamMap.put("code", code);
		LocalDateTime planSendTime = LocalDateTime.now();
		LocalDateTime overTime = planSendTime.plusMinutes(5);

		asyncEmailSender.send(email, templateParamMap, EmailEnum.Type.VERIFYCODE, planSendTime, overTime);

        return Collections.singletonMap("value", "验证码发送成功");
	}
	
	@Override
	public CaptchaResp captcha(String type) {
		LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 90, 4, 100);
		String code = lineCaptcha.getCode();
		String uuid = IdUtil.fastSimpleUUID();

		verifyCodeService.save(type, uuid, code);

		CaptchaResp resp = new CaptchaResp();
		resp.setUuid(uuid);
		resp.setImageBase64Data(lineCaptcha.getImageBase64Data());
		return resp;
	}

	@Override
	public Boolean verify(String type, String certificate, String inputcode) {
		boolean verifyPass = verifyCodeService.verify(type, certificate, inputcode);
		return verifyPass;
	}
}
