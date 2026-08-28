package com.company.framework.message.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.company.framework.message.IMessage;
import com.company.framework.message.MessageResolver;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于 MySQL 表存储（common_i18n）的国际化消息实现。
 *
 * <p>
 * 消息解析优先级：
 * <ol>
 * <li>common_i18n 表：以 code 为 business_type、business_id={@link MessageResolver#GLOBAL_BUSINESS_ID}、locale 查询
 * i18n_text；</li>
 * <li>Spring MessageSource（messages*.properties）：表未命中时回退；</li>
 * <li>code 自身：按 {@link MessageFormat} 格式化作为兜底默认文案。</li>
 * </ol>
 *
 * <p>
 * 约定：
 * <ul>
 * <li>消息编码 code 对应 common_i18n.business_type；</li>
 * <li>全局/系统消息统一使用 {@link MessageResolver#GLOBAL_BUSINESS_ID}（=0）作为 business_id， 不与具体业务实体行（business_id&gt;0）的翻译冲突；</li>
 * <li>locale 使用 {@link Locale#toLanguageTag()}（如 zh-CN、en-US），与 common_i18n.locale 列一致。</li>
 * </ul>
 *
 * <p>
 * DB 查询结果按 {@code code|locale} 维度做本地缓存（含未命中），避免高频调用（如全局异常处理）反复打 DB； 缓存 TTL 到期自动失效，便于运行期更新文案后最终生效。
 *
 * @author JQ棣
 */
@Slf4j
public class MysqlMessage implements IMessage {

    private final IMessage delegate;
    private final MessageResolver messageResolver;

    private final Cache<String, Optional<String>> cache =
        Caffeine.newBuilder().maximumSize(10_000).expireAfterWrite(5, TimeUnit.MINUTES).build();

    public MysqlMessage(IMessage delegate, MessageResolver messageResolver) {
        this.delegate = delegate;
        this.messageResolver = messageResolver;
    }

    @Override
    public String getMessage(String code, Object... args) {
        if (code == null) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String localeTag = locale.toLanguageTag();

        // 1. 优先查 common_i18n 表
        String dbText = resolveFromDb(code, localeTag);
        if (dbText != null) {
            return format(dbText, args, locale);
        }

        // 2. 表未命中，回退到 MessageSource；defaultMessage 兜底为 code 按 MessageFormat 格式化后的结果
        return delegate.getMessage(code, args);
    }

    /**
     * 查询 common_i18n，命中/未命中结果均走本地缓存。
     */
    private String resolveFromDb(String code, String localeTag) {
        try {
            return cache.get(cacheKey(code, localeTag), k -> Optional.ofNullable(messageResolver.resolve(code, localeTag)))
                .orElse(null);
        } catch (Exception e) {
            // DB 异常不阻断主流程，降级到 MessageSource
            log.warn("从 common_i18n 解析消息失败, code={}, locale={}", code, localeTag, e);
            return null;
        }
    }

    private static String cacheKey(String code, String localeTag) {
        return code + "|" + localeTag;
    }

    private static String format(String pattern, Object[] args, Locale locale) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        return new MessageFormat(pattern, locale).format(args);
    }
}
