package com.company.tool.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.tool.api.feign.BannerFeign;
import com.company.tool.api.request.BannerReq;
import com.company.tool.api.response.BannerResp;

@Component
public class BannerFeignFallback implements FallbackFactory<BannerFeign> {

	@Override
	public BannerFeign create(final Throwable e) {
		return new BannerFeign() {
			@Override
			public List<BannerResp> list(BannerReq bannerReq) {
                return Collections.emptyList();// 降级返回空列表
			}
		};
	}
}
