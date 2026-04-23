package com.company.admin.controller.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.company.admin.annotation.Pagination;

import com.company.admin.entity.loan.ProductTips;
import com.company.admin.service.loan.ProductTipsService;

/**
 * 产品提示Controller
 * Created by JQ棣 on 2018/11/20.
 */
@Controller
public class ProductTipsController {

    @Autowired
    private ProductTipsService productTipsService;

    @RequestMapping(value = "/admin/loan/productTips", method = RequestMethod.GET)
    @Pagination
    public String index(Model model, ProductTips productTips) {
        model.addAttribute("search", productTips);
        model.addAttribute("pageModel", productTipsService.listAndCount(productTips));
        return "loan/productTips";
    }

    @RequestMapping(value = "/admin/loan/productTips/get", method = RequestMethod.POST)
    @ResponseBody
    public ProductTips get(ProductTips productTips) {
        return productTipsService.get(productTips);
    }

    @RequestMapping(value = "/admin/loan/productTips/save", method = RequestMethod.POST)
    @ResponseBody
    public Void save(@Validated(ProductTips.Save.class) ProductTips productTips) {
        productTipsService.save(productTips);
        return null;
    }

    @RequestMapping(value = "/admin/loan/productTips/remove", method = RequestMethod.POST)
    @ResponseBody
    public Void remove(ProductTips productTips) {
        productTipsService.remove(productTips);
        return null;
    }

    @RequestMapping(value = "/admin/loan/productTips/update", method = RequestMethod.POST)
    @ResponseBody
    public Void update(@Validated(ProductTips.Update.class) ProductTips productTips) {
        productTipsService.update(productTips);
        return null;
    }
}
