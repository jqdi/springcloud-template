package com.company.user.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface UserClientEnum {

	@AllArgsConstructor
	enum ClientType {
        // 客户端类型(MANAGE:管理端,USER:用户端,MERCHANT:商户端,RIDER:骑手端)
        MANAGE("MANAGE", "管理端"),
        USER("USER", "用户端"),
        MERCHANT("MERCHANT", "商户端"),
        RIDER("RIDER", "骑手端"),
		;

		@Getter
		private String code;

		@Getter
		private String desc;

		public static ClientType of(String code) {
			for (ClientType item : ClientType.values()) {
				if (item.getCode().equals(code)) {
					return item;
				}
			}
			return null;
		}
	}
}
