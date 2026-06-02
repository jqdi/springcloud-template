package com.company.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.company.framework.globalresponse.ExceptionUtil;
import com.company.tool.api.feign.FileFeign;
import com.company.tool.api.request.PresignedUploadReq;
import com.company.tool.api.response.PresignedUploadResp;

import cn.hutool.http.HttpConfig;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private FileFeign fileFeign;

	@PostMapping("/upload")
	public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
		String name = file.getName();
		String originalFilename = file.getOriginalFilename();
		String contentType = file.getContentType();
		long size = file.getSize();
		log.info("name:{},originalFilename:{},contentType:{},size:{}", name, originalFilename, contentType, size);

		if (size == 0) {
			ExceptionUtil.throwException("请选择文件");
		}

        PresignedUploadReq presignedUploadReq = new PresignedUploadReq();
        presignedUploadReq.setBasePath("web");
        presignedUploadReq.setFileName(originalFilename);
        PresignedUploadResp presignedUploadResp = fileFeign.presignedUpload(presignedUploadReq);
        String fileKey = presignedUploadResp.getFileKey();
        String presignedUrl = presignedUploadResp.getPresignedUrl();

        byte[] fileBytes;
        try (InputStream inputStream = file.getInputStream()) {
            fileBytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("IOException", e);
            ExceptionUtil.throwException("文件上传失败");
            return null;
        }

        // 客户端使用presignedUrl上传文件
        HttpRequest httpRequest = HttpRequest.put(presignedUrl)
                // 设置不要自动添加Content-Type，否则会报签名不匹配
                .setConfig(HttpConfig.create().setUseDefaultContentTypeIfNull(false)).body(fileBytes);
        try (HttpResponse response = httpRequest.execute()) {
            String result = response.body();
            log.info("result:{}", result);
            return Collections.singletonMap("value", fileKey);
        }
    }

    @PostMapping("/presignedUpload")
    public PresignedUploadResp presignedUpload(String fileName) {
        PresignedUploadReq presignedUploadReq = new PresignedUploadReq();
        presignedUploadReq.setBasePath("web");
        presignedUploadReq.setFileName(fileName);
        return fileFeign.presignedUpload(presignedUploadReq);
    }

    /**
     * 获取访问链接
     *
     * @param fileKey
     * @return
     */
    @GetMapping("/url")
    public Map<String, String> url(String fileKey) {
        return fileFeign.presignedUrl(fileKey);
    }
}