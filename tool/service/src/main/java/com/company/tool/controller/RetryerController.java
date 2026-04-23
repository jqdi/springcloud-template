package com.company.tool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.feign.RetryerFeign;
import com.company.tool.api.request.RetryerInfoReq;
import com.company.tool.retry.FeignRetryer;
import com.company.tool.retry.RetryerInfo;

@RestController
@RequestMapping(value = "/retryer")
public class RetryerController implements RetryerFeign {

	@Autowired
	private FeignRetryer feignRetryer;

	@Override
	public Void call(@RequestBody RetryerInfoReq req) {
		RetryerInfo retryerInfo = RetryerInfo.builder()
				.feignUrl(req.getFeignUrl()).jsonParams(req.getJsonParams())
				.increaseSeconds(req.getIncreaseSeconds()).maxFailure(req.getMaxFailure())
				.waitStrategy(req.getWaitStrategy()).nextDisposeTime(req.getNextDisposeTime()).build();
		feignRetryer.call(retryerInfo);
		return null;
	}

	@Override
	public List<Integer> selectId4Call() {
		List<Integer> idList = feignRetryer.selectId4CallFail();
		return idList;
	}
	
	@Override
	public Void callById(Integer id) {
		feignRetryer.call(id);
		return null;
	}

}
