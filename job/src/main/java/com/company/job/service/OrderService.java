package com.company.job.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.order.api.feign.OrderFeign;
import com.company.order.api.request.OrderFinishReq;
import com.company.order.api.request.OrderReceiveReq;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

@Component
public class OrderService {

	@Autowired
	private OrderFeign orderFeign;
	
	/**
	 * 一段时间内没有收货则自动变为已收货
	 * 
	 * @param param
	 * @return
	 */
	@XxlJob("sendSuccess2ReceiveHandler")
	public ReturnT<String> sendSuccess2ReceiveHandler() {
		String param = XxlJobHelper.getJobParam();
		XxlJobHelper.log("param:{}", param);
		List<String> orderCodeList = null;
		if (StringUtils.isNotBlank(param)) {
			orderCodeList = Arrays.asList(param.split(",")).stream().collect(Collectors.toList());
			
			XxlJobHelper.log("size:{}", orderCodeList.size());
			for (String orderCode : orderCodeList) {
				OrderReceiveReq orderReviewReq = new OrderReceiveReq();
				orderReviewReq.setOrderCode(orderCode);
				orderReviewReq.setFinishTime(LocalDateTime.now());
				orderFeign.receive(orderReviewReq);
			}
			return ReturnT.SUCCESS;
		}
		
		int limit = 1000;
		do {
			orderCodeList = orderFeign.select4OverSendSuccess(limit);
			
			XxlJobHelper.log("size:{}", orderCodeList.size());
			for (String orderCode : orderCodeList) {
				OrderReceiveReq orderReviewReq = new OrderReceiveReq();
				orderReviewReq.setOrderCode(orderCode);
				orderReviewReq.setFinishTime(LocalDateTime.now());
				orderFeign.receive(orderReviewReq);
			}
		} while (orderCodeList.size() == limit);
		
		return ReturnT.SUCCESS;
	}

	/**
	 * 一段时间内没有评价则自动变为已结束
	 * 
	 * @param param
	 * @return
	 */
	@XxlJob("waitReview2completeHandler")
	public ReturnT<String> waitReview2completeHandler() {
		String param = XxlJobHelper.getJobParam();
		XxlJobHelper.log("param:{}", param);
		List<String> orderCodeList = null;
		if (StringUtils.isNotBlank(param)) {
			orderCodeList = Arrays.asList(param.split(",")).stream().collect(Collectors.toList());

			XxlJobHelper.log("size:{}", orderCodeList.size());
			for (String orderCode : orderCodeList) {
				OrderFinishReq orderFinishReq = new OrderFinishReq();
				orderFinishReq.setOrderCode(orderCode);
				orderFinishReq.setFinishTime(LocalDateTime.now());
				orderFeign.finish(orderFinishReq);
			}
			return ReturnT.SUCCESS;
		}

		int limit = 1000;
		do {
			orderCodeList = orderFeign.select4OverWaitReview(limit);

			XxlJobHelper.log("size:{}", orderCodeList.size());
			for (String orderCode : orderCodeList) {
				OrderFinishReq orderFinishReq = new OrderFinishReq();
				orderFinishReq.setOrderCode(orderCode);
				orderFinishReq.setFinishTime(LocalDateTime.now());
				orderFeign.finish(orderFinishReq);
			}
		} while (orderCodeList.size() == limit);

		return ReturnT.SUCCESS;
	}
}
