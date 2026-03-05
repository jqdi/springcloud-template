package com.company.system.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.system.entity.SysConfig;
import com.company.system.mapper.SysConfigMapper;

/**
 * 审计字段测试
 */
@RestController
@RequestMapping("/auditTest")
public class AuditTestController {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @GetMapping("/mybatis-plus-insert")
    public Map<String, String> mybatisPlusInsert(String code, String value) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setCode(code);
        sysConfig.setValue(value);
        sysConfigMapper.insert(sysConfig);
        Map<String, String> result = Collections.singletonMap("value", sysConfig.getValue());
        return result;
    }

    @GetMapping("/sql-insert")
    public Map<String, String> sqlInsert(String code, String value) {
        sysConfigMapper.insertConfig(value, code);
//        sysConfigMapper.insertConfig2(value, code);
        Map<String, String> result = Collections.singletonMap("value", "1");
        return result;
    }

    @GetMapping("/sql-insertOrUpdate")
    public Map<String, String> sqlInsertOrUpdate(String code, String value) {
        sysConfigMapper.insertOrUpdateConfig(value, code);
        Map<String, String> result = Collections.singletonMap("value", "1");
        return result;
    }

    @GetMapping("/mybatis-plus-update")
    public Map<String, String> mybatisPlusUpdate(Integer id, String code, String value) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setId(id);
        sysConfig.setCode(code);
        sysConfig.setValue(value);
        sysConfigMapper.updateById(sysConfig);
        Map<String, String> result = Collections.singletonMap("value", sysConfig.getValue());
        return result;
    }

    @GetMapping("/sql-update")
    public Map<String, String> sqlUpdate(String code, String value) {
        sysConfigMapper.updateValueByCode(value, code);
        Map<String, String> result = Collections.singletonMap("value", "1");
        return result;
    }
}
