package com.company.tool.api.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.request.ClientUploadReq;
import com.company.tool.api.request.UploadReq;
import com.company.tool.api.response.ClientUploadResp;
import com.company.tool.api.response.UploadResp;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/file", fallbackFactory = ThrowExceptionFallback.class)
public interface FileFeign {

    /**
     * <pre>
     * 上传（不建议使用）
     * byte[]在微服务间传递，会有性能问题，消耗带宽
     * </pre>
     *
     * @param uploadReq
     * @return
     */
    @PostMapping("/upload")
    UploadResp upload(@RequestBody UploadReq uploadReq);

    /**
     * <pre>
     * 客户端上传（建议使用）
     * 使用场景：
     * 1. 前端直连文件服务器上传，直接将fileKey、presignedUrl返回给前端，让前端使用presignedUrl上传文件，避免文件经过微服务
     * 2. 前端请求后端接口上传，后端入口服务就使用presignedUrl上传文件，然后再把fileKey返回给前端，避免文件流传递其他微服务
     * </pre>
     *
     * @param clientUploadReq
     * @return 预签名链接
     */
    @PostMapping(value = "/clientUpload")
    ClientUploadResp clientUpload(@RequestBody ClientUploadReq clientUploadReq);

    /**
     * 获取预签名链接
     *
     * @param fileKey
     * @return 预签名链接
     */
    @GetMapping(value = "/presignedUrl")
    Map<String, String> presignedUrl(@RequestParam("fileKey") String fileKey);
}
