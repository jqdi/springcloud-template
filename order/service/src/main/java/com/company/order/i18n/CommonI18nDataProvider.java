package com.company.order.i18n;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.company.order.feign.CommonI18nFeign;
import com.company.tool.api.request.CommonI18nReq;
import com.company.tool.api.response.CommonI18nResp;

import io.github.jqdi.i18n.core.metadata.I18nFieldInfo;
import io.github.jqdi.i18n.core.metadata.I18nTableInfo;
import io.github.jqdi.i18n.core.metadata.RelatedI18nValueMapping;
import io.github.jqdi.i18n.core.provider.I18nDataProvider;

/**
 * 通用国际化数据提供者（一张表存储所有的国际化翻译）
 *
 * @author JQ棣
 */
@Component
public class CommonI18nDataProvider implements I18nDataProvider {

    @Autowired
    private CommonI18nFeign commonI18nFeign;

    @Override
    public Map<I18nFieldInfo, List<RelatedI18nValueMapping>> getValueMapping(I18nTableInfo i18nTableInfo,
                                                                             Set<Object> relatedFieldValueSet) {
        String i18nTable = i18nTableInfo.getI18nTable();
        String prefix = i18nTable.replace("_i18n", "");

        List<I18nFieldInfo> i18nFieldInfoList = i18nTableInfo.getI18nFieldInfoList();
        List<Integer> businessIdList = relatedFieldValueSet.stream().filter(Objects::nonNull).map(Object::toString)
                .map(Integer::valueOf).collect(Collectors.toList());

        List<CommonI18nReq.BusinessTypeId> commonI18nParamList = i18nFieldInfoList.stream().map(I18nFieldInfo::getI18nColumn)
                .map(v -> i18nTable.replace("_i18n", "") + "." + v).map(businessType -> {
                    return businessIdList.stream().map(businessId -> {
                        CommonI18nReq.BusinessTypeId commonI18n = new CommonI18nReq.BusinessTypeId();
                        commonI18n.setBusinessType(businessType);
                        commonI18n.setBusinessId(businessId);
                        return commonI18n;
                    }).collect(Collectors.toList());
                }).flatMap(Collection::stream).collect(Collectors.toList());

        CommonI18nReq commonI18nReq = new CommonI18nReq();
        commonI18nReq.setBusinessTypeIdList(commonI18nParamList);
        commonI18nReq.setLocale(LocaleContextHolder.getLocale().toLanguageTag());
        List<CommonI18nResp> commonI18nList = commonI18nFeign.selectByBusinessTypesBusinessIdsLocale(commonI18nReq);
        return i18nFieldInfoList.stream().collect(Collectors.toMap(f -> f, i18nFieldInfo -> {
            List<RelatedI18nValueMapping> relatedI18nValueMappingList =
                    commonI18nList.stream().filter(v -> v.getBusinessType().equals(prefix + "." + i18nFieldInfo.getI18nColumn()))
                            .map(v -> new RelatedI18nValueMapping(v.getBusinessId(), v.getI18nText())).collect(Collectors.toList());
            return relatedI18nValueMappingList;
        }));
    }
}