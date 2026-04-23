package com.company.admin.controller.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.company.admin.entity.image.Image;
import com.company.admin.entity.image.ImageCategory;
import com.company.admin.service.image.ImageCategoryService;
import com.company.admin.service.image.ImageService;
import com.company.framework.util.JsonUtil;

/**
 * 图片Controller
 * Created by JQ棣 on 2017/10/26.
 */
@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageCategoryService imageCategoryService;

    @RequestMapping(value = "/admin/content/image", method = RequestMethod.GET)
    public String index(Model model, Image image) throws Exception {
        if(image.getPage() == null) {
            image.setPage(1L);
        }
        model.addAttribute("search", image);
        model.addAttribute("pageModel", imageService.listAndCount(image));
        model.addAttribute("categoryTree", JsonUtil.toJsonString(imageCategoryService.tree(new ImageCategory())));
        return "content/image";
    }

    @RequestMapping(value = "/admin/content/image/get", method = RequestMethod.POST)
    @ResponseBody
    public Image adminGet(Image image) {
        return imageService.get(image);
    }

    @RequestMapping(value = "/admin/content/image/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(Image.Save.class) Image image) {
        imageService.save(image);
        return null;
    }

    @RequestMapping(value = "/admin/content/image/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(Image image) {
        imageService.remove(image);
        return null;
    }

    @RequestMapping(value = "/admin/content/image/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(Image.Update.class) Image image) {
        imageService.update(image);
        return null;
    }
}
