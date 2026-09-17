package com.company.user.feign;

import com.company.user.feign.fallback.OrderFeignFallback;
import com.company.order.api.interfaces.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
