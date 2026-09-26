package com.company.adminapi.messagedriven.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.adminapi.messagedriven.strategy.dto.SysOperLogDto;
import com.company.framework.messagedriven.BaseStrategy;
import com.company.adminapi.feign.SysOperLogFeign;
import com.company.system.api.request.SysOperLogReq;

@Component(StrategyConstants.SAVE_OPERLOG_STRATEGY)
public class SaveOperLogStrategy implements BaseStrategy<SysOperLogDto> {

	@Autowired
	private SysOperLogFeign sysOperLogFeign;

	@Override
	public void doStrategy(SysOperLogDto params) {
		SysOperLogReq sysOperLogReq = new SysOperLogReq();
		sysOperLogReq.setSysUserId(params.getSysUserId());
		sysOperLogReq.setTitle(params.getTitle());
		sysOperLogReq.setBusinessType(params.getBusinessType());
		sysOperLogReq.setMethod(params.getMethod());
		sysOperLogReq.setRequestMethod(params.getRequestMethod());
		sysOperLogReq.setOperUrl(params.getOperUrl());
		sysOperLogReq.setOperIp(params.getOperIp());
		String operLocation = "query by ip " + params.getOperIp();
		sysOperLogReq.setOperLocation(operLocation);
		sysOperLogReq.setOperParam(params.getOperParam());
		sysOperLogReq.setJsonResult(params.getJsonResult());
		sysOperLogReq.setStatus(params.getStatus());
		sysOperLogReq.setErrorMsg(params.getErrorMsg());
		sysOperLogReq.setCostTime(params.getCostTime());
		sysOperLogFeign.save(sysOperLogReq);
	}
}