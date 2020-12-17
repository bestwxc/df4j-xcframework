package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import org.springframework.core.io.Resource;

/**
 * 基于Json的配置工具类
 *
 * @param <T>
 */
public class JsonConfigFileUtils<T> {

    private Class<T> clazz;
    private Resource resource;
    private T configuration = null;

    private JsonConfigFileUtils(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 构造函数
     *
     * @param resource
     * @param clazz
     */
    public JsonConfigFileUtils(Resource resource, Class<T> clazz) {
        this(clazz);
        this.resource = resource;
    }

    /**
     * 读取解析配置类
     *
     * @return
     */
    private T readConfiguration() {
        try {
            String content = FileUtils.readFile(resource.getFile());
            T t = JsonUtils.parse(content, clazz);
            return t;
        } catch (Exception e) {
            throw new XcException("解析配置文件异常", e);
        }
    }

    /**
     * 返回解析后的配置类
     *
     * @param reload
     * @return
     */
    public synchronized T getConfiguration(boolean reload) {
        if (configuration != null && !reload) {
            return this.configuration;
        }
        return this.readConfiguration();
    }

    /**
     * 返回解析后的配置类
     *
     * @return
     */
    public T getConfiguration() {
        return this.getConfiguration(false);
    }
}
