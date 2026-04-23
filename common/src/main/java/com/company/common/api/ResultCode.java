package com.company.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResultCode {
    /* 框架错误码 */
    SUCCESS("0", "成功"), //
    FAIL("1", "失败"), //
    API_FUSING("503", "系统繁忙，请稍后再试"), // API熔断

    /* 遵循 HTTP 状态码语义 */
    UNAUTHORIZED("401", "未授权，请登录"), // 未认证（需登录）
    SYSTEM_ERROR("500", "系统错误"), // 服务端错误
    ;

    @Getter
    private String code;
    @Getter
    private String message;

    public static ResultCode of(String code) {
        for (ResultCode item : ResultCode.values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
