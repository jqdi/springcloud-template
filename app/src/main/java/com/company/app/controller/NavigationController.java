package com.company.app.controller;


import com.company.framework.context.HeaderContextUtil;
import com.company.framework.context.HttpContextUtil;
import com.company.framework.util.PropertyUtils;
import com.company.framework.util.WebUtil;
import com.company.tool.api.feign.BannerFeign;
import com.company.tool.api.feign.NavFeign;
import com.company.tool.api.request.BannerReq;
import com.company.tool.api.request.NavReq;
import com.company.tool.api.response.BannerResp;
import com.company.tool.api.response.NavResp;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.feign.UserOauthFeign;
import com.company.user.api.response.UserOauthResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 导航栏API
 *
 * @author JQ棣
 */
@RestController
@RequestMapping("/navigation")
public class NavigationController {

	@Autowired
	private NavFeign navFeign;
	@Autowired
	private BannerFeign bannerFeign;
	@Autowired
	private UserOauthFeign userOauthFeign;

	/**
	 * 金刚位列表
	 */
	@GetMapping("/nav")
	public List<com.company.app.resp.NavResp> nav(HttpServletRequest request) {

		NavReq navReq = new NavReq();

		Map<String, String> runtimeAttach = WebUtil.getReqParam(request);

        /* 补充一些系统可自动获取的参数 */
        Map<String, String> headerMap = HeaderContextUtil.headerMap();
        runtimeAttach.putAll(headerMap);

		/* 补充一些展示替换的参数 */

		// 补充用户id
		String userId = HeaderContextUtil.currentUserId();
		if (StringUtils.isBlank(userId)) {
			String deviceid = HeaderContextUtil.deviceid();
			if (StringUtils.isNotBlank(deviceid)) {
				UserOauthResp userOauthResp = userOauthFeign
						.selectOauth(UserOauthEnum.IdentityType.WX_OPENID_MINIAPP, deviceid);
				if (userOauthResp != null) {
					userId = String.valueOf(userOauthResp.getUserId());
				}
			}
		}
		runtimeAttach.put("userId", userId);

		navReq.setRuntimeAttach(runtimeAttach);
		navReq.setMaxSize(4);

		List<NavResp> navRespList = navFeign.list(navReq);
		List<com.company.app.resp.NavResp> respList = PropertyUtils.copyArrayProperties(navRespList, com.company.app.resp.NavResp.class);
		return respList;
	}

	/**
	 * 轮播图列表
	 */
	@GetMapping("/banner")
	public List<com.company.app.resp.BannerResp> banner(HttpServletRequest request) {

		BannerReq bannerReq = new BannerReq();

		Map<String, String> runtimeAttach = WebUtil.getReqParam(request);

        /* 补充一些系统可自动获取的参数 */
        Map<String, String> headerMap = HeaderContextUtil.headerMap();
        runtimeAttach.putAll(headerMap);

		/* 补充一些展示替换的参数 */

		// 补充用户id
		String userId = HeaderContextUtil.currentUserId();
		if (StringUtils.isBlank(userId)) {
			String deviceid = HeaderContextUtil.deviceid();
			if (StringUtils.isNotBlank(deviceid)) {
				UserOauthResp userOauthResp = userOauthFeign
						.selectOauth(UserOauthEnum.IdentityType.WX_OPENID_MINIAPP, deviceid);
				if (userOauthResp != null) {
					userId = String.valueOf(userOauthResp.getUserId());
				}
			}
		}
		runtimeAttach.put("userId", userId);

		bannerReq.setRuntimeAttach(runtimeAttach);

		List<BannerResp> bannerRespList = bannerFeign.list(bannerReq);
		List<com.company.app.resp.BannerResp> respList = PropertyUtils.copyArrayProperties(bannerRespList, com.company.app.resp.BannerResp.class);
		return respList;
	}
}
