package com.company.tool.service;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.tool.entity.CommonI18n;
import com.company.tool.mapper.CommonI18nMapper;

import java.util.Collections;
import java.util.List;

@Service
public class CommonI18nService extends ServiceImpl<CommonI18nMapper, CommonI18n> {

    public List<CommonI18n> selectByBusinessTypeBusinessidsLocale(String businessType, List<Integer> businessTypeIdList,
        String locale) {
        if (CollectionUtils.isEmpty(businessTypeIdList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectByBusinessTypeBusinessidsLocale(businessType, businessTypeIdList, locale);
    }

    public List<CommonI18n> selectByBusinessTypesBusinessIdsLocale(List<CommonI18n> commonI18nList, String locale) {
        if (CollectionUtils.isEmpty(commonI18nList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectByBusinessTypesBusinessIdsLocale(commonI18nList, locale);
    }

    /**
     * 根据 业务类型(business_type) + 业务ID + locale 查询单条国际化文案。
     * <p>
     * 用于全局/系统消息场景：消息编码 code 作为 business_type，business_id 固定为 0
     * （见 {@link com.company.framework.message.MessageResolver#GLOBAL_BUSINESS_ID}）。
     *
     * @param businessType 业务类型（消息编码）
     * @param businessId  业务ID（全局消息传 0）
     * @param locale      地区编码，如 zh-CN、en-US
     * @return 命中的国际化记录；未命中返回 null
     */
    public CommonI18n selectByBusinessTypeBusinessIdLocale(String businessType, Integer businessId, String locale) {
        return this.lambdaQuery()
            .select(CommonI18n::getI18nText)
            .eq(CommonI18n::getBusinessType, businessType)
            .eq(CommonI18n::getBusinessId, businessId)
            .eq(CommonI18n::getLocale, locale)
            .one();
    }
}
