package com.company.admin.controller.user;



import com.company.admin.service.system.ConfigService;
import com.company.admin.service.user.UserMapPositionService;
import com.company.admin.annotation.Pagination;

import com.company.admin.entity.user.User;
import com.company.admin.entity.user.UserMapPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 定位Controller
 * Created by JQ棣 on 2018/11/20.
 */
@Controller
public class UserMapPositionController {

    @Autowired
    private UserMapPositionService userMapPositionService;

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/admin/user/userMapPosition", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, UserMapPosition userMapPosition) {
        model.addAttribute("search", userMapPosition);
        model.addAttribute("pageModel", userMapPositionService.listAndCount(userMapPosition));
        model.addAttribute("user", new User(userMapPosition.getUserId()));
        model.addAttribute("mapKey", configService.findByKey("mapKey"));
        return "user/userMapPosition";
    }

    @RequestMapping(value = "/admin/user/userMapPosition/get", method = RequestMethod.POST)
    @ResponseBody
    public UserMapPosition get(UserMapPosition userMapPosition) {
        return userMapPositionService.get(userMapPosition);
    }

//	@RequestMapping(value = "/admin/user/userMapPosition/save", method = RequestMethod.POST)
//	@ResponseBody
//	public Void save(@Validated(UserMapPosition.Save.class) UserMapPosition userMapPosition) {
//		userMapPositionService.save(userMapPosition);
//		return null;
//	}

    @RequestMapping(value = "/admin/user/userMapPosition/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(UserMapPosition userMapPosition) {
        userMapPositionService.remove(userMapPosition);
        return null;
    }

    @RequestMapping(value = "/admin/user/userMapPosition/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(UserMapPosition.Update.class) UserMapPosition userMapPosition) {
        userMapPositionService.update(userMapPosition);
        return null;
    }
}
