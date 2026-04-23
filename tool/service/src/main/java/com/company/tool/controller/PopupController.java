package com.company.tool.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.util.Utils;
import com.company.framework.context.HeaderContextUtil;
import com.company.tool.api.feign.PopupFeign;
import com.company.tool.api.request.BestPopupReq;
import com.company.tool.api.request.CancelUserPopupReq;
import com.company.tool.api.request.CreateUserPopupReq;
import com.company.tool.api.response.BestPopupResp;
import com.company.tool.entity.PopupLog;
import com.company.tool.popup.PopService;
import com.company.tool.popup.dto.PopButton;
import com.company.tool.popup.dto.PopImage;
import com.company.tool.popup.dto.PopupCanPop;
import com.company.tool.service.market.PopupLogService;
import com.company.tool.service.market.UserPopupService;

@RestController
@RequestMapping("/popup")
public class PopupController implements PopupFeign {

	@Autowired
	private PopService popService;
	@Autowired
	private UserPopupService userPopupService;
	@Autowired
	private PopupLogService popupLogService;

	@Override
	public BestPopupResp bestPopup(@RequestBody BestPopupReq bestPopupReq) {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		String deviceid = HeaderContextUtil.deviceid();

		Map<String, String> runtimeAttach = bestPopupReq.getRuntimeAttach();

		// 补充一些请求头参数
		runtimeAttach.putAll(HeaderContextUtil.httpContextHeader());

		PopupCanPop popupCanPop = popService.bestPop(userId, deviceid, runtimeAttach);
		if (popupCanPop == null) {
			return null;
		}

		// 转换为前端方便开发的格式
		BestPopupResp resp = toBestPopupResp(popupCanPop, popupCanPop.getBgImg());
		return resp;
	}


	/**
	 * 递归组装数据(转换为前端方便开发的格式)
	 *
	 * @param popupCanPop
	 * @param bgImgPop
	 * @return
	 */
	private static BestPopupResp toBestPopupResp(PopupCanPop popupCanPop, PopImage bgImgPop) {
		if (bgImgPop == null) {
			return null;
		}

		BestPopupResp resp = new BestPopupResp();
		resp.setModel(bgImgPop.getModel().getCode());
		resp.setTitle(popupCanPop.getTitle());
		resp.setText(popupCanPop.getText());

		BestPopupResp.PopImage bgImg = new BestPopupResp.PopImage();
		bgImg.setImgUrl(bgImgPop.getImgUrl());
		bgImg.setType(bgImgPop.getType());
		bgImg.setValue(bgImgPop.getValue());
		resp.setBgImg(bgImg);

		BestPopupResp.PopButton closeBtn = Optional.ofNullable(popupCanPop.getCloseBtn()).map(v -> {
			BestPopupResp.PopButton convert = new BestPopupResp.PopButton();
			convert.setType(v.getType());
			convert.setText(v.getText());
			convert.setValue(v.getValue());
			return convert;
		}).orElse(null);
		resp.setCloseBtn(closeBtn);

		resp.setPopupLogId(popupCanPop.getPopupLogId());

		resp.setNext(toBestPopupResp(popupCanPop, bgImgPop.getNext()));
		return resp;
	}

	/**
	 * 创建用户弹窗（只弹1次）
	 */
	@Override
	public Void createUserPopup(@RequestBody CreateUserPopupReq createUserPopupReq) {
		Integer userId = createUserPopupReq.getUserId();
		LocalDateTime beginTime = createUserPopupReq.getBeginTime();
		LocalDateTime endTime = createUserPopupReq.getEndTime();
		Integer priority = createUserPopupReq.getPriority();
		String title = createUserPopupReq.getTitle();
		String text = createUserPopupReq.getText();

		PopImage bgImg = Optional.ofNullable(createUserPopupReq.getBgImg()).map(v -> {
			PopImage convert = new PopImage();
			convert.setImgUrl(v.getImgUrl());
			convert.setType(v.getType());
			convert.setValue(v.getValue());
			return convert;
		}).orElse(null);

		PopButton closeBtn = Optional.ofNullable(createUserPopupReq.getCloseBtn()).map(v -> {
			PopButton convert = new PopButton();
			convert.setType(v.getType());
			convert.setText(v.getText());
			convert.setValue(v.getValue());
			return convert;
		}).orElse(null);

		userPopupService.createUserPopup(userId, beginTime, endTime, priority, title, text, bgImg, closeBtn);

		return null;
	}

	/**
	 * 取消用户弹窗（只弹1次）
	 */
	@Override
	public Void cancelUserPopup(@RequestBody CancelUserPopupReq cancelUserPopupReq) {
		Integer userId = cancelUserPopupReq.getUserId();
		String title = cancelUserPopupReq.getTitle();
		String remark = cancelUserPopupReq.getRemark();

		userPopupService.cancelUserPopup(userId, title, remark);

		return null;
	}

	/**
	 * 前端确认展示了弹窗
	 */
	@Override
	public Void remarkPopupLog(Integer popupLogId, String remark) {
		PopupLog popupLog = popupLogService.getById(popupLogId);
		PopupLog popupLog4Update = new PopupLog();
		popupLog4Update.setId(popupLogId);
		String newRemark = Utils.rightRemark(popupLog.getRemark(), remark);
		popupLog4Update.setRemark(newRemark);
		popupLogService.updateById(popupLog4Update);
		return null;
	}
}
