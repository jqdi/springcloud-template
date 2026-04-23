package com.company.im.api.feign;


import com.company.im.api.constant.Constants;
import com.company.im.api.feign.fallback.ThrowExceptionFallback;
import com.company.im.api.request.AllReq;
import com.company.im.api.request.GroupReq;
import com.company.im.api.request.UserReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/websocket", fallbackFactory = ThrowExceptionFallback.class)
public interface WebsocketFeign {

    /**
     * 发消息到所有连接
     *
     * @param allReq
     * @return
     */
    @PostMapping("/sendToAll")
    Void sendToAll(@RequestBody AllReq allReq);

    /**
     * 发消息给指定用户
     *
     * @param userReq
     * @return
     */
    @PostMapping("/sendToUser")
    Void sendToUser(@RequestBody UserReq userReq);

    /**
     * 发消息到组
     *
     * @param groupReq
     * @return
     */
    @PostMapping("/sendToGroup")
    Void sendToGroup(@RequestBody GroupReq groupReq);

}
