package com.company.tool.i18n;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.company.datasource.mybatis.i18n.metadata.I18nFieldInfo;
import com.company.datasource.mybatis.i18n.metadata.RelatedI18nValueMapping;
import com.company.datasource.mybatis.i18n.provider.I18nDataProvider;
import com.company.tool.entity.CommonI18n;
import com.company.tool.service.CommonI18nService;

/**
 * 通用国际化数据提供者（一张表存储所有的国际化翻译）
 *
 * @author JQ棣
 */
@Component
public class CommonI18nDataProvider implements I18nDataProvider {

    @Autowired
    private CommonI18nService commonI18nService;

    @Override
    public List<RelatedI18nValueMapping> getValueMapping(I18nFieldInfo i18nFieldInfo, Set<Object> relatedFieldValueSet) {
        String i18nTable = i18nFieldInfo.getI18nTable();
        String i18nColumn = i18nFieldInfo.getI18nColumn();

        String businessType = i18nTable.replace("_i18n", "") + "." + i18nColumn;
        List<Integer> businessIdList = relatedFieldValueSet.stream().filter(Objects::nonNull).map(Object::toString)
            .map(Integer::valueOf).collect(Collectors.toList());
        List<CommonI18n> commonI18nList = commonI18nService.selectByBusinessTypeBusinessidsLocale(businessType, businessIdList,
            LocaleContextHolder.getLocale().toLanguageTag());
        List<RelatedI18nValueMapping> valueMappingList = commonI18nList.stream()
            .map(v -> new RelatedI18nValueMapping(v.getBusinessId(), v.getI18nText())).collect(Collectors.toList());
        return valueMappingList;
    }
}
