package com.company.tool.api.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.BannerFeignFallback;
import com.company.tool.api.request.BannerReq;
import com.company.tool.api.response.BannerResp;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/banner", fallbackFactory = BannerFeignFallback.class)
public interface BannerFeign {

	@RequestMapping("/list")
	List<BannerResp> list(@RequestBody BannerReq bannerReq);
}
