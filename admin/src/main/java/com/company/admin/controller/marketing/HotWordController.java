package com.company.admin.controller.marketing;



import com.company.admin.service.marketing.HotWordService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.marketing.HotWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 热搜词Controller
 * Created by JQ棣 on 2018/11/07.
 */
@Controller
public class HotWordController {

    @Autowired
    private HotWordService hotWordService;

    @RequestMapping(value = "/admin/marketing/hotWord", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, HotWord hotWord) {
        model.addAttribute("search", hotWord);
        model.addAttribute("pageModel", hotWordService.listAndCount(hotWord));
        return "marketing/hotWord";
    }

    @RequestMapping(value = "/admin/marketing/hotWord/get", method = RequestMethod.POST)
    @ResponseBody
    public HotWord get(HotWord hotWord) {
        return hotWordService.get(hotWord);
    }

    @RequestMapping(value = "/admin/marketing/hotWord/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(HotWord.Save.class) HotWord hotWord) {
        hotWordService.save(hotWord);
        return null;
    }

    @RequestMapping(value = "/admin/marketing/hotWord/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(HotWord hotWord) {
        hotWordService.remove(hotWord);
        return null;
    }

    @RequestMapping(value = "/admin/marketing/hotWord/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(HotWord.Update.class) HotWord hotWord) {
        hotWordService.update(hotWord);
        return null;
    }

    @RequestMapping(value = "/admin/marketing/hotWord/status/update", method = RequestMethod.POST)
    @ResponseBody
    public Void updateStatus(HotWord hotWord) {
        hotWordService.update(hotWord);
        return null;
    }
}
