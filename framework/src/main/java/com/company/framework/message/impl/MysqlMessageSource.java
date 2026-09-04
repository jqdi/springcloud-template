package com.company.framework.message.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.company.framework.message.MessageResolver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.AbstractMessageSource;

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
public class MysqlMessageSource extends AbstractMessageSource {
    Map<String, String> map = new HashMap<>();
    {
        map.put("test.hello", "测试消息");
        map.put("test.hello.name", "aaaaaaaaaaaaas {0}");
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        // 通过表查询
        String pattern = map.get(code);
        if (StringUtils.isNotBlank(pattern)) {
            return new MessageFormat(pattern, locale);
        }
        return null;
    }
}
