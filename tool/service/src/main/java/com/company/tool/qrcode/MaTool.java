package com.company.tool.qrcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.stereotype.Component;

import com.company.framework.util.Utils;
import com.company.framework.context.SpringContextUtil;
import com.company.tool.qrcode.dto.LineColorParam;
import com.company.tool.qrcode.dto.MaWxaCode;
import com.company.tool.wx.miniapp.config.WxMaConfiguration;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;

@Slf4j
@Component("wxMaQrcodeMaTool")
public class MaTool {

	public MaWxaCode createWxaCodeUnlimit(String appid, String scene, String page, boolean checkPath, int width,
			boolean autoColor, LineColorParam lineColorParam, boolean isHyaline) {
		MaWxaCode maWxaCode = new MaWxaCode();

		WxMaService wxMaService = WxMaConfiguration.getMaService(appid);
		WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();

		/**
		 * <pre>
		{
			"page": "pages/index/index",
			"scene": "a=1",
			"check_path": true,
			"env_version": "release"
		}
		 * </pre>
		 */
		// 默认"release" 要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"
		String envVersion = WxMaConstants.DEFAULT_ENV_VERSION;// 默认
		if (SpringContextUtil.isTestProfile()) {// 测试环境
			envVersion = "develop";
		}

		WxMaCodeLineColor lineColor = new WxMaCodeLineColor(lineColorParam.getR(), lineColorParam.getG(),
				lineColorParam.getB());

		long start = System.currentTimeMillis();
		try {
			/**
			 * 获取不限制的小程序码
			 *
			 * <pre>
			 * 官网：https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html
			 * </pre>
			 */
			byte[] bytes = qrcodeService.createWxaCodeUnlimitBytes(scene, page, checkPath, envVersion, width, autoColor,
					lineColor, isHyaline);
			maWxaCode.setBytes(bytes);
			maWxaCode.setSuccess(true);
		} catch (WxErrorException e) {
			log.error("createWxaCodeUnlimit error", e);
			WxError error = e.getError();
			maWxaCode.setSuccess(false);
			maWxaCode.setMessage(error.getErrorMsg());
		}
		log.info(
				"cost:{}ms,scene:{},page:{},checkPath:{},envVersion:{},width:{},autoColor:{},lineColor:{},isHyaline:{}",
				System.currentTimeMillis() - start, scene, page, checkPath, envVersion, width, autoColor,
				lineColor, isHyaline);
		return maWxaCode;
	}

	public static void main(String[] args) {
		WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid("wxeb6ffb1ebd72a4fd1");
        config.setSecret("1111111b11e22e84521333333333");

        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);

        WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();

		// 默认"release" 要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"
		String envVersion = WxMaConstants.DEFAULT_ENV_VERSION;// 默认
		if (SpringContextUtil.isTestProfile()) {// 测试环境
			envVersion = "develop";
		}

//		String scene = "shareTo=1&linkId=22";
//		String page = "pages/member/share";
		String scene = "id=51";
//		String page = "pages/adv";
		String page = "pages/home/index";
		boolean checkPath = true;
		int width = 430;
		boolean autoColor = true;
		WxMaCodeLineColor lineColor = new WxMaCodeLineColor("0", "0", "0");
		boolean isHyaline = false;
		try {
			byte[] bytes = qrcodeService.createWxaCodeUnlimitBytes(scene, page, checkPath, envVersion, width, autoColor, lineColor, isHyaline);
			System.out.println(bytes);
			String extraSuffix = Utils.extraSuffix(bytes);
			System.out.println("extraSuffix:" + extraSuffix);

			try {
				OutputStream out = new FileOutputStream(new File("D:/WxaCodeUnlimit." + extraSuffix));
				boolean isCloseOut = true;
				IoUtil.write(out, isCloseOut, bytes);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (WxErrorException e) {
			log.error("createWxaCodeUnlimit error", e);
		}
	}
}
