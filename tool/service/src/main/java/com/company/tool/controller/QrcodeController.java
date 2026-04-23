package com.company.tool.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.tool.api.feign.QrcodeFeign;
import com.company.tool.api.request.WxaCodeReq;
import com.company.tool.filestorage.UploadService;
import com.company.tool.qrcode.WxaCodeService;
import com.company.tool.qrcode.dto.LineColorParam;

/**
 * 二维码
 * 
 * <pre>
 * 1.获取小程序码
 * </pre>
 */
@RestController
@RequestMapping("/qrcode")
public class QrcodeController implements QrcodeFeign {

	@Autowired
	private WxaCodeService wxaCodeService;
	@Autowired
	private UploadService uploadService;

	@Override
	public Map<String, String> wxaCode2upload(@RequestBody WxaCodeReq wxaCodeReq) {
		byte[] bytes = getWxaCodeBytes(wxaCodeReq);
		String fileKey = uploadService.upload(bytes);
        return Collections.singletonMap("value", fileKey);
	}

	@Override
	public byte[] wxaCode(@RequestBody WxaCodeReq wxaCodeReq) {
		byte[] bytes = getWxaCodeBytes(wxaCodeReq);
		return bytes;
	}

	private byte[] getWxaCodeBytes(WxaCodeReq wxaCodeReq) {
		String scene = wxaCodeReq.getScene();
		String page = wxaCodeReq.getPage();

		Boolean checkPath = wxaCodeReq.getCheckPath();
		Integer width = wxaCodeReq.getWidth();
		Boolean autoColor = wxaCodeReq.getAutoColor();

		LineColorParam lineColorParam = Optional.ofNullable(wxaCodeReq.getLineColor())
				.map(v -> new LineColorParam(v.getR(), v.getG(), v.getB())).orElse(null);

		Boolean isHyaline = wxaCodeReq.getIsHyaline();

		return wxaCodeService.wxaCode(scene, page, checkPath, width, autoColor, lineColorParam, isHyaline);
	}
}
