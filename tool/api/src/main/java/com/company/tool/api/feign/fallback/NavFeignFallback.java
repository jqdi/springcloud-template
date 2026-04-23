package com.company.tool.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.tool.api.feign.NavFeign;
import com.company.tool.api.request.NavReq;
import com.company.tool.api.response.NavResp;

@Component
public class NavFeignFallback implements FallbackFactory<NavFeign> {

	@Override
	public NavFeign create(final Throwable e) {
		return new NavFeign() {
			@Override
			public List<NavResp> list(NavReq navReq) {
                return Collections.emptyList();// 降级返回空列表
			}
		};
	}
}
