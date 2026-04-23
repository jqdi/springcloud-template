package com.company.common.fallback;

import com.company.common.api.ResultCode;
import com.company.common.exception.ResultException;

/**
 * 降级工具类
 *
 * @author JQ棣
 */
public class FallbackUtil {

    public static <T> T create() {
        throw new ResultException(ResultCode.API_FUSING.getCode(), ResultCode.API_FUSING.getMessage());
    }
}
