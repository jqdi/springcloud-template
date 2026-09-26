package com.company.framework.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.context.support.AbstractMessageSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于 MySQL 表存储的国际化消息实现
 *
 * @author JQ棣
 */
@Slf4j
public class MysqlMessageSource extends AbstractMessageSource {
    private final MessageSourceResolver messageSourceResolver;
    private final Cache cache;

    public MysqlMessageSource(MessageSourceResolver messageSourceResolver, Cache cache) {
        this.messageSourceResolver = messageSourceResolver;
        this.cache = cache;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        // String pattern = messageSourceResolver.resolve(code, locale);
        // 加入缓存提升性能
        String key = String.format("%s:%s", code, locale.toLanguageTag());
        String pattern = cache.get(key, () -> messageSourceResolver.resolve(code, locale));
        if (StringUtils.isBlank(pattern)) {
            return null;
        }
        return new MessageFormat(pattern, locale);
    }
}
