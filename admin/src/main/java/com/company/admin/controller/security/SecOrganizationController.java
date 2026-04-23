package com.company.admin.controller.security;

import com.fasterxml.jackson.core.JsonProcessingException;


import com.company.framework.util.JsonUtil;
import com.company.admin.service.security.SecOrganizationService;

import com.company.admin.entity.security.SecOrganization;
import com.company.admin.entity.security.SecRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统组织Controller
 * Created by JQ棣 on 2017/11/5.
 */
@Controller
public class SecOrganizationController {

    @Autowired
    private SecOrganizationService secOrganizationService;

    //region Admin
    @RequestMapping(value = "/admin/system/secOrganization", method = RequestMethod.GET)
    public String indexList(Model model, SecOrganization secOrganization) throws JsonProcessingException {
        if(secOrganization.getPage() == null) secOrganization.setPage(1L);
        model.addAttribute("search", secOrganization);
        model.addAttribute("pageModel", secOrganizationService.listAndCount(secOrganization));
        model.addAttribute("orgTree", JsonUtil.toJsonString(secOrganizationService.tree(new SecOrganization())));
        return "system/secOrganization";
    }

    @RequestMapping(value = "/admin/security/secOrganization/get", method = RequestMethod.POST)
    @ResponseBody
    public SecOrganization adminGet(SecOrganization secOrganization) {
        return secOrganizationService.get(secOrganization);
    }

    @RequestMapping(value = "/admin/security/secOrganization/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(SecOrganization.Save.class) SecOrganization secOrganization) {
        secOrganizationService.save(secOrganization);
        return null;
    }

    @RequestMapping(value = "/admin/security/secOrganization/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(SecOrganization secOrganization) {
        secOrganizationService.remove(secOrganization);
        return null;
    }

    @RequestMapping(value = "/admin/security/secOrganization/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(SecOrganization.Update.class) SecOrganization secOrganization) {
        secOrganizationService.update(secOrganization);
        return null;
    }

    @RequestMapping(value = "/admin/security/secOrganization/secRole/list", method = RequestMethod.POST)
    @ResponseBody
    public List<SecRole> adminListRole(SecOrganization secOrganization) {
        return secOrganizationService.listRole(secOrganization);
    }

    @RequestMapping(value = "/admin/security/secOrganization/authorize", method = RequestMethod.POST)
    @ResponseBody
    public Void adminAuthorize(SecOrganization secOrganization, Long[] roleIds) {
        List<SecRole> roleList = new ArrayList<>();
        for (Long item : roleIds) {
            roleList.add(new SecRole(item));
        }
        secOrganization.setRoleList(roleList);
        secOrganizationService.authorizeRole(secOrganization);
        return null;
    }
    //endregion
}
