package com.company.web.feign.client;

import com.company.web.feign.FeignConstants;
import com.company.web.feign.fallback.OrderFeignFallback;
import com.company.order.api.interfaces.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
