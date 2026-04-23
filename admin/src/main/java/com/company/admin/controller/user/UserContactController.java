package com.company.admin.controller.user;



import com.company.admin.service.user.UserContactService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.user.User;
import com.company.admin.entity.user.UserContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 联系人Controller
 * Created by JQ棣 on 2018/11/19.
 */
@Controller
public class UserContactController {

    @Autowired
    private UserContactService userContactService;

    @RequestMapping(value = "/admin/user/userContact", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, UserContact userContact) {
        model.addAttribute("search", userContact);
        model.addAttribute("pageModel", userContactService.listAndCount(userContact));
        model.addAttribute("user", new User(userContact.getUserId()));
        return "user/user_detail_contact";
    }

    @RequestMapping(value = "/admin/user/userContact/get", method = RequestMethod.POST)
    @ResponseBody
    public UserContact get(UserContact userContact) {
        return userContactService.get(userContact);
    }

    @RequestMapping(value = "/admin/user/userContact/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(UserContact.Save.class) UserContact userContact) {
        userContactService.save(userContact);
        return null;
    }

    @RequestMapping(value = "/admin/user/userContact/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(UserContact userContact) {
        userContactService.remove(userContact);
        return null;
    }

    @RequestMapping(value = "/admin/user/userContact/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(UserContact.Update.class) UserContact userContact) {
        userContactService.update(userContact);
        return null;
    }
}
