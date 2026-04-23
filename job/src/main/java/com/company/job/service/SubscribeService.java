package com.company.job.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.tool.api.feign.SubscribeFeign;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

@Component
public class SubscribeService {

	@Autowired
	private SubscribeFeign subscribeFeign;

	@XxlJob("subscribeHandler")
	public ReturnT<String> subscribeHandler() {
		String param = XxlJobHelper.getJobParam();
		XxlJobHelper.log("param:{}", param);
		List<Integer> idList = null;
		if (StringUtils.isNotBlank(param)) {
			idList = Arrays.asList(param.split(",")).stream().map(String::valueOf).map(Integer::valueOf)
					.collect(Collectors.toList());

			XxlJobHelper.log("size:{}", idList.size());
			for (Integer id : idList) {
				subscribeFeign.exePreTimeSend(id);
			}
			return ReturnT.SUCCESS;
		}

		int limit = 1000;
		do {
			idList = subscribeFeign.select4PreTimeSend(limit);

			XxlJobHelper.log("size:{}", idList.size());
			for (Integer id : idList) {
				subscribeFeign.exePreTimeSend(id);
			}
		} while (idList.size() == limit);

		return ReturnT.SUCCESS;
	}
	
	@XxlJob("syncTemplateHandler")
	public ReturnT<String> syncTemplateHandler(String param) {
		XxlJobHelper.log("param:{}", param);
		subscribeFeign.syncTemplate();
		return ReturnT.SUCCESS;
	}
}
