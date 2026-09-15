package com.company.job.feign;

import com.company.job.feign.fallback.WalletIncomeUseRecordFeignFactory;
import com.company.user.api.constant.Constants;
import com.company.user.api.feign.WalletIncomeUseRecordApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/walletIncomeUseRecord", fallbackFactory = WalletIncomeUseRecordFeignFactory.class)
public interface WalletIncomeUseRecordFeign extends WalletIncomeUseRecordApi {
}
