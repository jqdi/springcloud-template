package com.company.order.api.feign;

import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.company.order.api.constant.Constants;
import com.company.order.api.request.PayCloseReq;
import com.company.order.api.request.PayRefundReq;
import com.company.order.api.request.PayReq;
import com.company.order.api.request.ToPayReq;
import com.company.order.api.response.PayResp;

import org.springframework.cloud.openfeign.FallbackFactory;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/pay", fallbackFactory = ThrowExceptionFallback.class)
public interface PayFeign {

	/**
	 * 统一下单
	 * 
	 * @param payReq
	 * @return 支付结果
	 */
	@PostMapping("/unifiedorder")
	PayResp unifiedorder(@RequestBody PayReq payReq);

	/**
	 * 关闭订单
	 *
	 * @param payCloseReq
	 * @return
	 */
	@PostMapping("/payClose")
	Void payClose(@RequestBody PayCloseReq payCloseReq);
	
	/**
	 * 去支付（可切换支付方式）
	 * 
	 * @param toPayReq
	 * @return 支付结果
	 */
	@PostMapping("/toPay")
	PayResp toPay(@RequestBody ToPayReq toPayReq);

	/**
	 * 退款
	 * 
	 * @param payRefundReq
	 * @return
	 */
	@PostMapping("/refund")
	Void refund(@RequestBody PayRefundReq payRefundReq);
}
