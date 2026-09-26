package com.company.user.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.user.constant.Constants;
import com.company.user.entity.DeviceInfo;
import com.company.user.mapper.user.DeviceInfoMapper;

import java.time.LocalDateTime;

@Component
public class DeviceInfoService extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements IService<DeviceInfo> {

    @Cacheable(value = Constants.CacheName.DEVICE_INFO, key = "#deviceid")
    public DeviceInfo selectByDeviceid(String deviceid) {
        return baseMapper.selectByDeviceid(deviceid);
    }

    public int saveOrUpdate(String deviceid, String platform, String operator, String channel, String version, String requestip,
        String userAgent, LocalDateTime time) {
        return baseMapper.saveOrUpdate(deviceid, platform, operator, channel, version, requestip, userAgent, time);
    }
}
