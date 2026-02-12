package com.company.datasource.mybatis.i18n.annotation;

import java.lang.annotation.*;

import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.provider.impl.DefaultI18nDataProvider;

/**
 * 国际化字段注解
 *
 * @author JQ棣
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18nField {
    /**
     * 国际化表名
     */
    String i18nTable();

    /**
     * 国际化字段名（为空则与当前字段同名）
     */
    String i18nColumn();

    /**
     * 关联字段名
     */
    String i18nRelatedColumn();

    /**
     * 关联值来源属性名（当前实体类一定会有该属性名）
     */
    String relatedValueFromField();

    /**
     * 国际化语言编码字段名（存储zh-CN、zh-TW、en-US等语言编码）
     */
    String i18nLocaleColumn() default "locale";

    /**
     * 国际化数据源
     */
    Class<? extends I18nDataProvider> i18nDataProvider() default DefaultI18nDataProvider.class;
}
