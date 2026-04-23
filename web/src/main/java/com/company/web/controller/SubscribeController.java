package com.company.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.WebUtil;
import com.company.tool.api.feign.SubscribeFeign;
import com.company.tool.api.request.SubscribeGrantReq;
import com.company.web.req.GrantReq;

@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

	@Autowired
	private SubscribeFeign subscribeFeign;

	/**
	 * 获取订阅组
	 * 
	 * @param group
	 * @return
	 */
	@GetMapping(value = "/group")
	public List<String> group(String group) {
		return subscribeFeign.selectTemplateCodeByGroup(group);
	}

	/**
	 * 提交授权
	 * 
	 * @param request
	 * @param grantReq
	 * @return
	 */
	@PostMapping(value = "/grant")
	public Void grant(HttpServletRequest request, GrantReq grantReq) {
		SubscribeGrantReq subscribeGrantReq = new SubscribeGrantReq();
		subscribeGrantReq.setGroup(grantReq.getGroup());

		String openid = HeaderContextUtil.deviceid();
		subscribeGrantReq.setOpenid(openid);

		subscribeGrantReq.setResJson(grantReq.getResJson());

		Map<String, String> runtimeAttach = WebUtil.getReqParam(request);
		// 移除GrantReq独立处理的参数
		runtimeAttach.remove("group");
		runtimeAttach.remove("resJson");

		subscribeGrantReq.setRuntimeAttach(runtimeAttach);
		return subscribeFeign.grant(subscribeGrantReq);
	}
}
