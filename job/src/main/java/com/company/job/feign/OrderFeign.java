package com.company.job.feign;

import com.company.job.feign.fallback.OrderFeignFallback;
import com.company.order.api.interfaces.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
