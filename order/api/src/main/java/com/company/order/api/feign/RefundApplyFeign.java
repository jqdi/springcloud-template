package com.company.order.api.feign;


import com.company.order.api.constant.Constants;
import com.company.order.api.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.request.PayRefundApplyReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign {

	/**
	 * 退款申请
	 * 
	 * @param payRefundApplyReq
	 * @return
	 */
	@PostMapping("/refundApply")
	Integer refundApply(@RequestBody PayRefundApplyReq payRefundApplyReq);

	@GetMapping("/selectId4Deal")
	List<Integer> selectId4Deal();

	/**
	 * 处理退款申请
	 * 
	 * <pre>
	 * 申请结果或退款结果会以广播方式通知到各业务
	 * </pre>
	 * 
	 * @param id
	 * @return
	 */
	@PostMapping("/dealRefundApply")
	Boolean dealRefundApply(@RequestParam("id") Integer id);
}
