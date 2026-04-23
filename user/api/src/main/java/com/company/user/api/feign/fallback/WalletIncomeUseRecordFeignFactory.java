package com.company.user.api.feign.fallback;


import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.company.common.fallback.FallbackUtil;
import com.company.user.api.feign.WalletIncomeUseRecordFeign;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用抛异常降级
 */
@Slf4j
@Component
public class WalletIncomeUseRecordFeignFactory implements FallbackFactory<WalletIncomeUseRecordFeign> {

    @Override
    public WalletIncomeUseRecordFeign create(Throwable throwable) {
        return new WalletIncomeUseRecordFeign() {

            @Override
            public List<Integer> selectId4Expire(Integer limit) {
                return Collections.emptyList();// 降级返回空列表
            }

            @Override
            public Boolean update4Expire(Integer id) {
                return FallbackUtil.create();
            }

        };
    }
}
