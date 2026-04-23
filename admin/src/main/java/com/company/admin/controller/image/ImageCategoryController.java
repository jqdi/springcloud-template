package com.company.admin.controller.image;



import com.company.admin.entity.system.Dictionary;
import com.company.admin.service.image.ImageCategoryService;
import com.company.admin.service.system.DictionaryService;

import com.company.admin.entity.image.ImageCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 图片分类Controller
 * Created by JQ棣 on 2017/10/23.
 */
@Controller
public class ImageCategoryController {

    @Autowired
    private ImageCategoryService imageCategoryService;

    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping(value = "/admin/content/imageCategory", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("imageJumpTypeList", dictionaryService.mapByCategory("imageJumpType"));
        return "content/imageCategory";
    }

    @RequestMapping(value = "/admin/content/imageCategory/tree", method = RequestMethod.POST)
    @ResponseBody
    public List<ImageCategory> adminTree(ImageCategory imageCategory) {
        imageCategory = imageCategory == null ? new ImageCategory() : imageCategory;
        return imageCategoryService.tree(imageCategory);
    }

    @RequestMapping(value = "/admin/content/imageCategory/get", method = RequestMethod.POST)
    @ResponseBody
    public ImageCategory adminGet(ImageCategory imageCategory) {
        ImageCategory i = imageCategoryService.get(imageCategory);
        return i;
    }

    @RequestMapping(value = "/admin/content/imageCategory/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(ImageCategory.Save.class) @RequestBody ImageCategory imageCategory) {
        imageCategory.setLock(0);
        imageCategoryService.save(imageCategory);
        return null;
    }

    @RequestMapping(value = "/admin/content/imageCategory/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(ImageCategory imageCategory) {
        imageCategoryService.remove(imageCategory);
        return null;
    }

    @RequestMapping(value = "/admin/content/imageCategory/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(ImageCategory.Update.class) @RequestBody ImageCategory imageCategory)
            {
        imageCategoryService.update(imageCategory);
        return null;
    }

    @RequestMapping(value = "/admin/content/imageCategory/lock", method = RequestMethod.POST)
    @ResponseBody
    public Void adminLock(ImageCategory imageCategory)
            {
        imageCategoryService.updateLock(imageCategory);
        return null;
    }

    @RequestMapping(value = "/admin/content/imageCategory/jumpType/list", method = RequestMethod.POST)
    @ResponseBody
    public List<Dictionary> listJumptype(ImageCategory imageCategory) {
        return imageCategoryService.listJumpType(imageCategory);
    }
}
