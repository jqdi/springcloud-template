package com.company.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.system.Version;
import com.company.admin.service.system.VersionService;


/**
 * Created by JQ棣 on 11/1/17.
 */
@Controller
public class VersionController {

    @Autowired
    private VersionService versionService;

    @RequestMapping(value = "/admin/system/version", method = RequestMethod.GET)
    public String index(Model model, Version version) {
        model.addAttribute("search", version);
        model.addAttribute("pageModel", versionService.listAndCount(version));
        return "system/version";
    }

    @RequestMapping(value = "/admin/system/version/get", method = RequestMethod.POST)
    @ResponseBody
    public Version adminGet(Version version) {
        return versionService.get(version);
    }

    @RequestMapping(value = "/admin/system/version/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(Version.Save.class) Version version) {
        versionService.save(version);
        return null;
    }

    @RequestMapping(value = "/admin/system/version/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(Version version) {
        versionService.remove(version);
        return null;
    }

    @RequestMapping(value = "/admin/system/version/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(Version.Update.class) Version version) {
        versionService.update(version);
        return null;
    }
}
