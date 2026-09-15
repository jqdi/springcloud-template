package com.company.user.api.feign;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface OpenAccessAccountApi {

	@RequestMapping("/getAppKeyByAppid")
    Map<String, String> getAppKeyByAppid(@RequestParam("appid") String appid);
}