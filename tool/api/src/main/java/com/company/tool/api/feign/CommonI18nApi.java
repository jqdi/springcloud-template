package com.company.tool.api.feign;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.tool.api.request.CommonI18nReq;
import com.company.tool.api.response.CommonI18nResp;

public interface CommonI18nApi {

    /**
     * 根据业务类型、业务ID、语言查询(单条)
     *
     * @param businessType
     * @param businessId
     * @param locale
     * @return
     */
    @GetMapping("/selectByBusinessTypeBusinessIdLocale")
    CommonI18nResp selectByBusinessTypeBusinessIdLocale(@RequestParam("businessType") String businessType,
        @RequestParam("businessId") Integer businessId, @RequestParam("locale") String locale);

    /**
     * 根据业务类型、业务ID、语言查询
     *
     * @param commonI18nReq
     * @return
     */
    @PostMapping("/selectByBusinessTypesBusinessIdsLocale")
    List<CommonI18nResp> selectByBusinessTypesBusinessIdsLocale(@RequestBody CommonI18nReq commonI18nReq);

}