package com.company.tool.api.feign;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.response.AppVersionCheckResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/appVersion", fallbackFactory = ThrowExceptionFallback.class)
public interface AppVersionFeign {

    /**
     * 检查
     *
     * @param appCode
     * @param currentVersion
     * @return
     */
    @GetMapping("/check")
    AppVersionCheckResp check(@RequestParam("appCode") String appCode, @RequestParam("currentVersion") String currentVersion);
}
