package com.company.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.company.framework.context.HeaderContextUtil;
import com.company.user.api.enums.WalletEnum;
import com.company.user.api.feign.WalletRecordFeign;
import com.company.user.api.response.WalletRecordResp;
import com.company.user.entity.Wallet;
import com.company.user.entity.WalletRecord;
import com.company.user.service.WalletRecordService;
import com.company.user.service.WalletService;

@RestController
@RequestMapping("/walletRecord")
public class WalletRecordController implements WalletRecordFeign {

	@Autowired
	private WalletService walletService;
	@Autowired
	private WalletRecordService walletRecordService;

	@Override
	public List<WalletRecordResp> pageMain(
			@Valid @NotNull(message = "缺少参数当前页") @Min(value = 1, message = "当前页不能小于1") Integer current,
			@Valid @NotNull(message = "缺少参数每页数量") Integer size) {
		Integer userId = HeaderContextUtil.currentUserIdInt();

		Wallet wallet = walletService.getOrInit(userId, WalletEnum.Type.TO_MAIN);
		List<WalletRecord> walletRecordList = walletRecordService.page(wallet.getId(), current, size);
		List<WalletRecordResp> respList = walletRecordList.stream().map(v -> {
			WalletRecordResp resp = new WalletRecordResp();
			resp.setAmount(v.getAmount());
			resp.setType(v.getType());
			resp.setBalanceBefore(v.getBalanceBefore());
			resp.setBalanceAfter(v.getBalanceAfter());
			resp.setCreateTime(v.getCreateTime());
			return resp;
		}).collect(Collectors.toList());
		return respList;
	}
}
