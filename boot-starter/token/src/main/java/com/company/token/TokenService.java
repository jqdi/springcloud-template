package com.company.token;

public interface TokenService {
    /**
     * 生成token
     *
     * @param userId 账号id
     * @param device 登录设备类型
     * @return token
     */
    String generate(String userId, String device);

    /**
     * 失效token
     *
     * @param token token
     * @return 登录设备类型
     */
    String invalid(String token);

    /**
     * 检查token
     *
     * @param token token
     * @return 账号id
     */
    String checkAndGet(String token);

    /**
     * 获取token名称
     *
     * @return token名称
     */
    String getTokenName();
}