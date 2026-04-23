package com.company.system.api.response;

import lombok.Data;

import java.util.List;

@Data
public class RouterResp {
    /**
     * 路由名字
     */
    private String name;
    /**
     * 路由地址
     */
    private String path;
    /**
     * 是否隐藏路由，当设置 true 的时候该路由不会再侧边栏出现
     */
    private Boolean hidden;
    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;
    /**
     * 组件地址
     */
    private String component;
    /**
     * 其他元素
     */
    private Meta meta;
    /**
     * 子路由
     */
    private List<RouterResp> children;

    @Data
    public static class Meta {
        /**
         * 设置该路由在侧边栏和面包屑中展示的名字
         */
        private String title;
        /**
         * 设置该路由的图标，对应路径src/assets/icons/svg
         */
        private String icon;
    }
}
