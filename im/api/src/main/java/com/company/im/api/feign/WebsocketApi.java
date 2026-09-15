package com.company.im.api.feign;

import com.company.im.api.request.AllReq;
import com.company.im.api.request.GroupReq;
import com.company.im.api.request.UserReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * websocket 契约接口（纯契约，不带 @FeignClient）
 * <p>
 * 调用方在自身 service 模块的 feign 包下新建 WebsocketFeign extends WebsocketApi 并标注 @FeignClient
 */
public interface WebsocketApi {

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
