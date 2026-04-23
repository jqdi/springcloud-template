package com.company.tool.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.JsonUtil;
import com.company.framework.context.HeaderContextUtil;
import com.company.tool.api.feign.NavFeign;
import com.company.tool.api.request.NavReq;
import com.company.tool.api.response.NavResp;
import com.company.tool.nav.NavShowService;
import com.company.tool.nav.dto.NavItemCanShow;

@RestController
@RequestMapping("/nav")
public class NavController implements NavFeign {

	@Autowired
	private NavShowService navShowService;

	@Override
	public List<NavResp> list(@RequestBody NavReq navReq) {
		Map<String, String> runtimeAttach = navReq.getRuntimeAttach();

		// 补充一些请求头参数
		runtimeAttach.putAll(HeaderContextUtil.httpContextHeader());

		List<NavItemCanShow> navList = navShowService.list(navReq.getMaxSize(), runtimeAttach);
		List<NavResp> respList = navList.stream().map(v -> {
			NavResp resp = new NavResp();
			resp.setTitle(v.getTitle());
			resp.setLogo(v.getLogo());
			resp.setType(v.getType());
			resp.setValue(v.getValue());
			String attachJson = v.getAttachJson();
			if (StringUtils.isNotBlank(attachJson)) {
				resp.setAttach(JsonUtil.toJsonNode(attachJson));
			}
			return resp;
		}).collect(Collectors.toList());

		return respList;
	}
}
