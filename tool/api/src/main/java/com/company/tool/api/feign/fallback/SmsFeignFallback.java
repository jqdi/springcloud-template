package com.company.tool.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.fallback.FallbackUtil;
import com.company.tool.api.feign.SmsFeign;
import com.company.tool.api.request.SendSmsReq;

@Component
public class SmsFeignFallback implements FallbackFactory<SmsFeign> {

	@Override
	public SmsFeign create(final Throwable e) {
		return new SmsFeign() {

			@Override
			public List<Integer> select4PreTimeSend(Integer limit) {
				return Collections.emptyList();// 降级返回空列表
			}

			@Override
			public Void exePreTimeSend(Integer id) {
				return FallbackUtil.create();
			}

			@Override
			public Void send(SendSmsReq sendSmsReq) {
				return FallbackUtil.create();
			}
		};
	}
}
