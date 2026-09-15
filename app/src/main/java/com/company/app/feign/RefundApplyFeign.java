package com.company.app.feign;

import com.company.app.feign.fallback.ThrowExceptionFallback;
import com.company.order.api.constant.Constants;
import com.company.order.api.feign.RefundApplyApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/refundApply", fallbackFactory = ThrowExceptionFallback.class)
public interface RefundApplyFeign extends RefundApplyApi {
}
