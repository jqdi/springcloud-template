package com.company.datasource.mybatis.i18n.metadata;

/**
 * 关联字段-国际化字段 值映射
 *
 * @author JQ棣
 */
public class RelatedI18nValueMapping {
    /**
     * 关联字段值
     */
    private final Object i18nRelatedValue;

    /**
     * 国际化字段值
     */
    private final Object i18nValue;

    public RelatedI18nValueMapping(Object i18nRelatedValue, Object i18nValue) {
        this.i18nRelatedValue = i18nRelatedValue;
        this.i18nValue = i18nValue;
    }

    public Object getI18nRelatedValue() {
        return i18nRelatedValue;
    }

    public Object getI18nValue() {
        return i18nValue;
    }
}
