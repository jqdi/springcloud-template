package com.company.datasource.mybatis.i18n.provider.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.company.datasource.mybatis.i18n.metadata.I18nFieldInfo;
import com.company.datasource.mybatis.i18n.metadata.RelatedI18nValueMapping;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;

/**
 * 国际化数据提供者（默认）
 *
 * @author JQ棣
 */
public class DefaultI18nDataProvider implements I18nDataProvider {

    public DefaultI18nDataProvider() {}

    @Override
    public List<RelatedI18nValueMapping> getValueMapping(I18nFieldInfo i18nFieldInfo, Set<Object> relatedFieldValueSet) {
        return Collections.emptyList();
    }
}
