package com.company.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.company.token.jsonwebtoken.JsonWebTokenService;
import com.company.token.satoken.SaTokenService;

import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpLogic;

//@Configuration 使用org.springframework.boot.autoconfigure.AutoConfiguration.imports装配bean
public class TokenAutoConfiguration {

//    @Bean
    @ConditionalOnMissingBean
    public TokenService tokenService(@Value("${token.timeout:2592000}") Integer timeout,
        @Value("${token.secret:defaultsecret}") String secret, @Value("${token.name:}") String name, @Value("${token.prefix:}") String prefix) {
        TokenService tokenService = new JsonWebTokenService(timeout, secret, name, prefix);
        return tokenService;
    }

    // Sa-Token 整合 jwt (Style模式)
    @Bean // 仅使用SaToken需要
    public StpLogic getStpLogicJwt() {
        // https://sa-token.dev33.cn/doc/index.html#/plugin/jwt-extend
//		return new StpLogicJwtForSimple();// Token风格替换，数据记录到redis
//		return new StpLogicJwtForMixin();// jwt 与 Redis 逻辑混合
        return new StpLogicJwtForStateless();// 完全舍弃Redis，只用jwt
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenService tokenService(StpLogic stpLogic) {
        TokenService tokenService = new SaTokenService(stpLogic);
        return tokenService;
	}

}