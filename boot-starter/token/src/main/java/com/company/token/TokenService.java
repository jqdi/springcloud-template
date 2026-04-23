package com.company.token;

public interface TokenService {
    /**
     * 生成token
     *
     * @param tokenParams 账号id + 登录设备类型
     * @return token
     */
    String generate(TokenParams tokenParams);

    /**
     * 失效token
     *
     * @param token token
     * @return tokenParams 账号id + 登录设备类型
     */
    TokenParams invalid(String token);

    /**
     * 检查token
     *
     * @param token token
     * @return tokenParams 账号id + 登录设备类型
     */
    TokenParams checkAndGet(String token);

    /**
     * 获取token名称
     *
     * @return token名称
     */
    String getTokenName();
}
