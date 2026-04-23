package com.company.admin.controller.system;



import com.company.admin.service.system.DictionaryService;

import com.company.admin.entity.system.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author JQ棣
 * @date 2018/9/23
 */
@RestController


@RequestMapping("/admin/system/dictData")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @PostMapping("/save")
    public Void save(@Validated(Dictionary.Save.class) Dictionary dictionary) {
        dictionaryService.save(dictionary);
        return null;
    }

    @RequestMapping("/remove")
    public Void delete(@NotNull Long id) {
        dictionaryService.deleteById(id);
        return null;
    }

    @RequestMapping("/get")
    public Dictionary find(@NotNull Long id) {
        return dictionaryService.findById(id);
    }

    @PostMapping("/update")
    public Void update(@Validated(Dictionary.Update.class) Dictionary dictionary) {
        dictionaryService.update(dictionary);
        return null;
    }

    @RequestMapping("/lock/update")
    public Void updateLock(@NotNull Long id) {
        dictionaryService.updateLock(id);
        return null;
    }

    @RequestMapping(value = "/reload")
    @ResponseBody
    public Void reload() {
        dictionaryService.invalidateCache();
        return null;
    }
}
