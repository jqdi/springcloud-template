package com.company.adminapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.company.adminapi.feign.client.UserInfoFeign;
import com.company.adminapi.feign.client.UserOauthFeign;
import com.company.framework.globalresponse.ExceptionUtil;
import com.company.framework.util.RegexUtil;
import com.company.user.api.enums.UserClientEnum;
import com.company.user.api.enums.UserOauthEnum;
import com.company.user.api.request.UserInfoReq;
import com.company.user.api.response.UserInfoResp;
import com.company.user.api.response.UserOauthResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.company.adminapi.annotation.OperationLog;
import com.company.adminapi.annotation.RequirePermissions;
import com.company.adminapi.controller.converter.SysUserIdNicknameConverter;
import com.company.adminapi.converter.annotation.RespConverter;
import com.company.adminapi.easyexcel.ExcelUtil;
import com.company.adminapi.enums.OperationLogEnum.BusinessType;
import com.company.adminapi.excel.SysUserExcel;
import com.company.adminapi.feign.client.SysUserFeign;
import com.company.adminapi.feign.client.SysUserPasswordFeign;
import com.company.common.response.PageResp;
import com.company.framework.context.HeaderContextUtil;
import com.company.framework.util.PropertyUtils;
import com.company.system.api.request.SaveNewPasswordReq;
import com.company.system.api.request.SysUserReq;
import com.company.system.api.response.SysUserInfoResp;
import com.company.system.api.response.SysUserResp;
import com.company.token.accesscontrol.annotation.RequireLogin;

@RequireLogin
@RestController
@RequestMapping("/sysUser")
public class UserController {

	@Autowired
	private SysUserFeign sysUserFeign;
	@Autowired
	private UserInfoFeign userInfoFeign;
	@Autowired
	private UserOauthFeign userOauthFeign;
	@Autowired
	private SysUserPasswordFeign sysUserPasswordFeign;

	@RespConverter(field = "createBy", newField = "createByNickname", dataSource = SysUserIdNicknameConverter.class)
	@RespConverter(field = "updateBy", newField = "updateByNickname", dataSource = SysUserIdNicknameConverter.class)
	@RequirePermissions("system:sysUser:query")
	@GetMapping("/page")
	public PageResp<com.company.adminapi.resp.SysUserResp> page(@NotNull @Min(value = 1) Long current, @NotNull Long size, String account, String nickname, String email, String phonenumber, String sex, String avatar, String status, Integer deptId, String userRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		PageResp<SysUserResp> pageResp = sysUserFeign.page(current, size, account, nickname, email, phonenumber, sex, avatar, status, deptId, userRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<com.company.adminapi.resp.SysUserResp> respList = PropertyUtils.copyArrayProperties(pageResp.getList(), com.company.adminapi.resp.SysUserResp.class);
        return PageResp.of(pageResp.getTotal(), respList);
	}

	@RespConverter(field = "createBy", newField = "createByNickname", dataSource = SysUserIdNicknameConverter.class)
	@RespConverter(field = "updateBy", newField = "updateByNickname", dataSource = SysUserIdNicknameConverter.class)
	@RequirePermissions("system:sysUser:query")
	@GetMapping("/query")
	public com.company.adminapi.resp.SysUserResp query(@NotNull Integer id) {
		SysUserResp sysUserResp = sysUserFeign.query(id);
		com.company.adminapi.resp.SysUserResp resp = PropertyUtils.copyProperties(sysUserResp, com.company.adminapi.resp.SysUserResp.class);
		return resp;
	}

	@OperationLog(title = "分配用户", businessType = BusinessType.INSERT)
	@RequirePermissions("system:sysUser:save")
	@PostMapping("/assignUser")
	public Boolean assignUser(@RequestBody SysUserReq sysUserReq) {
//		sysUserFeign.save(sysUserReq);

        String email = sysUserReq.getEmail();
        String phonenumber = sysUserReq.getPhonenumber();
        String account = sysUserReq.getAccount();
        if (!RegexUtil.checkEmail(email)) {
            ExceptionUtil.throwException("邮箱格式错误");
        }

        UserOauthResp userOauthResp = userOauthFeign.selectOauth(UserOauthEnum.IdentityType.EMAIL, email);
        if (userOauthResp != null && userOauthResp.getUserId() != null) {
            ExceptionUtil.throwException("邮箱已注册，可以直接登录！");
        }

//        String password = sysUserReq.getPassword();
        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setClientType(UserClientEnum.ClientType.MANAGE);
        userInfoReq.setIdentityType(UserOauthEnum.IdentityType.EMAIL);
        userInfoReq.setIdentifier(email);
        userInfoReq.setCertificate(regByEmailReq.getCode());
        userInfoReq.setPassword(PassWordUtil.md5(regByEmailReq.getPassword()));
//		userInfoReq.setAvatar(null);
//		userInfoReq.setNickname(null);
        UserInfoResp userInfoResp = userInfoFeign.findOrCreateUser(userInfoReq);

        UserInfoReq userInfoReq = new UserInfoReq();
        userInfoReq.setClientType(UserClientEnum.ClientType.MANAGE);
        userInfoReq.setIdentityType(UserOauthEnum.IdentityType.PASSWORD);
        userInfoReq.setIdentifier(sysUserReq.getAccount());
        // userInfoReq.setCertificate(certificate);
        userInfoReq.setNickname(sysUserReq.getNickname());
        userInfoReq.setAvatar(sysUserReq.getAvatar());

        UserInfoResp userInfoResp = userInfoFeign.findOrCreateUser(userInfoReq);
        if (userInfoResp == null) {
            return null;
        }
        Integer id = userInfoResp.getId();
        if (id == null) {
            return null;
        }
//        return String.valueOf(id);

		SysUserResp sysUserResp = sysUserFeign.getByAccount(sysUserReq.getAccount());
		SaveNewPasswordReq req = new SaveNewPasswordReq();
		req.setSysUserId(sysUserResp.getId());
		// todo: 设置高复杂度密码，并邮件发送
		req.setPassword("12345678");
		sysUserPasswordFeign.saveNewPassword(req);
		return true;
	}

	@OperationLog(title = "用户信息更新", businessType = BusinessType.UPDATE)
	@RequirePermissions("system:sysUser:update")
	@PostMapping("/update")
	public Boolean update(@RequestBody SysUserReq sysUserReq) {
		// 账号不可修改
		sysUserReq.setAccount(null);
		return sysUserFeign.update(sysUserReq);
	}

	@OperationLog(title = "用户信息删除", businessType = BusinessType.DELETE)
	@RequirePermissions("system:sysUser:remove")
	@PostMapping("/remove")
	public Boolean remove(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return true;
        }
		return sysUserFeign.remove(idList);
	}

	@OperationLog(title = "用户信息导出", businessType = BusinessType.EXPORT, isSaveResponseData = false)
	@RequirePermissions("system:sysUser:export")
	@GetMapping("/export")
	public void export(HttpServletResponse response, String account, String nickname, String email, String phonenumber, String sex, String avatar, String status, Integer deptId, String userRemark, String createTimeStart, String createTimeEnd, String updateTimeStart, String updateTimeEnd) {
		List<SysUserResp> listResp = sysUserFeign.list(account, nickname, email, phonenumber, sex, avatar, status, deptId, userRemark, createTimeStart, createTimeEnd, updateTimeStart, updateTimeEnd);
		List<SysUserExcel> excelList = PropertyUtils.copyArrayProperties(listResp, SysUserExcel.class);
		ExcelUtil.write2httpResponse(response, "用户信息", SysUserExcel.class, excelList);
	}

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
	@GetMapping(value = "/getInfo")
	public SysUserInfoResp getInfo() {
		Integer userId = HeaderContextUtil.currentUserIdInt();
		return sysUserFeign.getInfo(userId);
	}

	/**
	 * 个人中心-获取用户信息
	 *
	 * @return 用户信息
	 */
	@GetMapping(value = "/profile")
	public SysUserInfoResp profile() {
//		Integer userId = HeaderContextUtil.currentUserIdInt();
//		return sysUserFeign.getInfo(userId);
		return null;
	}

	/**
	 * 个人中心-修改用户信息
	 *
	 * @return 用户信息
	 */
	@PostMapping(value = "/updateProfile")
	public SysUserInfoResp updateProfile() {
//		Integer userId = HeaderContextUtil.currentUserIdInt();
//		return sysUserFeign.getInfo(userId);
		return null;
	}

	/**
	 * 个人中心-修改密码
	 *
	 * @return 用户信息
	 */
	@PostMapping(value = "/updatePassword")
	public SysUserInfoResp updatePassword() {
//		Integer userId = HeaderContextUtil.currentUserIdInt();
//		return sysUserFeign.getInfo(userId);
		return null;
	}
}
