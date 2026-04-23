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
}
