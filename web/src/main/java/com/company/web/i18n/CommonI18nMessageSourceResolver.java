package com.company.web.i18n;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.framework.i18n.MessageSourceResolver;
import com.company.web.feign.CommonI18nFeign;
import com.company.tool.api.response.CommonI18nResp;

/**
 * 基于 common_i18n 表的消息解析器
 *
 * @author JQ棣
 */
@Component
public class CommonI18nMessageSourceResolver implements MessageSourceResolver {

    /** 全局/系统消息在 common_i18n 中的 business_id 占位值（不绑定具体业务实体行） */
    private static final int GLOBAL_BUSINESS_ID = 0;

    @Autowired
    private CommonI18nFeign commonI18nFeign;

    @Override
    public String resolve(String code, Locale locale) {
        CommonI18nResp commonI18n =
            commonI18nFeign.selectByBusinessTypeBusinessIdLocale(code, GLOBAL_BUSINESS_ID, locale.toLanguageTag());
        if (commonI18n == null) {
            return null;
        }
        return commonI18n.getI18nText();
    }
}