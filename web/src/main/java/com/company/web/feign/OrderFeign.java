package com.company.web.feign;

import com.company.web.feign.fallback.OrderFeignFallback;
import com.company.order.api.constant.Constants;
import com.company.order.api.feign.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
