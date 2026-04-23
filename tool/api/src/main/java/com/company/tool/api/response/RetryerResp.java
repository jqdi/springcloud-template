package com.company.tool.api.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/**
 * 重试结果
 * 所有feign重试必须返回该实体类
 */
@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RetryerResp {
    /**
     * 是否有结果（必填）
     */
    Boolean hasResult;

    // 以下字段hasResult=true 时，有值
    /**
     * 备注
     */
    String remark;

    /**
     * 结束重试
     */
    public static RetryerResp end() {
        return new RetryerResp().setHasResult(true);
    }

    /**
     * 结束重试
     */
    public static RetryerResp end(String remark) {
        return end().setRemark(remark);
    }

    /**
     * 继续重试
     */
    public static RetryerResp goon() {
        return new RetryerResp().setHasResult(false);
    }

    /**
     * 继续重试
     */
    public static RetryerResp goon(String remark) {
        return goon().setRemark(remark);
    }

}
