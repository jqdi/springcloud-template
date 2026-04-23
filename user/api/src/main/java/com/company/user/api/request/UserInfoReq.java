package com.company.user.api.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.company.user.api.enums.UserOauthEnum;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoReq {
	@NotNull(message = "账号类型不能为空")
	private UserOauthEnum.IdentityType identityType;

	@NotBlank(message = "账号不能为空")
	private String identifier;

	private String certificate;
	private String password;

	private String nickname;
	private String avatar;
}
