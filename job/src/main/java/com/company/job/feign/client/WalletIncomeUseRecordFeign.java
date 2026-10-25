package com.company.job.feign.client;

import com.company.job.feign.FeignConstants;
import com.company.job.feign.fallback.WalletIncomeUseRecordFeignFactory;
import com.company.user.api.interfaces.WalletIncomeUseRecordApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = FeignConstants.USER, path = "/walletIncomeUseRecord", fallbackFactory = WalletIncomeUseRecordFeignFactory.class)
public interface WalletIncomeUseRecordFeign extends WalletIncomeUseRecordApi {
}
