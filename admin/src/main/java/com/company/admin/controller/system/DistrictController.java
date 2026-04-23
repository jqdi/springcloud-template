package com.company.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.admin.annotation.Pagination;

import com.company.admin.entity.system.District;
import com.company.admin.service.system.DistrictService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * 区县Controller
 * Created by JQ棣 on 2018/05/30.
 */
@Controller
public class DistrictController {

    @Autowired
    private DistrictService districtService;

    @RequestMapping(value = "/admin/system/district", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, District district) throws JsonProcessingException {
        model.addAttribute("search", district);
        model.addAttribute("pageModel", districtService.listAndCount(district));
        return "system/district";
    }

    @RequestMapping(value = "/admin/system/district/get", method = RequestMethod.POST)
    @ResponseBody
    public District get(District district) {
        return districtService.get(district);
    }

    @RequestMapping(value = "/admin/system/district/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(District.Save.class) District district) {
        districtService.save(district);
        return null;
    }

    @RequestMapping(value = "/admin/system/district/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(District district) {
        districtService.remove(district);
        return null;
    }

    @RequestMapping(value = "/admin/system/district/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(District.Update.class) District district) {
        districtService.update(district);
        return null;
    }

    @RequestMapping(value = "/admin/system/district/status/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdateStatus(District district) {
        districtService.updateStatus(district);
        return null;
    }

    @RequestMapping(value = "/admin/system/district/list", method = RequestMethod.POST)
    @ResponseBody
    public List<District> adminDistrictList(District district) {
        return districtService.listCombo(district);
    }
}
