package com.company.token.satoken;

import org.apache.commons.lang3.StringUtils;

import com.company.token.TokenService;

import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaTokenService implements TokenService {
    // sa token StpLogic
    private final StpLogic stpLogic;

    public SaTokenService(StpLogic stpLogic) {
        this.stpLogic = stpLogic;
    }

    @Override
    public String generate(String userId, String device) {
        stpLogic.login(userId, device);// 可以做到同端互斥登录
        String token = stpLogic.getTokenValueNotCut();
        log.info("userId:{},device:{},token:{}", userId, device, token);
        return token;
    }

	@Override
	public String invalid(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        SaTokenInfo tokenInfo = stpLogic.getTokenInfo();
        log.info("tokenInfo:{}", tokenInfo);
        try {
            StpUtil.logout();
        } catch (ApiDisabledException e) {
            // log.error("ApiDisabledException", e);
            log.warn("ApiDisabledException:{}", e.getMessage());
        } catch (NotLoginException e) {
            log.error("NotLoginException:{},{},{}", e.getType(), e.getLoginType(), e.getMessage(), e);
            return null;
        }
		return tokenInfo.getLoginDeviceType();
	}

    @Override
    public String checkAndGet(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

		try {
			return stpLogic.getLoginIdAsString();
		} catch (NotLoginException e) {
			log.error("NotLoginException:{},{},{}", e.getType(), e.getLoginType(), e.getMessage(), e);
			return null;
		}
	}

    @Override
    public String getTokenName() {
        return stpLogic.getTokenName();
    }

}
