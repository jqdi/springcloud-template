package com.company.token.accesscontrol.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultUnauthorizedHandler implements UnauthorizedHandler {
    @Override
    public void handle(int code, String msg) {
        log.warn("未授权处理:{},{}", code, msg);
    }
}
