package com.company.user.feign;

import com.company.user.constant.Constants;
import com.company.user.feign.fallback.OrderFeignFallback;
import com.company.order.api.feign.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
