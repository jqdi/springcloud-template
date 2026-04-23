package com.company.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;

import com.company.system.api.request.RemoveReq;
import com.company.system.api.response.PageResp;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.constant.Constants;
import com.company.system.api.feign.SysUserFeign;
import com.company.system.api.request.SysUserAssignRoleReq;
import com.company.system.api.request.SysUserReq;
import com.company.system.api.response.SysUserInfoResp;
import com.company.system.api.response.SysUserResp;
import com.company.system.entity.SysMenu;
import com.company.system.entity.SysUser;
import com.company.system.service.SysMenuService;
import com.company.system.service.SysRoleService;
import com.company.system.service.SysUserRoleService;
import com.company.system.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sysUser")
public class SysUserController implements SysUserFeign {

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	private QueryWrapper<SysUser> toQueryWrapper(String account, String nickname, String email, String phonenumber, String sex, String avatar, String status, Integer deptId, String userRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
		if (StringUtils.isNotBlank(account)) {
			queryWrapper.like("account", account);
		}
		if (StringUtils.isNotBlank(nickname)) {
			queryWrapper.like("nickname", nickname);
		}
		if (StringUtils.isNotBlank(email)) {
			queryWrapper.like("email", email);
		}
		if (StringUtils.isNotBlank(phonenumber)) {
			queryWrapper.like("phonenumber", phonenumber);
		}
        if (StringUtils.isNotBlank(sex)) {
            queryWrapper.eq("sex", sex);
        }
		if (StringUtils.isNotBlank(avatar)) {
			queryWrapper.like("avatar", avatar);
		}
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq("status", status);
        }
		if (deptId != null) {
			queryWrapper.eq("dept_id", deptId);
		}
		if (StringUtils.isNotBlank(userRemark)) {
			queryWrapper.like("user_remark", userRemark);
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
	public PageResp<SysUserResp> page(Long current, Long size, String account, String nickname, String email, String phonenumber, String sex, String avatar, String status, Integer deptId, String userRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysUser> queryWrapper = toQueryWrapper(account, nickname, email, phonenumber, sex, avatar, status, deptId, userRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		long count = sysUserService.count(queryWrapper);

		queryWrapper.orderByDesc("id");
		List<SysUser> list = sysUserService.list(PageDTO.of(current, size), queryWrapper);

		List<SysUserResp> respList = PropertyUtils.copyArrayProperties(list, SysUserResp.class);
		return PageResp.of(count, respList);
	}

	@Override
	public List<SysUserResp> list(String account, String nickname, String email, String phonenumber, String sex, String avatar, String status, Integer deptId, String userRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		QueryWrapper<SysUser> queryWrapper = toQueryWrapper(account, nickname, email, phonenumber, sex, avatar, status, deptId, userRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);

		queryWrapper.orderByDesc("id");
		List<SysUser> list = sysUserService.list(queryWrapper);

		List<SysUserResp> respList = PropertyUtils.copyArrayProperties(list, SysUserResp.class);
		return respList;
	}

	@Override
	public SysUserResp query(Integer id) {
		SysUser sysUser = sysUserService.getById(id);
		SysUserResp sysUserResp = PropertyUtils.copyProperties(sysUser, SysUserResp.class);
		return sysUserResp;
	}

	@Override
	public Boolean save(SysUserReq sysUserReq) {
		SysUser sysUser = PropertyUtils.copyProperties(sysUserReq, SysUser.class);
		boolean success = sysUserService.save(sysUser);
		return success;
	}

	@Override
	public Boolean update(SysUserReq sysUserReq) {
		SysUser sysUser = PropertyUtils.copyProperties(sysUserReq, SysUser.class);
		boolean success = sysUserService.updateById(sysUser);
		return success;
	}

	@Override
	public Boolean remove(@RequestBody RemoveReq<Integer> req) {
		if (req.getIdList() == null || req.getIdList().isEmpty()) {
			return true;
		}
		boolean success = sysUserService.removeByIds(req.getIdList());
		return success;
	}

	@Override
	public SysUserResp getByAccount(String account) {
		SysUser sysUser = sysUserService.getByAccount(account);
		return PropertyUtils.copyProperties(sysUser, SysUserResp.class);
	}

	@Override
	public SysUserResp getById(Integer id) {
		SysUser sysUser = sysUserService.getById(id);
		return PropertyUtils.copyProperties(sysUser, SysUserResp.class);
	}

	@Override
	public SysUserInfoResp getInfo(Integer userId) {
		SysUser user = sysUserService.getById(userId);
		Set<String> permissions = new HashSet<>();
		boolean isAdmin = sysRoleService.hasRole(userId, Constants.SUPER_ROLE);
		if (isAdmin) {
			permissions.add("*:*:*");
		} else {
            List<SysMenu> menuList = sysMenuService.getByUserId(userId);
			menuList.forEach(v -> {
				if (StringUtils.isNotBlank(v.getPerms())) {
					permissions.addAll(Arrays.asList(v.getPerms().trim().split(",")));
				}
			});
		}
		SysUserInfoResp resp = new SysUserInfoResp().setUser(PropertyUtils.copyProperties(user, SysUserInfoResp.User.class))
				.setPermissions(permissions);
		return resp;
	}

	@Override
	public List<SysUserResp> getByBatchId(@RequestBody List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}
		List<SysUser> sysUsers = sysUserService.listByIds(ids);
		return PropertyUtils.copyArrayProperties(sysUsers, SysUserResp.class);
	}

	@Override
	public Map<Integer, String> mapNicknameById(Collection<Integer> ids) {
		List<SysUser> sysUserList = sysUserService.selectByIds(ids);
		Map<Integer, String> idNicknameMap = sysUserList.stream()
				.collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));
		return idNicknameMap;
	}

	@Override
	public Boolean assignRole(@RequestBody SysUserAssignRoleReq req) {
		sysUserRoleService.assignRole(req.getSysUserId(), req.getSysRoleIds());
		return true;
	}
}
