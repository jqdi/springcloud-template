package com.company.user.feign;

import com.company.user.constant.Constants;
import com.company.user.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.feign.RefundApplyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign extends RefundApplyApi {
}
