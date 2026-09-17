package com.company.order.feign;

/**
 * Feign 相关常量
 */
public interface FeignConstants {
    /**
     * 服务名
     */
    String TOOL = "template-tool";
    String SYSTEM = "template-system";
    String USER = "template-user";
    String ORDER = "template-order";

    static String feignUrl(String path) {
        return "http://" + ORDER + path;
    }
}
