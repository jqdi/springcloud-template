package com.company.framework.gray;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 灰度路由配置属性（框架层，镜像 gateway 包）。
 *
 * <p>支持两种路由模式：developer（开发调试）| release（灰度发布）。
 */
@ConfigurationProperties(prefix = "gray")
public class GrayProperties {

    /** 灰度路由总开关 */
    private boolean enabled = false;

    /** 路由模式：developer（开发调试）| release（灰度发布） */
    private String mode = "developer";

    /** developer 模式：请求头名（逗号分隔） */
    private String developerHeaders = "x-deviceid,Authorization";

    /** developer 模式：开发者标识在实例 metadata 中的字段名 */
    private String developerMetadataKey = "developer";

    /** release 模式：灰度请求头名（逗号分隔） */
    private String headers = "x-gray-version";

    /** release 模式：实例 metadata 中版本字段名 */
    private String versionMetadataKey = "version";

    /** release 模式：实例 metadata 中权重字段名 */
    private String weightMetadataKey = "weight";

    /** release 模式：默认权重 */
    private int defaultWeight = 100;

    /** release 模式：无匹配版本实例时是否回退到基线实例 */
    private boolean fallbackToBaseline = true;

    /** release 模式：染色规则列表（框架层通常不配，染色在网关层） */
    private List<DyeRule> dyeRules = new ArrayList<>();

    public static class DyeRule {
        private String type;
        private String headerName;
        private String headerValue;
        private String userHeader;
        private String userIds;
        private int percent;
        private String version;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getHeaderName() { return headerName; }
        public void setHeaderName(String headerName) { this.headerName = headerName; }
        public String getHeaderValue() { return headerValue; }
        public void setHeaderValue(String headerValue) { this.headerValue = headerValue; }
        public String getUserHeader() { return userHeader; }
        public void setUserHeader(String userHeader) { this.userHeader = userHeader; }
        public String getUserIds() { return userIds; }
        public void setUserIds(String userIds) { this.userIds = userIds; }
        public int getPercent() { return percent; }
        public void setPercent(int percent) { this.percent = percent; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getDeveloperHeaders() { return developerHeaders; }
    public void setDeveloperHeaders(String developerHeaders) { this.developerHeaders = developerHeaders; }
    public String getDeveloperMetadataKey() { return developerMetadataKey; }
    public void setDeveloperMetadataKey(String developerMetadataKey) { this.developerMetadataKey = developerMetadataKey; }
    public String getHeaders() { return headers; }
    public void setHeaders(String headers) { this.headers = headers; }
    public String getVersionMetadataKey() { return versionMetadataKey; }
    public void setVersionMetadataKey(String versionMetadataKey) { this.versionMetadataKey = versionMetadataKey; }
    public String getWeightMetadataKey() { return weightMetadataKey; }
    public void setWeightMetadataKey(String weightMetadataKey) { this.weightMetadataKey = weightMetadataKey; }
    public int getDefaultWeight() { return defaultWeight; }
    public void setDefaultWeight(int defaultWeight) { this.defaultWeight = defaultWeight; }
    public boolean isFallbackToBaseline() { return fallbackToBaseline; }
    public void setFallbackToBaseline(boolean fallbackToBaseline) { this.fallbackToBaseline = fallbackToBaseline; }
    public List<DyeRule> getDyeRules() { return dyeRules; }
    public void setDyeRules(List<DyeRule> dyeRules) { this.dyeRules = dyeRules; }

    public boolean isDeveloperMode() { return "developer".equalsIgnoreCase(mode); }
    public boolean isReleaseMode() { return "release".equalsIgnoreCase(mode); }
}
