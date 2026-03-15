package com.company.token.jsonwebtoken;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.company.token.TokenParams;
import com.company.token.TokenService;
import com.company.token.jsonwebtoken.util.TokenUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonWebTokenService implements TokenService {
    /**
     * 连接 Token 前缀和 Token 值的字符
     */
    public static final String TOKEN_CONNECTOR_CHAT  = " ";

    // 超时秒数
    private final Integer timeout;
    // 秘钥
    private final String secret;
    // 前缀
    private final String name;
    // 前缀
    private final String prefix;

    public JsonWebTokenService(Integer timeout, String secret, String name, String prefix) {
        this.timeout = timeout;
        this.secret = secret;
        this.name = name;
        this.prefix = prefix;
    }

    @Override
    public String generate(TokenParams tokenParams) {
        String userId = tokenParams.getUserId();
        String device = tokenParams.getDevice();
        int amount = timeout;
        if (timeout == -1) {
            amount = Integer.MAX_VALUE;
        }
        Date expiration = DateUtils.addSeconds(new Date(), amount);
        String token = TokenUtil.generateToken(userId, device, expiration, secret);
        if (StringUtils.isNotBlank(prefix)) {
            token = prefix + TOKEN_CONNECTOR_CHAT + token;
        }
        return token;
    }

    @Override
    public TokenParams invalid(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (StringUtils.isNotBlank(prefix)) {
            if (!token.startsWith(prefix + TOKEN_CONNECTOR_CHAT)) {
                return null;
            }
            token = token.substring(prefix.length() + TOKEN_CONNECTOR_CHAT.length());
        }
        Claims claims = TokenUtil.getClaims(token, secret);
        log.info("claims:{}", claims);

        // do nothing
        if (claims == null) {
            return null;
        }
        String userId = claims.getSubject();
        String device = claims.getAudience();
        return new TokenParams(userId, device);
    }

    @Override
    public TokenParams checkAndGet(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (StringUtils.isNotBlank(prefix)) {
            if (!token.startsWith(prefix + TOKEN_CONNECTOR_CHAT)) {
                return null;
            }
            token = token.substring(prefix.length() + TOKEN_CONNECTOR_CHAT.length());
        }
        String userId = TokenUtil.checkTokenAndGetSubject(token, secret);
        Claims claims = TokenUtil.getClaims(token, secret);
        log.info("claims:{}", claims);
        String device = claims.getAudience();
        return new TokenParams(userId, device);
    }

    @Override
    public String getTokenName() {
        return name;
    }

}
