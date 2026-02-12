package com.company.datasource.mybatis.i18n.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.datasource.mybatis.i18n.provider.I18nDataProviderFactory;

/**
 * 国际化数据提供者工厂（集成Spring）
 *
 * @author JQ棣
 */
public class SpringI18nDataProviderFactory implements I18nDataProviderFactory, BeanFactoryAware {
    private BeanFactory beanFactory;

    private final I18nDataProvider defaultI18nDataProvider;

    public SpringI18nDataProviderFactory(I18nDataProvider defaultI18nDataProvider) {
        this.defaultI18nDataProvider = defaultI18nDataProvider;
    }

    @Override
    public I18nDataProvider newInstance(Class<? extends I18nDataProvider> clazz) {
        if (clazz == null || beanFactory == null) {
            return defaultI18nDataProvider;
        }
        return beanFactory.getBean(clazz);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
