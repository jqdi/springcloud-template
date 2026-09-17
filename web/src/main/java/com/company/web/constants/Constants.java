package com.company.web.constants;

public interface Constants {
    /**
     * 服务名
     */
    interface FeignClient {
        String TOOL = "template-tool";
        String SYSTEM = "template-system";
        String USER = "template-user";
        String ORDER = "template-order";
    }

	public interface VerifyCodeType {
		String REGISTER = "register";
		String LOGIN = "login";
	}
}
