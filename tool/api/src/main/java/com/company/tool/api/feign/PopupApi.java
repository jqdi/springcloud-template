package com.company.tool.api.feign;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.company.tool.api.request.BestPopupReq;
import com.company.tool.api.request.CancelUserPopupReq;
import com.company.tool.api.request.CreateUserPopupReq;
import com.company.tool.api.response.BestPopupResp;

public interface PopupApi {

	@RequestMapping("/bestPopup")
	BestPopupResp bestPopup(@RequestBody BestPopupReq bestPopupReq);

	@RequestMapping("/createUserPopup")
	Void createUserPopup(@RequestBody CreateUserPopupReq createUserPopupReq);

	@RequestMapping("/cancelUserPopup")
	Void cancelUserPopup(@RequestBody CancelUserPopupReq cancelUserPopupReq);

	@RequestMapping("/remarkPopupLog")
	Void remarkPopupLog(@RequestParam("popupLogId") Integer popupLogId, @RequestParam("remark") String remark);
}