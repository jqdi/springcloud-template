package com.company.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface UserOauthEnum {

	@AllArgsConstructor
	enum IdentityType {
		PASSWORD("password", "密码"),// USERNAME,MOBILE,EMAIL都可以结合这个密码使用
		// 账号类型(mobile:手机号,wx-unionid:微信unionid,wx-openid-miniapp:微信小程序openid,wx-openid-mp:微信公众号openid,email:邮箱,username:用户名,sina:新浪微博,qq:QQ)
		USERNAME("username", "用户名"),
		MOBILE("mobile", "手机号"),

		WX_UNIONID("wx-unionid", "微信unionid"),
		WX_OPENID_APP("wx-openid-app", "微信APP openid"),
		WX_OPENID_MINIAPP("wx-openid-miniapp", "微信小程序openid"),
		WX_OPENID_MP("wx-openid-mp", "微信公众号openid"),

		ALI_USERID_MINIAPP("ali-userid-miniapp", "支付宝小程序userId"),

		EMAIL("email", "邮箱"),
		SINA("sina", "新浪微博"),
		QQ("qq", "QQ"),
		;

		@Getter
		private String code;

		@Getter
		private String desc;

		public static IdentityType of(String code) {
			for (IdentityType item : IdentityType.values()) {
				if (item.getCode().equals(code)) {
					return item;
				}
			}
			return null;
		}
	}
}
