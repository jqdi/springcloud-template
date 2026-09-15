package com.company.web.feign.fallback;

import java.util.Collections;
import java.util.List;

import com.company.web.feign.CommonI18nFeign;
import com.company.tool.api.request.CommonI18nReq;
import com.company.tool.api.response.CommonI18nResp;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CommonI18nFeignFallback implements FallbackFactory<CommonI18nFeign> {

    @Override
    public CommonI18nFeign create(final Throwable e) {
        return new CommonI18nFeign() {
            @Override
            public CommonI18nResp selectByBusinessTypeBusinessIdLocale(String businessType, Integer businessId, String locale) {
                return null;// 降级返回null
            }

            @Override
            public List<CommonI18nResp> selectByBusinessTypesBusinessIdsLocale(CommonI18nReq commonI18nReq) {
                return Collections.emptyList();// 降级返回空列表
            }
        };
    }
}