package com.company.order.api.feign;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.company.order.api.request.PayCloseReq;
import com.company.order.api.request.PayRefundReq;
import com.company.order.api.request.PayReq;
import com.company.order.api.request.ToPayReq;
import com.company.order.api.response.PayResp;

public interface PayApi {

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