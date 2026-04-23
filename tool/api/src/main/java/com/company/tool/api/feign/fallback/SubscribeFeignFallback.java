package com.company.tool.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.fallback.FallbackUtil;
import com.company.tool.api.feign.SubscribeFeign;
import com.company.tool.api.request.SubscribeGrantReq;
import com.company.tool.api.request.SubscribeSendReq;

@Component
public class SubscribeFeignFallback implements FallbackFactory<SubscribeFeign> {

	@Override
	public SubscribeFeign create(final Throwable e) {
		return new SubscribeFeign() {

			@Override
			public List<String> selectTemplateCodeByGroup(String group) {
				return FallbackUtil.create();
			}

			@Override
			public Void grant(SubscribeGrantReq subscribeGrantReq) {
				return FallbackUtil.create();
			}

			@Override
			public Void send(SubscribeSendReq subscribeSendReq) {
				return FallbackUtil.create();
			}

			@Override
			public List<Integer> select4PreTimeSend(Integer limit) {
				return Collections.emptyList();// 降级返回空列表
			}

			@Override
			public Void exePreTimeSend(Integer id) {
				return FallbackUtil.create();
			}

			@Override
			public Void syncTemplate() {
				return FallbackUtil.create();
			}

		};
	}
}
