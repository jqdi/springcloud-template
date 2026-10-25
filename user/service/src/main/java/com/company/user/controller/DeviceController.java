package com.company.user.controller;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;

import com.company.user.api.interfaces.DeviceApi;
import com.company.user.entity.DeviceInfo;
import com.company.user.mapper.user.DeviceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController implements DeviceApi {

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;


    @Value("${template.device.onlineSeconds:30}")
    private Integer onlineSeconds;// 判断n秒内活跃就算在线

    @Override
    public Boolean isOnline(String deviceid, String appCode) {
        if (StringUtils.isAnyBlank(deviceid, appCode)) {
            return false;
        }

        DeviceInfo deviceInfo = deviceInfoMapper.selectByDeviceidAppCode(deviceid, appCode);
        if (deviceInfo == null) {
            return false;
        }
        long seconds = LocalDateTimeUtil.between(deviceInfo.getTime(), LocalDateTime.now(), ChronoUnit.SECONDS);
        if (seconds > onlineSeconds) {
            return false;
        }

        return true;
    }

    @Override
    public Long countByAppCode(String appCode) {
        return deviceInfoMapper.countByAppCode(appCode);
    }

    @Override
    public Long deviceCountUpgraded(String appCode, String version) {
        if (StringUtils.isBlank(version)) {
            return 0L;
        }
        long upgraded = 0;
        Integer minId = 0;
        int limit = 1000;
        while (true) {
            List<DeviceInfo> list = deviceInfoMapper.pageByAppCodeId(appCode, minId, limit);
            if (list.isEmpty()) {
                break;
            }
            for (DeviceInfo deviceInfo : list) {
                String deviceVersion = deviceInfo.getVersion();
                if (StringUtils.isNotBlank(deviceVersion) && CharSequenceUtil.compareVersion(deviceVersion, version) == 0) {
                    upgraded++;
                }
                minId = deviceInfo.getId();
            }
            if (list.size() < limit) {
                break;
            }
        }
        return upgraded;
    }
}
