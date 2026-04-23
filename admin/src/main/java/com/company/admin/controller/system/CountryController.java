package com.company.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.system.Country;
import com.company.admin.service.system.CountryService;


/**
 * 国家Controller
 * Created by JQ棣 on 2018/5/3.
 */
@Controller
public class CountryController {

    @Autowired
    private CountryService countryService;

    @RequestMapping(value = "/admin/system/country", method = RequestMethod.GET)
    public String index(Model model, Country country) {
        if(country.getPage() == null) country.setPage(1L);
        model.addAttribute("search", country);
        model.addAttribute("pageModel", countryService.listAndCount(country));
        return "system/country";
    }

    @RequestMapping(value = "/admin/system/country/get", method = RequestMethod.POST)
    @ResponseBody
    public Country adminGet(Country country) {
        return countryService.get(country);
    }

    @RequestMapping(value = "/admin/system/country/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(Country.Save.class) Country country) {
        countryService.save(country);
        return null;
    }

    @RequestMapping(value = "/admin/system/country/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(Country country) {
        countryService.remove(country);
        return null;
    }

    @RequestMapping(value = "/admin/system/country/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(Country.Update.class) Country country) {
        countryService.update(country);
        return null;
    }

    @RequestMapping(value = "/admin/system/country/status/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdateStatus(Country country) {
        countryService.updateStatus(country);
        return null;
    }

}
