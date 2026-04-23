package com.company.tool.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.context.HeaderContextUtil;
import com.company.tool.api.feign.BannerFeign;
import com.company.tool.api.request.BannerReq;
import com.company.tool.api.response.BannerResp;
import com.company.tool.banner.BannerShowService;
import com.company.tool.banner.dto.BannerCanShow;

@RestController
@RequestMapping("/banner")
public class BannerController implements BannerFeign {

	@Autowired
	private BannerShowService bannerShowService;

	@Override
	public List<BannerResp> list(@RequestBody BannerReq bannerReq) {
		Map<String, String> runtimeAttach = bannerReq.getRuntimeAttach();

		// 补充一些请求头参数
		runtimeAttach.putAll(HeaderContextUtil.httpContextHeader());

		List<BannerCanShow> bannerList = bannerShowService.list(runtimeAttach);
		List<BannerResp> respList = bannerList.stream().map(v -> {
			BannerResp resp = new BannerResp();
			resp.setTitle(v.getTitle());
			resp.setImage(v.getImage());
			resp.setType(v.getType());
			resp.setValue(v.getValue());
			return resp;
		}).collect(Collectors.toList());

		return respList;
	}
}
