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
import com.company.system.api.feign.SysLogininfoFeign;
import com.company.system.api.request.SysLogininfoReq;
import com.company.system.api.response.SysLogininfoResp;
import com.company.system.entity.SysLogininfo;
import com.company.system.service.SysLogininfoService;

@RestController
@RequestMapping("/sysLogininfo")
public class SysLogininfoController implements SysLogininfoFeign {

	@Autowired
	private SysLogininfoService sysLogininfoService;

	private QueryWrapper<SysLogininfo> toQueryWrapper(Integer sysUserId, String loginTimeStart, String loginTimeEnd, String account, String device, String platform, String operator, String version, String deviceid, String channel, String ip, String address, String source, String lang, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysLogininfo> queryWrapper = new QueryWrapper<>();
		if (sysUserId != null) {
			queryWrapper.eq("sys_user_id", sysUserId);
		}
        if (StringUtils.isNotBlank(loginTimeStart)) {
            queryWrapper.ge("login_time", loginTimeStart + " 00:00:00");
        }
        if (StringUtils.isNotBlank(loginTimeEnd)) {
            queryWrapper.le("login_time", loginTimeEnd + " 23:59:59");
        }
		if (StringUtils.isNotBlank(account)) {
			queryWrapper.like("account", account);
		}
		if (StringUtils.isNotBlank(device)) {
			queryWrapper.like("device", device);
		}
		if (StringUtils.isNotBlank(platform)) {
			queryWrapper.like("platform", platform);
		}
		if (StringUtils.isNotBlank(operator)) {
			queryWrapper.like("operator", operator);
		}
		if (StringUtils.isNotBlank(version)) {
			queryWrapper.like("version", version);
		}
		if (StringUtils.isNotBlank(deviceid)) {
			queryWrapper.like("deviceid", deviceid);
		}
		if (StringUtils.isNotBlank(channel)) {
			queryWrapper.like("channel", channel);
		}
		if (StringUtils.isNotBlank(ip)) {
			queryWrapper.like("ip", ip);
		}
		if (StringUtils.isNotBlank(address)) {
			queryWrapper.like("address", address);
		}
		if (StringUtils.isNotBlank(source)) {
			queryWrapper.like("source", source);
		}
		if (StringUtils.isNotBlank(lang)) {
			queryWrapper.like("lang", lang);
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
	public PageResp<SysLogininfoResp> page(Long current, Long size, Integer sysUserId, String loginTimeStart, String loginTimeEnd, String account, String device, String platform, String operator, String version, String deviceid, String channel, String ip, String address, String source, String lang, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysLogininfo> queryWrapper = toQueryWrapper(sysUserId, loginTimeStart, loginTimeEnd, account, device, platform, operator, version, deviceid, channel, ip, address, source, lang, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		long count = sysLogininfoService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysLogininfo> list = sysLogininfoService.list(PageDTO.of(current, size), queryWrapper);

		List<SysLogininfoResp> respList = PropertyUtils.copyArrayProperties(list, SysLogininfoResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysLogininfoResp> list(Integer sysUserId, String loginTimeStart, String loginTimeEnd, String account, String device, String platform, String operator, String version, String deviceid, String channel, String ip, String address, String source, String lang, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysLogininfo> queryWrapper = toQueryWrapper(sysUserId, loginTimeStart, loginTimeEnd, account, device, platform, operator, version, deviceid, channel, ip, address, source, lang, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysLogininfo> list = sysLogininfoService.list(queryWrapper);

		List<SysLogininfoResp> respList = PropertyUtils.copyArrayProperties(list, SysLogininfoResp.class);
		return respList;
	}

	@Override
	public SysLogininfoResp query(Integer id) {
		SysLogininfo sysLogininfo = sysLogininfoService.getById(id);
		SysLogininfoResp sysLogininfoResp = PropertyUtils.copyProperties(sysLogininfo, SysLogininfoResp.class);
		return sysLogininfoResp;
	}

	@Override
	public Boolean save(SysLogininfoReq sysLogininfoReq) {
		SysLogininfo sysLogininfo = PropertyUtils.copyProperties(sysLogininfoReq, SysLogininfo.class);
		boolean success = sysLogininfoService.save(sysLogininfo);
		return success;
	}

	@Override
	public Boolean update(SysLogininfoReq sysLogininfoReq) {
		SysLogininfo sysLogininfo = PropertyUtils.copyProperties(sysLogininfoReq, SysLogininfo.class);
		boolean success = sysLogininfoService.updateById(sysLogininfo);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysLogininfoService.removeBatchByIds(req.getIdList());
		return success;
	}
}
