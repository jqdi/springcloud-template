package com.company.system.controller;

import cn.hutool.json.JSONUtil;
import com.company.system.api.response.RouterResp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SysMenuControllerTest {

    @Autowired
    private SysMenuController sysMenuController;

    @Test
    void getRouters() {
        List<RouterResp> respList = sysMenuController.getRouters(1);
        System.out.println(JSONUtil.toJsonPrettyStr(respList));
    }
}