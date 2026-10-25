package com.company.job.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.job.feign.client.AppVersionFeign;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

/**
 * APP版本灰度统计任务
 */
@Slf4j
@Component
public class AppVersionUpgradeCountJob {

    @Autowired
    private AppVersionFeign appVersionFeign;

    /**
     * 统计所有灰度中应用的最新版本已升级设备数
     */
    @XxlJob("appVersionUpgradeCountHandler")
    public ReturnT<String> appVersionUpgradeCountHandler() {
        XxlJobHelper.log("开始统计APP版本灰度升级量");
        Boolean result = appVersionFeign.statAllUpgradedDevices();
        XxlJobHelper.log("统计结束,result:{}", result);
        return ReturnT.SUCCESS;
    }
}
