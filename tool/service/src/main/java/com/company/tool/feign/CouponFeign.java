package com.company.tool.feign;

import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.interfaces.CouponApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/coupon", fallbackFactory = ThrowExceptionFallback.class)
public interface CouponFeign extends CouponApi {
}
