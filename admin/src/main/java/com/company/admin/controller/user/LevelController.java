package com.company.admin.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.user.Level;
import com.company.admin.service.user.LevelService;

/**
 * 等级称号Controller
 * Created by JQ棣 on 2018/06/21.
 */
@Controller
public class LevelController {

    @Autowired
    private LevelService levelService;

    @RequestMapping(value = "/admin/user/level", method = RequestMethod.GET)
    public String index(Model model, Level level) {
        if (level.getPage() == null) {
            level.setPage(1L);
        }
        model.addAttribute("search", level);
        model.addAttribute("pageModel", levelService.listAndCount(level));
        return "user/level";
    }

    @RequestMapping(value = "/admin/user/level/get", method = RequestMethod.POST)
    @ResponseBody
    public Level get(Level level) {
        return levelService.get(level);
    }

    @RequestMapping(value = "/admin/user/level/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(Level.Save.class) Level level) {
        levelService.save(level);
        return null;
    }

    @RequestMapping(value = "/admin/user/level/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(Level level) {
        levelService.remove(level);
        return null;
    }

    @RequestMapping(value = "/admin/user/level/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(Level.Update.class) Level level) {
        levelService.update(level);
        return null;
    }
}
