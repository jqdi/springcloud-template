package com.company.framework.globalresponse;

import com.feiniaojin.gracefulresponse.GracefulResponseException;

/**
 * 业务异常（替换为GracefulResponse框架GracefulResponseException处理）
 *
 * @author JQ棣
 */
public class BusinessException extends GracefulResponseException {

    protected BusinessException(String code, String message) {
        super(code, message);
    }

    protected BusinessException(String message) {
        super(null, message);
    }
}
