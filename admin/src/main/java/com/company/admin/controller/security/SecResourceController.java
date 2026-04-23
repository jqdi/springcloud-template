package com.company.admin.controller.security;

import com.fasterxml.jackson.core.JsonProcessingException;


import com.company.framework.util.JsonUtil;
import com.company.admin.service.security.SecResourceService;

import com.company.admin.entity.security.SecResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统资源Controller
 * Created by JQ棣 on 2017/11/3.
 */
@Controller
public class SecResourceController {

    @Autowired
    private SecResourceService secResourceService;

    //region Admin
    @RequestMapping(value = "/admin/system/secResource", method = RequestMethod.GET)
    public String indexList(Model model, SecResource secResource) throws JsonProcessingException {
        if(secResource.getPage() == null) secResource.setPage(1L);
        model.addAttribute("search", secResource);
        model.addAttribute("pageModel", secResourceService.listAndCount(secResource));
        model.addAttribute("resourceTree", JsonUtil.toJsonString(secResourceService.tree(new SecResource())));
        return "system/secResource";
    }

    @RequestMapping(value = "/admin/security/secResource/get", method = RequestMethod.POST)
    @ResponseBody
    public SecResource adminGet(SecResource secResource) {
        return secResourceService.get(secResource);
    }

    @RequestMapping(value = "/admin/security/secResource/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(SecResource.Save.class) SecResource secResource) {
        secResourceService.save(secResource);
        return null;
    }

    @RequestMapping(value = "/admin/security/secResource/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(SecResource secResource) {
        secResourceService.remove(secResource);
        return null;
    }

    @RequestMapping(value = "/admin/security/secResource/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(SecResource.Update.class) SecResource secResource) {
        secResourceService.update(secResource);
        return null;
    }

    @RequestMapping(value = "/admin/security/secResource/reload", method = RequestMethod.POST)
    @ResponseBody
    public Void reload() throws Exception {
        secResourceService.reload();
        secResourceService.invalidateCache();
        return null;
    }
    //endregion
}
