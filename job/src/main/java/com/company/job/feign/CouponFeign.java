package com.company.job.feign;

import com.company.job.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.constant.Constants;
import com.company.user.api.feign.CouponApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/coupon", fallbackFactory = ThrowExceptionFallback.class)
public interface CouponFeign extends CouponApi {
}
