package com.company.system.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.system.api.feign.SysUserPasswordFeign;
import com.company.system.api.request.RemindPasswordExpireReq;
import com.company.system.api.request.SaveNewPasswordReq;
import com.company.system.api.response.SysUserPasswordTipsResp;
import com.company.system.entity.SysUser;
import com.company.system.entity.SysUserPassword;
import com.company.system.service.SysUserPasswordService;
import com.company.system.service.SysUserService;
import com.company.tool.api.enums.EmailEnum;
import com.company.tool.api.enums.SmsEnum;
import com.company.tool.api.feign.EmailFeign;
import com.company.tool.api.feign.RetryerFeign;
import com.company.tool.api.feign.SmsFeign;
import com.company.tool.api.request.RetryerInfoReq;
import com.company.tool.api.request.SendEmailReq;
import com.company.tool.api.request.SendSmsReq;
import com.company.tool.api.response.RetryerResp;
import com.google.common.collect.Maps;

import cn.hutool.core.date.LocalDateTimeUtil;

@RestController
@RequestMapping("/sysUserPassword")
public class SysUserPasswordController implements SysUserPasswordFeign {
	private static final String NOTIFY_URL_REMINDPASSWORDEXPIRE = com.company.system.api.constant.Constants
			.feignUrl("/sysUserPassword/remindPasswordExpire");

	@Autowired
	private SysUserPasswordService sysUserPasswordService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private RetryerFeign retryerFeign;
	@Autowired
	private EmailFeign emailFeign;
	@Autowired
	private SmsFeign smsFeign;

	@Value("${sys.password.maxExpireLoginTimes:3}")
	private Integer maxExpireLoginTimes;

	@Value("${sys.password.remind.days:7}")
	private Integer remindDays;

	@Value("${sys.password.expire.days:90}")
	private Integer expireDays;

	@Override
	public Map<String, String> getPasswordBySysUserId(Integer sysUserId) {
        SysUserPassword sysUserPassword = sysUserPasswordService.getLastBySysUserId(sysUserId);
        if (sysUserPassword == null) {
            ExceptionUtil.throwException("密码未配置");
        }
        return Collections.singletonMap("value", sysUserPassword.getPassword());
	}

    @Override
    public SysUserPasswordTipsResp getPasswordTipsBySysUserId(Integer sysUserId) {
        SysUserPassword sysUserPassword = sysUserPasswordService.getLastBySysUserId(sysUserId);
        if (sysUserPassword == null) {
            ExceptionUtil.throwException("密码未配置");
        }

		SysUserPasswordTipsResp resp = new SysUserPasswordTipsResp();
		LocalDateTime now = LocalDateTime.now();
		if (sysUserPassword.getExpireTime().compareTo(now) > 0) {// 未过期
			resp.setCanUse(true);
			long days = LocalDateTimeUtil.between(now, sysUserPassword.getExpireTime(), ChronoUnit.DAYS);
			if (days < remindDays) {// 7天内过期
				resp.setPasswordTips("您的密码还有" + (days + 1) + "天过期，请及时更改密码");
			}
			return resp;
		}

		// 密码过期后，再给3次登录机会，登录后修改密码
		if (sysUserPassword.getExpireLoginTimes() < maxExpireLoginTimes) {
			resp.setCanUse(true);
			resp.setPasswordTips("您的密码已过期，未修改密码之前，您还能登录系统"
					+ (maxExpireLoginTimes - sysUserPassword.getExpireLoginTimes() - 1) + "次，请及时更改密码");
			return resp;
		}

		resp.setCanUse(false);
		resp.setPasswordTips("密码已过期，请联系管理员重置");
		return resp;
    }

	@Override
	public Void saveNewPassword(SaveNewPasswordReq saveNewPasswordReq) {
		SysUserPassword sysUserPassword = new SysUserPassword();
		sysUserPassword.setSysUserId(saveNewPasswordReq.getSysUserId());
		sysUserPassword.setPassword(saveNewPasswordReq.getPassword());
		LocalDateTime expireTime = LocalDateTime.now().plusDays(expireDays);
		sysUserPassword.setExpireTime(expireTime);
		sysUserPassword.setExpireLoginTimes(0);
		sysUserPasswordService.save(sysUserPassword);

		// 埋下提醒任务
		RemindPasswordExpireReq remindPasswordExpireReq = new RemindPasswordExpireReq();
		remindPasswordExpireReq.setSysUserId(saveNewPasswordReq.getSysUserId());
		remindPasswordExpireReq.setSysUserPasswordId(sysUserPassword.getId());

		RetryerInfoReq retryerInfoReq = RetryerInfoReq.builder()
				.feignUrl(NOTIFY_URL_REMINDPASSWORDEXPIRE)
				.jsonParams(remindPasswordExpireReq)
				.nextDisposeTime(expireTime)
				.build();
		retryerFeign.call(retryerInfoReq);

		return null;
	}

	/**
	 * 提醒密码过期(使用restTemplate的方式调用)
	 */
	@PostMapping("/remindPasswordExpire")
	public RetryerResp remindPasswordExpire(@RequestBody RemindPasswordExpireReq remindPasswordExpireReq) {
		Integer sysUserId = remindPasswordExpireReq.getSysUserId();
		Integer sysUserPasswordId = remindPasswordExpireReq.getSysUserPasswordId();
		SysUserPassword sysUserPassword = sysUserPasswordService.getLastBySysUserId(sysUserId);
		if (!sysUserPasswordId.equals(sysUserPassword.getId())) {
			// 最新密码已经不是该密码了
            return RetryerResp.end();
		}

		// 发送时间调到（09:00~18:30）工作时间内
		LocalDateTime now = LocalDateTime.now();
		LocalTime nowtime = LocalTime.now();

		LocalTime start = LocalTime.of(9, 0);
		LocalTime end = LocalTime.of(18, 30);

		LocalDateTime planSendTime = now;
		if (nowtime.isBefore(start)) {
			planSendTime = LocalDateTime.of(now.toLocalDate(), start);
		} else if (nowtime.isAfter(end)) {
			planSendTime = LocalDateTime.of(now.toLocalDate(), start).plusDays(1);// 第二天再发
		}
		LocalDateTime overTime = planSendTime.plusDays(remindDays);

		// 通知用户密码即将过期
		long days = LocalDateTimeUtil.between(now, sysUserPassword.getExpireTime(), ChronoUnit.DAYS);
		String daysStr = String.valueOf(days + 1);

		SysUser sysUser = sysUserService.getById(sysUserId);
		String email = sysUser.getEmail();
		if (StringUtils.isNotBlank(email)) {// 发邮件
			SendEmailReq sendEmailReq = new SendEmailReq();
			sendEmailReq.setEmail(email);
			Map<String, String> templateParamMap = Maps.newHashMap();
			templateParamMap.put("days", daysStr);
			sendEmailReq.setTemplateParamMap(templateParamMap);
			sendEmailReq.setType(EmailEnum.Type.REMIND_PASSWORD_EXPIRE);
			sendEmailReq.setPlanSendTime(planSendTime);
			sendEmailReq.setOverTime(overTime);
			emailFeign.send(sendEmailReq);
		}

		String phonenumber = sysUser.getPhonenumber();
		if (StringUtils.isNotBlank(phonenumber)) {// 发短信
			SendSmsReq sendEmailReq = new SendSmsReq();
			sendEmailReq.setMobile(phonenumber);
			Map<String, String> templateParamMap = Maps.newHashMap();
			templateParamMap.put("days", daysStr);
			sendEmailReq.setTemplateParamMap(templateParamMap);
			sendEmailReq.setType(SmsEnum.Type.REMIND_PASSWORD_EXPIRE);
			sendEmailReq.setPlanSendTime(planSendTime);
			sendEmailReq.setOverTime(overTime);
			smsFeign.send(sendEmailReq);
		}
        return RetryerResp.end();
	}
}
