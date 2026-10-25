package com.company.tool.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.company.common.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.tool.api.interfaces.AppVersionApi;
import com.company.tool.api.request.AppVersionReq;
import com.company.tool.api.response.AppVersionCheckResp;
import com.company.tool.api.response.AppVersionResp;
import com.company.tool.entity.AppVersion;
import com.company.tool.feign.client.DeviceFeign;
import com.company.tool.service.AppVersionService;

import cn.hutool.core.text.CharSequenceUtil;

@RestController
@RequestMapping(value = "/appVersion")
public class AppVersionController implements AppVersionApi {
    @Autowired
    private AppVersionService appVersionService;
    @Autowired
    private DeviceFeign deviceFeign;

    private LambdaQueryWrapper<AppVersion> toQueryWrapper(String appCode, String version) {
        LambdaQueryWrapper<AppVersion> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(appCode)) {
            wrapper.eq(AppVersion::getAppCode, appCode);
        }
        if (StringUtils.isNotBlank(version)) {
            wrapper.like(AppVersion::getVersion, version);
        }
        return wrapper;
    }

    @Override
    public PageResp<AppVersionResp> page(Long current, Long size, String appCode, String version) {
        LambdaQueryWrapper<AppVersion> queryWrapper = toQueryWrapper(appCode, version);

        long count = appVersionService.count(queryWrapper);

        queryWrapper.orderByDesc(AppVersion::getId);
        List<AppVersion> list = appVersionService.list(PageDTO.of(current, size), queryWrapper);

        List<AppVersionResp> respList = PropertyUtils.copyArrayProperties(list, AppVersionResp.class);
        return PageResp.of(count, respList);
    }

    @Override
    public List<AppVersionResp> list(String appCode, String version) {
        LambdaQueryWrapper<AppVersion> queryWrapper = toQueryWrapper(appCode, version);

        queryWrapper.orderByDesc(AppVersion::getId);
        List<AppVersion> list = appVersionService.list(queryWrapper);

        return PropertyUtils.copyArrayProperties(list, AppVersionResp.class);
    }

    @Override
    public AppVersionResp query(Integer id) {
        AppVersion appVersion = appVersionService.getById(id);
        return PropertyUtils.copyProperties(appVersion, AppVersionResp.class);
    }

    @Override
    public Boolean save(@RequestBody AppVersionReq req) {
        AppVersion entity = PropertyUtils.copyProperties(req, AppVersion.class);
        Long totalCount = deviceFeign.countByAppCode(req.getAppCode());
        // 可以调整逻辑，比如获取最近1年有活跃的设备数
        entity.setTotalCount(totalCount == null ? 0 : totalCount);
        entity.setUpdateCount(0L);
        return appVersionService.save(entity);
    }

    @Override
    public Boolean update(@RequestBody AppVersionReq req) {
        AppVersion entity = PropertyUtils.copyProperties(req, AppVersion.class);
        return appVersionService.updateById(entity);
    }

    @Override
    public Boolean remove(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
        return appVersionService.removeByIds(idList);
    }

    @Override
    public Boolean statAllUpgradedDevices() {
        return appVersionService.statAllUpgradedDevices();
    }

    @Override
    public AppVersionCheckResp check(String appCode, String currentVersion) {
        // 取最近1条全量版本记录
        AppVersion lastFullVersion = appVersionService.selectLastFullByAppCode(appCode);
        Integer lastFullVersionId = lastFullVersion != null ? lastFullVersion.getId() : 0;
        /*
         * 可能会存在多个灰度版本，如果有设备需要更新，那么优先新版本的灰度
         */
        // 取最近1条全量版本及之后的数据
        List<AppVersion> appVersionList = appVersionService.selectByAppCodeGeId(appCode, lastFullVersionId);
        for (AppVersion appVersion : appVersionList) {
            String latestVersion = appVersion.getVersion();
            String minSupportedVersion = appVersion.getMinSupportedVersion();

            if (CharSequenceUtil.compareVersion(currentVersion, latestVersion) >= 0) {
                // 当前版本>=最新版本，无需更新
                return AppVersionCheckResp.noUpdate();
            }

            BigDecimal grayPercent = appVersion.getGrayPercent();
            if (grayPercent.compareTo(BigDecimal.ONE) >= 0) {
                // 灰度比例达到100%，代表全量
                if (CharSequenceUtil.compareVersion(currentVersion, minSupportedVersion) < 0) {
                    // 当前版本<最低支持版本，强制更新
                    return AppVersionCheckResp.forceUpdate(latestVersion, appVersion.getDownloadUrl(),
                        appVersion.getReleaseNotes());
                }
                // 提示更新
                return AppVersionCheckResp.tipsUpdate(latestVersion, appVersion.getDownloadUrl(), appVersion.getReleaseNotes());
            }
            // 待补充逻辑：白名单设备始终提示更新（优先于灰度放量已满判断）
            boolean isInWhiteList = false;
            if (isInWhiteList) {
                // 命中白名单
                if (CharSequenceUtil.compareVersion(currentVersion, minSupportedVersion) < 0) {
                    // 当前版本<最低支持版本，强制更新
                    return AppVersionCheckResp.forceUpdate(latestVersion, appVersion.getDownloadUrl(),
                        appVersion.getReleaseNotes());
                }
                // 提示更新
                return AppVersionCheckResp.tipsUpdate(latestVersion, appVersion.getDownloadUrl(), appVersion.getReleaseNotes());
            }
            // 灰度比例未达到100%
            /*
             * 灰度强更问题：可能会导致超出灰度比例范围的设备被强制更新
             * 所以建议灰度无强更
             */
            Long updateCount = appVersion.getUpdateCount();
            Long totalCount = appVersion.getTotalCount();
            int grayCount = grayPercent.multiply(new BigDecimal(totalCount)).intValue();
            if (updateCount < grayCount) {
                // 提示更新
                return AppVersionCheckResp.tipsUpdate(latestVersion, appVersion.getDownloadUrl(), appVersion.getReleaseNotes());
            }
            // 更新量已超过灰度量，继续找前1条数据判断
        }
        return AppVersionCheckResp.noUpdate();
    }
}
