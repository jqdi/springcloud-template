package com.company.gateway.gray;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 灰度路由配置属性。
 *
 * <p>绑定前缀 {@code gray}，支持按环境区分开关、染色规则、版本匹配等配置。
 */
@ConfigurationProperties(prefix = "gray")
public class GrayProperties {

    /** 灰度路由总开关 */
    private boolean enabled = false;

    /** 灰度请求头名（逗号分隔），从这些头中提取版本号 */
    private String headers = "x-gray-version";

    /** 实例 metadata 中版本字段名 */
    private String versionMetadataKey = "version";

    /** 实例 metadata 中权重字段名 */
    private String weightMetadataKey = "weight";

    /** 默认权重（实例无 weight metadata 时使用） */
    private int defaultWeight = 100;

    /** 无匹配版本实例时是否回退到基线实例 */
    private boolean fallbackToBaseline = true;

    /** 染色规则列表，按配置顺序匹配，命中第一个即染色 */
    private List<DyeRule> dyeRules = new ArrayList<>();

    /**
     * 染色规则定义。
     */
    public static class DyeRule {

        /** 规则类型：header | whitelist | percent */
        private String type;

        /** type=header 时，请求头名 */
        private String headerName;

        /** type=header 时，请求头期望值 */
        private String headerValue;

        /** type=whitelist 时，用户ID所在请求头名 */
        private String userHeader;

        /** type=whitelist 时，逗号分隔的用户ID白名单 */
        private String userIds;

        /** type=percent 时，灰度百分比 0-100 */
        private int percent;

        /** 命中后染色的目标版本号 */
        private String version;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderValue() {
            return headerValue;
        }

        public void setHeaderValue(String headerValue) {
            this.headerValue = headerValue;
        }

        public String getUserHeader() {
            return userHeader;
        }

        public void setUserHeader(String userHeader) {
            this.userHeader = userHeader;
        }

        public String getUserIds() {
            return userIds;
        }

        public void setUserIds(String userIds) {
            this.userIds = userIds;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getVersionMetadataKey() {
        return versionMetadataKey;
    }

    public void setVersionMetadataKey(String versionMetadataKey) {
        this.versionMetadataKey = versionMetadataKey;
    }

    public String getWeightMetadataKey() {
        return weightMetadataKey;
    }

    public void setWeightMetadataKey(String weightMetadataKey) {
        this.weightMetadataKey = weightMetadataKey;
    }

    public int getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(int defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public boolean isFallbackToBaseline() {
        return fallbackToBaseline;
    }

    public void setFallbackToBaseline(boolean fallbackToBaseline) {
        this.fallbackToBaseline = fallbackToBaseline;
    }

    public List<DyeRule> getDyeRules() {
        return dyeRules;
    }

    public void setDyeRules(List<DyeRule> dyeRules) {
        this.dyeRules = dyeRules;
    }
}
