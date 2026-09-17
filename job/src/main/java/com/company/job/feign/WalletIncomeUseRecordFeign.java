package com.company.job.feign;

import com.company.job.feign.fallback.WalletIncomeUseRecordFeignFactory;
import com.company.user.api.feign.WalletIncomeUseRecordApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/walletIncomeUseRecord", fallbackFactory = WalletIncomeUseRecordFeignFactory.class)
public interface WalletIncomeUseRecordFeign extends WalletIncomeUseRecordApi {
}
