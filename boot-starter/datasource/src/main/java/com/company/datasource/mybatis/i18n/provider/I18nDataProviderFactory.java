package com.company.datasource.mybatis.i18n.provider;

/**
 * 国际化数据提供者工厂
 *
 * @author JQ棣
 */
public interface I18nDataProviderFactory {
    I18nDataProvider newInstance(Class<? extends I18nDataProvider> clazz);
}
