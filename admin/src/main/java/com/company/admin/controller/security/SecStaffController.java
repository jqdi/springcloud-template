package com.company.admin.controller.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.company.admin.service.security.SecStaffService;

import com.company.admin.entity.security.SecOrganization;
import com.company.admin.entity.security.SecRole;
import com.company.admin.entity.security.SecStaff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户Controller
 * Created by JQ棣 on 2017/11/6.
 */
@Controller
public class SecStaffController {

    @Autowired
    private SecStaffService secStaffService;

    @RequestMapping(value = "/admin/system/secStaff", method = RequestMethod.GET)
    public String indexList(Model model, SecStaff secStaff) throws JsonProcessingException {
        if(secStaff.getPage() == null) secStaff.setPage(1L);
        model.addAttribute("search", secStaff);
        model.addAttribute("pageModel", secStaffService.listAndCount(secStaff));
        return "system/secStaff";
    }

    @RequestMapping(value = "/admin/security/secStaff/get", method = RequestMethod.POST)
    @ResponseBody
    public SecStaff adminGet(SecStaff secStaff, HttpServletRequest request) {
        //防止非超级管理员获取超级管理员信息
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff currentStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        if (currentStaff.getType().equals(10)) {
            secStaff.setType(null);
        }
        else {
            secStaff.setType(11);
        }
        return secStaffService.get(secStaff);
    }

    @RequestMapping(value = "/admin/security/secStaff/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(SecStaff.Save.class) SecStaff secStaff) throws Exception {
        secStaffService.save(secStaff);
        return null;
    }

    @RequestMapping(value = "/admin/security/secStaff/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(SecStaff secStaff) {
        secStaffService.remove(secStaff);
        return null;
    }

    @RequestMapping(value = "/admin/security/secStaff/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(SecStaff.Update.class) SecStaff secStaff, HttpServletRequest request) throws Exception {
        //防止非超级管理员修改超级管理员信息
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff currentStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        if (currentStaff.getType().equals(10)) {
            secStaff.setType(null);
        }
        else {
            secStaff.setType(11);
        }
        secStaffService.update(secStaff);
        return null;
    }

    @RequestMapping(value = "/admin/security/secStaff/secRole/list", method = RequestMethod.POST)
    @ResponseBody
    public List<SecRole> adminListRole(SecStaff secStaff) {
        return secStaffService.listRole(secStaff);
    }

    @RequestMapping(value = "/admin/security/secStaff/secRole/authorize", method = RequestMethod.POST)
    @ResponseBody
    public Void adminAuthorizeRole(SecStaff secStaff, Long[] roleIds) {
        List<SecRole> roleList = new ArrayList<>();
        for (Long item : roleIds) {
            roleList.add(new SecRole(item));
        }
        secStaff.setRoleList(roleList);
        secStaffService.authorizeRole(secStaff);
        return null;
    }

    @RequestMapping(value = "/admin/security/secStaff/secOrg/tree", method = RequestMethod.POST)
    @ResponseBody
    public List<SecOrganization> adminListOrg(SecStaff secStaff) {
        return secStaffService.treeOrganization(secStaff);
    }

    @RequestMapping(value = "/admin/security/secStaff/secOrg/authorize", method = RequestMethod.POST)
    @ResponseBody
    public Void adminAuthorizeOrg(SecStaff secStaff, Long[] organizationIds) {
        List<SecOrganization> orgList = new ArrayList<>();
        for (Long item : organizationIds) {
            orgList.add(new SecOrganization(item));
        }
        secStaff.setOrganizationList(orgList);
        secStaffService.authorizeOrganization(secStaff);
        return null;
    }

    @RequestMapping(value = "/admin/security/secStaff/info/get", method = RequestMethod.POST)
    @ResponseBody
    public SecStaff adminGetInfo(SecStaff secStaff, HttpServletRequest request) {
        //防止非超级管理员获取超级管理员信息
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff currentStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        if (currentStaff.getType().equals(10)) {
            secStaff.setType(null);
        }
        else {
            secStaff.setType(11);
        }
        return secStaffService.get(secStaff);
    }

    @RequestMapping(value = "/admin/security/secStaff/info/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdateInfo(@Validated(SecStaff.Update.class) SecStaff secStaff, HttpServletRequest request) throws Exception {
        //防止非超级管理员修改超级管理员信息
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff currentStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        if (currentStaff.getType().equals(10)) {
            secStaff.setType(null);
        }
        else {
            secStaff.setType(11);
        }
        secStaff.setId(currentStaff.getId());
        secStaffService.update(secStaff);
        return null;
    }
}
