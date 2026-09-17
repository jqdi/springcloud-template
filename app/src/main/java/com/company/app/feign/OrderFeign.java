package com.company.app.feign;

import com.company.app.feign.fallback.OrderFeignFallback;
import com.company.order.api.feign.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
