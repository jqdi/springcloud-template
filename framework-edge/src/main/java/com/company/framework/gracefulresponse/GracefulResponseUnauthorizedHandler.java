package com.company.framework.gracefulresponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.token.accesscontrol.handler.UnauthorizedHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "template.enable", name = "access-control", havingValue = "true", matchIfMissing = true)
public class GracefulResponseUnauthorizedHandler implements UnauthorizedHandler {

    @Override
    public void handle(int code, String msg) {
        ExceptionUtil.throwException(String.valueOf(code), msg);
    }
}
