package com.df4j.xcframework.boot.datasource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SpringBoot datasource配置
 */
public class DynamicDatasourceProperties {

    // 是否启用自定义数据源配置
    private boolean enabled;
    // 默认数据源
    private String defaultKey;
    // 数据源配置
    private Map<String, DynamicDatasourceNode> datasources = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    public Map<String, DynamicDatasourceNode> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, DynamicDatasourceNode> datasources) {
        this.datasources = datasources;
    }

    /**
     * 数据源节点配置
     */
    public static class DynamicDatasourceNode {
        // 数据源类型
        private String type;

        // 路径匹配类型
        private String matchType;

        // 适用的module名称
        private List<String> modules;

        // 适用的包
        private List<String> packages;

        // 主节点key
        private String master;

        // 开启读写分离，支持读库
        private boolean enableReadNodes = false;

        // 详细节点配置
        private Map<String, Map<String, Object>> nodes = new LinkedHashMap<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMatchType() {
            return matchType;
        }

        public void setMatchType(String matchType) {
            this.matchType = matchType;
        }

        public List<String> getModules() {
            return modules;
        }

        public void setModules(List<String> modules) {
            this.modules = modules;
        }

        public List<String> getPackages() {
            return packages;
        }

        public void setPackages(List<String> packages) {
            this.packages = packages;
        }

        public String getMaster() {
            return master;
        }

        public void setMaster(String master) {
            this.master = master;
        }

        public boolean isEnableReadNodes() {
            return enableReadNodes;
        }

        public void setEnableReadNodes(boolean enableReadNodes) {
            this.enableReadNodes = enableReadNodes;
        }

        public Map<String, Map<String, Object>> getNodes() {
            return nodes;
        }

        public void setNodes(Map<String, Map<String, Object>> nodes) {
            this.nodes = nodes;
        }
    }
}
