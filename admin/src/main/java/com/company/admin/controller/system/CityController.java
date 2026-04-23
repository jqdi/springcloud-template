package com.company.admin.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.system.City;
import com.company.admin.service.system.CityService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * 城市Controller
 * Created by JQ棣 on 2017/11/13.
 */
@Controller
public class CityController {

    @Autowired
    private CityService cityService;

    @RequestMapping(value = "/admin/system/city", method = RequestMethod.GET)
    public String index(Model model, City city) throws JsonProcessingException {
        if (city.getPage() == null) city.setPage(1L);
        model.addAttribute("search", city);
        model.addAttribute("pageModel", cityService.listAndCount(city));
        return "system/city";
    }

    @RequestMapping(value = "/admin/system/city/get", method = RequestMethod.POST)
    @ResponseBody
    public City adminGet(City city) {
        return cityService.get(city);
    }

    @RequestMapping(value = "/admin/system/city/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(City.Save.class) City city) {
        cityService.save(city);
        return null;
    }

    @RequestMapping(value = "/admin/system/city/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(City city) {
        cityService.remove(city);
        return null;
    }

    @RequestMapping(value = "/admin/system/city/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(City.Update.class) City city) {
        cityService.update(city);
        return null;
    }

    @RequestMapping(value = "/admin/system/city/status/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdateStatus(City city) {
        cityService.updateStatus(city);
        return null;
    }

    @RequestMapping(value = "/admin/system/city/district/status/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdateDistrictStatus(City city) {
        cityService.updateDistrictStatus(city);
        return null;
    }

    @RequestMapping(value = "/admin/system/city/list", method = RequestMethod.POST)
    @ResponseBody
    public List<City> adminCityList(City city) {
        return cityService.listCombo(city);
    }

}
