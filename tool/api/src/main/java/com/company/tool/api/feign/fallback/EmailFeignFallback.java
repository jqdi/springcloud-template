package com.company.tool.api.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.fallback.FallbackUtil;
import com.company.tool.api.feign.EmailFeign;
import com.company.tool.api.request.SendEmailReq;

@Component
public class EmailFeignFallback implements FallbackFactory<EmailFeign> {

	@Override
	public EmailFeign create(final Throwable e) {
		return new EmailFeign() {

			@Override
			public List<Integer> select4PreTimeSend(Integer limit) {
				return Collections.emptyList();// 降级返回空列表
			}

			@Override
			public Void exePreTimeSend(Integer id) {
				return FallbackUtil.create();
			}

			@Override
			public Void send(SendEmailReq sendEmailReq) {
				return FallbackUtil.create();
			}
		};
	}
}
