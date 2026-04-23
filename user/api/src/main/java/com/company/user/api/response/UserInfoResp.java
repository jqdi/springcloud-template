package com.company.user.api.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoResp {
	private Integer id;
	private String nickname;
}
