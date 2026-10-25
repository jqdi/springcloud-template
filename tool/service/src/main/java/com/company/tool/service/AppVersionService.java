package com.company.tool.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.tool.constant.Constants;
import com.company.tool.entity.AppInfo;
import com.company.tool.entity.AppVersion;
import com.company.tool.feign.client.DeviceFeign;
import com.company.tool.mapper.AppInfoMapper;
import com.company.tool.mapper.AppVersionMapper;

@Service
public class AppVersionService extends ServiceImpl<AppVersionMapper, AppVersion> {

    @Autowired
    private AppInfoMapper appInfoMapper;
    @Autowired
    private DeviceFeign deviceFeign;

    public AppVersion selectLastFullByAppCode(String appCode) {
        return baseMapper.selectLastFullByAppCode(appCode);
    }

    public List<AppVersion> selectByAppCodeGeId(String appCode, Integer fullId) {
        return baseMapper.selectByAppCodeGeId(appCode, fullId);
    }

    @Cacheable(value = Constants.CacheName.APP_VERSION, key = "#appCode")
    public AppVersion selectLastFullByAppCodeCache(String appCode) {
        return this.selectLastFullByAppCode(appCode);
    }

    @CacheEvict(value = Constants.CacheName.APP_VERSION, key = "#appCode")
    public void delLastFullByAppCodeCache(String appCode) {
    }

    /**
     * 统计所有应用的灰度中已升级设备数
     */
    public Boolean statAllUpgradedDevices() {
        List<AppInfo> appInfoList = appInfoMapper.selectList(null);
        if (CollectionUtils.isEmpty(appInfoList)) {
            return true;
        }
        for (AppInfo appInfo : appInfoList) {
            String appCode = appInfo.getAppCode();

            // 取最近1条全量版本记录
            AppVersion lastFullVersion = selectLastFullByAppCode(appCode);
            Integer lastFullVersionId = lastFullVersion != null ? lastFullVersion.getId() : 0;
            /*
             * 可能会存在多个灰度版本，都要更新
             */
            // 取最近1条全量版本及之后的数据
            List<AppVersion> appVersionList = selectByAppCodeGeId(appCode, lastFullVersionId);
            for (AppVersion appVersion : appVersionList) {
                if (appVersion.getGrayPercent().compareTo(BigDecimal.ONE) < 0) {
                    continue;
                }
                Long updateCount = deviceFeign.deviceCountUpgraded(appCode, appVersion.getVersion());
                AppVersion entity = new AppVersion();
                entity.setId(appVersion.getId());
                entity.setUpdateCount(updateCount == null ? 0L : updateCount);
                updateById(entity);
            }
        }
        return true;
    }
}
