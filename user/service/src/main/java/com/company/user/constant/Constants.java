package com.company.user.constant;

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
        return "http://" + FeignClient.USER + path;
    }
}
