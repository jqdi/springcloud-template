package com.company.tool.api.interfaces;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import com.company.tool.api.request.BannerReq;
import com.company.tool.api.response.BannerResp;

public interface BannerApi {

	@RequestMapping("/list")
	List<BannerResp> list(@RequestBody BannerReq bannerReq);
}