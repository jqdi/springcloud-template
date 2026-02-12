package com.company.datasource.mybatis.i18n.provider.impl;

import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.provider.I18nDataProviderFactory;

/**
 * 国际化数据提供者工厂（默认）
 *
 * @author JQ棣
 */
public class DefaultI18nDataProviderFactory implements I18nDataProviderFactory {

    private final I18nDataProvider defaultI18nDataProvider;

    public DefaultI18nDataProviderFactory(I18nDataProvider defaultI18nDataProvider) {
        this.defaultI18nDataProvider = defaultI18nDataProvider;
    }

    @Override
    public I18nDataProvider newInstance(Class<? extends I18nDataProvider> clazz) {
        if (clazz == null) {
            return defaultI18nDataProvider;
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
