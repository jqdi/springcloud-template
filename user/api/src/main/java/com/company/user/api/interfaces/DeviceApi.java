package com.company.user.api.interfaces;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DeviceApi {

    /**
     * 判断设备是否已在线
     *
     * @param deviceid 设备ID
     * @param appCode 应用编码
     * @return
     */
    @RequestMapping("/isOnline")
    Boolean isOnline(@RequestParam("deviceid") String deviceid, @RequestParam("appCode") String appCode);

    /**
     * 按应用统计设备数量
     * 
     * @param appCode 应用编码
     */
    @RequestMapping("/countByAppCode")
    Long countByAppCode(@RequestParam("appCode") String appCode);

    /**
     * 已升级到指定版本的设备数（device_info.version = version）
     *
     * @param appCode 应用编码
     * @param version 目标版本号
     */
    @RequestMapping("/deviceCountUpgraded")
    Long deviceCountUpgraded(@RequestParam("appCode") String appCode, @RequestParam("version") String version);
}
