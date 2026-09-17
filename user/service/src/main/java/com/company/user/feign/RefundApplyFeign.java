package com.company.user.feign;

import com.company.user.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.RefundApplyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign extends RefundApplyApi {
}
