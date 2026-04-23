package com.company.token.accesscontrol.handler;

public interface UnauthorizedHandler {
    void handle(int code, String msg);
}
