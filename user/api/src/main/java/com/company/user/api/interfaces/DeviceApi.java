package com.company.user.api.interfaces;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DeviceApi {

    /**
     * 判断设备是否已在线
     *
     * @param deviceid
     * @return
     */
    @RequestMapping("/isOnline")
    Boolean isOnline(@RequestParam("deviceid") String deviceid);
}