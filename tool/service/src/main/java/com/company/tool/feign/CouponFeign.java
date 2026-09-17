package com.company.tool.feign;

import com.company.tool.constant.Constants;
import com.company.tool.feign.fallback.ThrowExceptionFallback;
import com.company.user.api.feign.CouponApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.USER, path = "/coupon", fallbackFactory = ThrowExceptionFallback.class)
public interface CouponFeign extends CouponApi {
}
