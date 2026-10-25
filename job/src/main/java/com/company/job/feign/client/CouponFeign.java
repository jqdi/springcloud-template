package com.company.job.feign.client;

import com.company.job.feign.FeignConstants;
import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.interfaces.CouponApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/coupon", fallbackFactory = ThrowExceptionFallback.class)
public interface CouponFeign extends CouponApi {
}
