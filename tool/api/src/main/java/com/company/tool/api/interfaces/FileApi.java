package com.company.tool.api.interfaces;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.tool.api.request.PresignedUploadReq;
import com.company.tool.api.request.UploadReq;
import com.company.tool.api.response.PresignedUploadResp;
import com.company.tool.api.response.UploadResp;

/**
 * 文件 契约接口（纯契约，不带 @FeignClient）
 *
 * <pre>
 * 服务提供方： Controller 实现本接口
 * 调用方：在自身 service 模块的 feign 包下新建 FileFeign extends FileApi 并标注 @FeignClient
 * </pre>
 *
 * @author JQ棣
 */
public interface FileApi {

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
     * @param presignedUploadReq
     * @return 预签名链接
     */
    @PostMapping(value = "/presignedUpload")
    PresignedUploadResp presignedUpload(@RequestBody PresignedUploadReq presignedUploadReq);

    /**
     * 获取预签名链接
     *
     * @param fileKey
     * @return 预签名链接
     */
    @GetMapping(value = "/presignedUrl")
    Map<String, String> presignedUrl(@RequestParam("fileKey") String fileKey);
}