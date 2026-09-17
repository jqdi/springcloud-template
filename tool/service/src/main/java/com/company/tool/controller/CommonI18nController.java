package com.company.tool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.framework.util.PropertyUtils;
import com.company.tool.api.interfaces.CommonI18nApi;
import com.company.tool.api.request.CommonI18nReq;
import com.company.tool.api.response.CommonI18nResp;
import com.company.tool.entity.CommonI18n;
import com.company.tool.service.CommonI18nService;

@RestController
@RequestMapping(value = "/commonI18n")
public class CommonI18nController implements CommonI18nApi {
    @Autowired
    private CommonI18nService commonI18nService;

    @Override
    public CommonI18nResp selectByBusinessTypeBusinessIdLocale(String businessType, Integer businessId, String locale) {
        return PropertyUtils.copyProperties(
            commonI18nService.selectByBusinessTypeBusinessIdLocale(businessType, businessId, locale), CommonI18nResp.class);
    }

    @Override
    public List<CommonI18nResp> selectByBusinessTypesBusinessIdsLocale(@RequestBody CommonI18nReq commonI18nReq) {
        List<CommonI18nReq.BusinessTypeId> businessTypeIdList = commonI18nReq.getBusinessTypeIdList();
        List<CommonI18n> commonI18nList = PropertyUtils.copyArrayProperties(businessTypeIdList, CommonI18n.class);
        String locale = commonI18nReq.getLocale();
        List<CommonI18n> commonI18ns = commonI18nService.selectByBusinessTypesBusinessIdsLocale(commonI18nList, locale);
        return PropertyUtils.copyArrayProperties(commonI18ns, CommonI18nResp.class);
    }

}