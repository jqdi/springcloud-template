package com.company.admin.controller.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.admin.entity.security.SecResource;
import com.company.admin.entity.security.SecRole;
import com.company.admin.entity.security.SecStaff;
import com.company.admin.service.security.SecRoleService;
import com.company.admin.service.security.SecStaffService;


/**
 * 系统角色Controller
 * Created by JQ棣 on 2017/11/5.
 */
@Controller
public class SecRoleController {

    @Autowired
    private SecRoleService secRoleService;
    @Autowired
    private SecStaffService secStaffService;

    //region Admin
    @RequestMapping(value = "/admin/system/secRole", method = RequestMethod.GET)
    public String indexList(Model model, SecRole secRole) throws Exception {
        if(secRole.getPage() == null) secRole.setPage(1L);
        model.addAttribute("search", secRole);
        model.addAttribute("pageModel", secRoleService.listAndCount(secRole));
        return "system/secRole";
    }

    @RequestMapping(value = "/admin/security/secRole/get", method = RequestMethod.POST)
    @ResponseBody
    public SecRole adminGet(SecRole secRole) {
        return secRoleService.get(secRole);
    }

    @RequestMapping(value = "/admin/security/secRole/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(SecRole.Save.class) SecRole secRole) {
        secRoleService.save(secRole);
        return null;
    }

    @RequestMapping(value = "/admin/security/secRole/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(SecRole secRole) {
        secRoleService.remove(secRole);
        return null;
    }

    @RequestMapping(value = "/admin/security/secRole/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(SecRole.Update.class) SecRole secRole) {
        secRoleService.update(secRole);
        return null;
    }

    @RequestMapping(value = "/admin/security/secRole/secResource/tree", method = RequestMethod.POST)
    @ResponseBody
    public List<SecResource> adminTree(SecRole secRole, HttpServletRequest request) {
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff secStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        return secRoleService.treeResource(secRole, secStaff);
    }

    @RequestMapping(value = "/admin/security/secRole/authorize", method = RequestMethod.POST)
    @ResponseBody
    public Void adminAuthorize(SecRole secRole, Long[] resourceIds, HttpServletRequest request)
            {
        List<SecResource> resourceList = new ArrayList<>();
        for (Long item : resourceIds) {
            resourceList.add(new SecResource(item));
        }
        secRole.setResourceList(resourceList);
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT");
        SecStaff secStaff = secStaffService.getByUsername(new SecStaff(((User) securityContextImpl.getAuthentication().getPrincipal()).getUsername()));
        secRoleService.authorizeResource(secRole, secStaff);
        return null;
    }
    //endregion
}
