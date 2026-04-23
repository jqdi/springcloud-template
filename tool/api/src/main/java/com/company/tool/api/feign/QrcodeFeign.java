package com.company.tool.api.feign;


import com.company.tool.api.constant.Constants;
import com.company.tool.api.feign.fallback.ThrowExceptionFallback;
import com.company.tool.api.request.WxaCodeReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = Constants.FEIGNCLIENT_VALUE, path = "/qrcode", fallbackFactory = ThrowExceptionFallback.class)
public interface QrcodeFeign {

	/**
	 * 获取小程序码并上传至图片服务
	 * 
	 * @param wxaCodeReq
	 * @return 小程序码图片链接
	 */
	@PostMapping(value = "/wxaCode2upload")
    Map<String, String> wxaCode2upload(@RequestBody WxaCodeReq wxaCodeReq);

	/**
	 * 获取小程序码（建议优先使用wxaCode2upload，除非上传图片到服务器有性能问题）
	 * 
	 * @param wxaCodeReq
	 * @return 小程序码byte数组
	 */
	@PostMapping(value = "/wxaCode")
	byte[] wxaCode(@RequestBody WxaCodeReq wxaCodeReq);

}
