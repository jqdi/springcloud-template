package com.company.app.feign.client;

import com.company.app.feign.FeignConstants;
import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.interfaces.RefundApplyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign extends RefundApplyApi {
}
