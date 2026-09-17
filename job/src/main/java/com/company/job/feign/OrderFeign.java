package com.company.job.feign;

import com.company.job.constants.Constants;
import com.company.job.feign.fallback.OrderFeignFallback;
import com.company.order.api.feign.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FeignClient.ORDER, path = "/order", fallbackFactory = OrderFeignFallback.class)
public interface OrderFeign extends OrderApi {
}
