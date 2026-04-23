package com.company.token;

/**
 * Token参数类
 */
public class TokenParams {
    private final String userId;
    private final String device;

    public TokenParams(String userId, String device) {
        this.userId = userId;
        this.device = device;
    }

    public String getUserId() {
        return userId;
    }

    public String getDevice() {
        return device;
    }
}
