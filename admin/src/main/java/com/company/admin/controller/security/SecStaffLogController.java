package com.company.admin.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.security.SecStaffLog;
import com.company.admin.service.security.SecStaffLogService;

/**
 * 系统用户日志Controller
 * Created by JQ棣 on 2017/11/9.
 */
@Controller
public class SecStaffLogController {

    @Autowired
    private SecStaffLogService secStaffLogService;

    @RequestMapping(value = "/admin/system/secStaffLog", method = RequestMethod.GET)
    public String indexList(Model model, SecStaffLog secStaff) {
        if(secStaff.getPage() == null) secStaff.setPage(1L);
        model.addAttribute("search", secStaff);
        model.addAttribute("pageModel", secStaffLogService.listAndCount(secStaff));
        return "system/secStaffLog";
    }

    @RequestMapping(value = "/admin/security/secStaffLog/get", method = RequestMethod.POST)
    @ResponseBody
    public SecStaffLog adminGet(SecStaffLog secStaffLog) {
        return secStaffLogService.get(secStaffLog);
    }
}
