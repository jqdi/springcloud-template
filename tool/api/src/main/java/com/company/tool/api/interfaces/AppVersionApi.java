package com.company.tool.api.interfaces;


import java.util.List;

import com.company.common.response.PageResp;
import com.company.tool.api.request.AppVersionReq;
import com.company.tool.api.response.AppVersionCheckResp;
import com.company.tool.api.response.AppVersionResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AppVersionApi {

    /**
     * 检查
     *
     * @param appCode
     * @param currentVersion
     * @return
     */
    @GetMapping("/check")
    AppVersionCheckResp check(@RequestParam("appCode") String appCode, @RequestParam("currentVersion") String currentVersion);

    /**
     * 分页查询
     */
    @GetMapping("/page")
    PageResp<AppVersionResp> page(@RequestParam("current") Long current, @RequestParam("size") Long size,
        @RequestParam(value = "appCode", required = false) String appCode,
        @RequestParam(value = "version", required = false) String version);

    /**
     * 列表查询
     */
    @GetMapping("/list")
    List<AppVersionResp> list(@RequestParam(value = "appCode", required = false) String appCode,
        @RequestParam(value = "version", required = false) String version);

    /**
     * 详情
     */
    @GetMapping("/query")
    AppVersionResp query(@RequestParam("id") Integer id);

    /**
     * 新增版本
     */
    @PostMapping("/save")
    Boolean save(@RequestBody AppVersionReq req);

    /**
     * 编辑版本
     */
    @PostMapping("/update")
    Boolean update(@RequestBody AppVersionReq req);

    /**
     * 删除版本
     */
    @PostMapping("/remove")
    Boolean remove(@RequestBody List<Integer> idList);

    /**
     * 统计所有灰度中应用的已升级设备数（供定时任务调用）
     */
    @PostMapping("/statAllUpgradedDevices")
    Boolean statAllUpgradedDevices();
}
