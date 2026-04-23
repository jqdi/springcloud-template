package com.company.system.controller;

import java.util.List;

import com.company.system.api.request.RemoveReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.feign.SysOperLogFeign;
import com.company.system.api.request.SysOperLogReq;
import com.company.system.api.response.SysOperLogResp;
import com.company.system.entity.SysOperLog;
import com.company.system.service.SysOperLogService;

@RestController
@RequestMapping("/sysOperLog")
public class SysOperLogController implements SysOperLogFeign {

	@Autowired
	private SysOperLogService sysOperLogService;

	private QueryWrapper<SysOperLog> toQueryWrapper(Integer sysUserId, String title, Integer businessType, String method, String requestMethod, String operUrl, String operIp, String operLocation, String operParam, String jsonResult, Integer status, String errorMsg, Integer costTime, String operTimeStart, String operTimeEnd, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysOperLog> queryWrapper = new QueryWrapper<>();
		if (sysUserId != null) {
			queryWrapper.eq("sys_user_id", sysUserId);
		}
		if (StringUtils.isNotBlank(title)) {
			queryWrapper.like("title", title);
		}
		if (businessType != null) {
			queryWrapper.eq("business_type", businessType);
		}
		if (StringUtils.isNotBlank(method)) {
			queryWrapper.like("method", method);
		}
		if (StringUtils.isNotBlank(requestMethod)) {
			queryWrapper.like("request_method", requestMethod);
		}
		if (StringUtils.isNotBlank(operUrl)) {
			queryWrapper.like("oper_url", operUrl);
		}
		if (StringUtils.isNotBlank(operIp)) {
			queryWrapper.like("oper_ip", operIp);
		}
		if (StringUtils.isNotBlank(operLocation)) {
			queryWrapper.like("oper_location", operLocation);
		}
		if (StringUtils.isNotBlank(operParam)) {
			queryWrapper.like("oper_param", operParam);
		}
		if (StringUtils.isNotBlank(jsonResult)) {
			queryWrapper.like("json_result", jsonResult);
		}
		if (status != null) {
			queryWrapper.eq("status", status);
		}
		if (StringUtils.isNotBlank(errorMsg)) {
			queryWrapper.like("error_msg", errorMsg);
		}
		if (costTime != null) {
			queryWrapper.eq("cost_time", costTime);
		}
        if (StringUtils.isNotBlank(operTimeStart)) {
            queryWrapper.ge("oper_time", operTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(operTimeEnd)) {
            queryWrapper.le("oper_time", operTimeEnd + " 23:59:59");
        }
        if (StringUtils.isNotBlank(createTimeStart)) {
            queryWrapper.ge("create_time", createTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(createTimeEnd)) {
            queryWrapper.le("create_time", createTimeEnd + " 23:59:59");
        }
        if (StringUtils.isNotBlank(updateTimeStart)) {
            queryWrapper.ge("update_time", updateTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(updateTimeEnd)) {
            queryWrapper.le("update_time", updateTimeEnd + " 23:59:59");
        }
		return queryWrapper;
	}

	@Override
	public PageResp<SysOperLogResp> page(Long current, Long size, Integer sysUserId, String title, Integer businessType, String method, String requestMethod, String operUrl, String operIp, String operLocation, String operParam, String jsonResult, Integer status, String errorMsg, Integer costTime, String operTimeStart, String operTimeEnd, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysOperLog> queryWrapper = toQueryWrapper(sysUserId, title, businessType, method, requestMethod, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg, costTime, operTimeStart, operTimeEnd, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		long count = sysOperLogService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysOperLog> list = sysOperLogService.list(PageDTO.of(current, size), queryWrapper);

		List<SysOperLogResp> respList = PropertyUtils.copyArrayProperties(list, SysOperLogResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysOperLogResp> list(Integer sysUserId, String title, Integer businessType, String method, String requestMethod, String operUrl, String operIp, String operLocation, String operParam, String jsonResult, Integer status, String errorMsg, Integer costTime, String operTimeStart, String operTimeEnd, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysOperLog> queryWrapper = toQueryWrapper(sysUserId, title, businessType, method, requestMethod, operUrl, operIp, operLocation, operParam, jsonResult, status, errorMsg, costTime, operTimeStart, operTimeEnd, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysOperLog> list = sysOperLogService.list(queryWrapper);

		List<SysOperLogResp> respList = PropertyUtils.copyArrayProperties(list, SysOperLogResp.class);
		return respList;
	}

	@Override
	public SysOperLogResp query(Integer id) {
		SysOperLog sysOperLog = sysOperLogService.getById(id);
		SysOperLogResp sysOperLogResp = PropertyUtils.copyProperties(sysOperLog, SysOperLogResp.class);
		return sysOperLogResp;
	}

	@Override
	public Boolean save(SysOperLogReq sysOperLogReq) {
		SysOperLog sysOperLog = PropertyUtils.copyProperties(sysOperLogReq, SysOperLog.class);
		Integer sysUserId = sysOperLogReq.getSysUserId();
		sysOperLog.setCreateBy(sysUserId);
		sysOperLog.setUpdateBy(sysUserId);
		boolean success = sysOperLogService.save(sysOperLog);
		return success;
	}

	@Override
	public Boolean update(SysOperLogReq sysOperLogReq) {
		SysOperLog sysOperLog = PropertyUtils.copyProperties(sysOperLogReq, SysOperLog.class);
		boolean success = sysOperLogService.updateById(sysOperLog);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysOperLogService.removeBatchByIds(req.getIdList());
		return success;
	}
}
