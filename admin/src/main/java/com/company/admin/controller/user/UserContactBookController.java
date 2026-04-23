package com.company.admin.controller.user;



import com.company.admin.service.user.UserContactBookService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.user.User;
import com.company.admin.entity.user.UserContactBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 通讯录Controller
 * Created by JQ棣 on 2018/11/19.
 */
@Controller
public class UserContactBookController {

    @Autowired
    private UserContactBookService userContactBookService;

    @RequestMapping(value = "/admin/user/userContactBook", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, UserContactBook userContactBook) {
        model.addAttribute("search", userContactBook);
        model.addAttribute("pageModel", userContactBookService.listAndCount(userContactBook));
        model.addAttribute("user", new User(userContactBook.getUserId()));
        return "user/user_detail_contactBook";
    }

    @RequestMapping(value = "/admin/user/userContactBook/get", method = RequestMethod.POST)
    @ResponseBody
    public UserContactBook get(UserContactBook userContactBook) {
        return userContactBookService.get(userContactBook);
    }

    @RequestMapping(value = "/admin/user/userContactBook/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(UserContactBook.Save.class) UserContactBook userContactBook) {
        userContactBookService.save(userContactBook);
        return null;
    }

    @RequestMapping(value = "/admin/user/userContactBook/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(UserContactBook userContactBook) {
        userContactBookService.remove(userContactBook);
        return null;
    }

    @RequestMapping(value = "/admin/user/userContactBook/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(UserContactBook.Update.class) UserContactBook userContactBook) {
        userContactBookService.update(userContactBook);
        return null;
    }
}
