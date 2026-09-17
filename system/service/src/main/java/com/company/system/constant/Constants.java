package com.company.system.constant;

/**
 * 常量
 */
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

    static String feignUrl(String path) {
        return "http://" + FeignClient.SYSTEM + path;
    }

	/** 管理员角色权限标识 */
    String SUPER_ROLE = "admin";
}
