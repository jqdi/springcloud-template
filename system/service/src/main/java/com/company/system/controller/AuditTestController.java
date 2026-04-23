package com.company.system.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.company.system.service.SysConfigService;
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
    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/mybatis-plus-insert")
    public Map<String, String> mybatisPlusInsert() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setCode(RandomUtil.randomString(10));
        sysConfig.setValue(RandomUtil.randomString(10));
        sysConfigMapper.insert(sysConfig);
        Map<String, String> result = Collections.singletonMap("value", sysConfig.getValue());
        return result;
    }

    @GetMapping("/mybatis-plus-insert-batch")
    public Map<String, String> mybatisPlusInsertBatch() {
        List<SysConfig> sysConfigList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SysConfig sysConfig = new SysConfig();
            sysConfig.setCode(RandomUtil.randomString(10));
            sysConfig.setValue(RandomUtil.randomString(10));
            sysConfigList.add(sysConfig);
        }
        sysConfigService.saveBatch(sysConfigList);
        Map<String, String> result = Collections.singletonMap("value", "1");
        return result;
    }

    @GetMapping("/sql-insert")
    public Map<String, String> sqlInsert() {
        sysConfigMapper.insertConfig(RandomUtil.randomString(10), RandomUtil.randomString(10));
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
