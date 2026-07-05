package com.company.framework.globalresponse;

import com.feiniaojin.gracefulresponse.GracefulResponseException;

import lombok.Getter;

/**
 * 参数业务异常
 *
 * @author JQ棣
 */
public class ArgsBusinessException extends GracefulResponseException {

    @Getter
    private final Object[] args;

    protected ArgsBusinessException(String code, String msg, Object... args) {
        super(code, msg);
        this.args = args;
    }

    protected ArgsBusinessException(String msg, Object... args) {
        super(msg);
        this.args = args;
    }
}
