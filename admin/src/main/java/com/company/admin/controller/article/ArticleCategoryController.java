package com.company.admin.controller.article;



import com.company.admin.service.article.ArticleCategoryService;

import com.company.admin.entity.article.ArticleCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 文章分类
 * Created by JQ棣 on 10/30/17.
 */
@Controller
@RequestMapping("/admin/content/article/category")
public class ArticleCategoryController {
    @Autowired
    private ArticleCategoryService articleCategoryService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "content/articleCategory";
    }

    @RequestMapping(value = "/tree", method = RequestMethod.POST)
    @ResponseBody
    public List<ArticleCategory> adminTree(ArticleCategory articleCategory) {
        articleCategory = articleCategory == null ? new ArticleCategory() : articleCategory;
        return articleCategoryService.tree(articleCategory);
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public ArticleCategory adminGet(ArticleCategory articleCategory) {
        return articleCategoryService.get(articleCategory);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Void adminSave(@Validated(ArticleCategory.Save.class) ArticleCategory articleCategory) {
        articleCategory.setLock(0);
        articleCategoryService.save(articleCategory);
        return null;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void adminRemove(ArticleCategory articleCategory) {
        articleCategoryService.remove(articleCategory);
        return null;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Void adminUpdate(@Validated(ArticleCategory.Update.class) ArticleCategory articleCategory)
            {
        if (articleCategory.getParent() == null)
            articleCategory.setParent(new ArticleCategory());
        articleCategoryService.update(articleCategory);
        return null;
    }

    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    @ResponseBody
    public Void adminLock(ArticleCategory articleCategory)
            {
        articleCategoryService.updateLock(articleCategory);
        return null;
    }
}
