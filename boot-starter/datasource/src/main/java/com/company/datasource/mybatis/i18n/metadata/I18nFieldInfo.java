package com.company.datasource.mybatis.i18n.metadata;

import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;

import java.lang.reflect.Field;

/**
 * 国际化字段信息（数据库的表名、字段名）
 *
 * @author JQ棣
 */
public class I18nFieldInfo {
    /**
     * 本字段属性
     *
     * @since 3.3.1
     */
    private final Field field;

    /**
     * 国际化表名
     */
    private final String i18nTable;

    /**
     * 国际化字段名
     */
    private final String i18nColumn;
    /**
     * 关联字段名
     */
    private final String i18nRelatedColumn;
    /**
     * 关联值来源属性
     */
    private final Field relatedValueFromField;
    /**
     * 国际化语言编码字段名（存储zh-CN、zh-TW、en-US等语言编码）
     */
    private final String i18nLocaleColumn;
    /**
     * 国际化数据提供者
     */
    private final Class<? extends I18nDataProvider> i18nDataProvider;

    public I18nFieldInfo(Field field, String i18nTable, String i18nColumn, String i18nRelatedColumn, Field relatedValueFromField,
        String i18nLocaleColumn, Class<? extends I18nDataProvider> i18nDataProvider) {
        this.field = field;
        this.i18nTable = i18nTable;
        this.i18nColumn = i18nColumn;
        this.i18nRelatedColumn = i18nRelatedColumn;
        this.relatedValueFromField = relatedValueFromField;
        this.i18nLocaleColumn = i18nLocaleColumn;
        this.i18nDataProvider = i18nDataProvider;
    }

    public Field getField() {
        return field;
    }

    public String getI18nTable() {
        return i18nTable;
    }

    public String getI18nColumn() {
        return i18nColumn;
    }

    public String getI18nRelatedColumn() {
        return i18nRelatedColumn;
    }

    public Field getRelatedValueFromField() {
        return relatedValueFromField;
    }

    public String getI18nLocaleColumn() {
        return i18nLocaleColumn;
    }

    public Class<? extends I18nDataProvider> getI18nDataProvider() {
        return i18nDataProvider;
    }

}
