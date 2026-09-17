package com.company.app.feign;

import com.company.app.constants.Constants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.RefundApplyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign extends RefundApplyApi {
}
