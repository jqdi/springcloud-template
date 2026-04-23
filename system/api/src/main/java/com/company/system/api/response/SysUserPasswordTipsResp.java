package com.company.system.api.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SysUserPasswordTipsResp {
	/**
	 * 密码是否可用?
	 */
	private Boolean canUse;

	/**
	 * 密码提醒
	 */
	private String passwordTips;
}
