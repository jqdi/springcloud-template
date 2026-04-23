package com.company.job.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.tool.api.feign.RetryerFeign;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

/**
 * 重试定时器
 */
@Component
public class RetryerService {

	@Autowired
	private RetryerFeign retryerFeign;

	@XxlJob("retryerHandler")
	public ReturnT<String> retryerHandler() throws Exception {
		String param = XxlJobHelper.getJobParam();
		XxlJobHelper.log("param:{}", param);
		List<Integer> idList = null;
		if (StringUtils.isNotBlank(param)) {
			idList = Arrays.asList(param.split(",")).stream().map(String::valueOf).map(Integer::valueOf)
					.collect(Collectors.toList());
		} else {
			idList = retryerFeign.selectId4Call();
		}

		XxlJobHelper.log("size:{}", idList.size());
		for (Integer id : idList) {
			retryerFeign.callById(id);
		}

		return ReturnT.SUCCESS;
	}
}
