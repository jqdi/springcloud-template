package com.company.tool.controller;

import com.company.tool.api.feign.FileFeign;
import com.company.tool.api.request.PresignedUploadReq;
import com.company.tool.api.request.UploadReq;
import com.company.tool.api.response.PresignedUploadResp;
import com.company.tool.api.response.UploadResp;
import com.company.tool.filestorage.PresignedUploadResult;
import com.company.tool.filestorage.UploadService;
import com.company.framework.globalresponse.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/file")
public class FileController implements FileFeign {

	@Autowired
	private UploadService uploadService;

	@Override
	public UploadResp upload(@RequestBody UploadReq uploadReq) {
		if (uploadReq.getBytes().length == 0) {
			ExceptionUtil.throwException("请选择文件");
		}

		String fileKey = uploadService.upload(uploadReq.getBytes(), uploadReq.getBasePath(), uploadReq.getFileName());

		UploadResp resp = new UploadResp();
		resp.setFileKey(fileKey);
		return resp;
	}

	@Override
	public PresignedUploadResp presignedUpload(@RequestBody PresignedUploadReq presignedUploadReq) {
		String basePath = presignedUploadReq.getBasePath();
		String fileName = presignedUploadReq.getFileName();
		PresignedUploadResult presignedUploadResult = uploadService.presignedUpload(basePath, fileName);
		PresignedUploadResp resp = new PresignedUploadResp();
		resp.setFileKey(presignedUploadResult.getFileKey());
		resp.setPresignedUrl(presignedUploadResult.getPresignedUrl());
		return resp;
	}

	@Override
	public Map<String, String> presignedUrl(String fileKey) {
		String presignedUrl = uploadService.presignedUrl(fileKey);
		return Collections.singletonMap("value", presignedUrl);
	}
}