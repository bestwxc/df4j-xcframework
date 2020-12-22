package com.df4j.xcframework.boot.utils;

import com.df4j.xcframework.base.exception.XcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FactoryConfigUtils {
    private static Logger logger = LoggerFactory.getLogger(FactoryConfigUtils.class);

    private static ClassLoader getClassLoader() {
        return ClassUtils.getDefaultClassLoader();
    }


    public static Map<String, List<String>> loadSpringFactories() {
        return loadSpringFactories(getClassLoader());
    }

    public static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
        Method method = null;
        try {
            method = SpringFactoriesLoader.class.getDeclaredMethod("loadSpringFactories", ClassLoader.class);
        } catch (Exception e) {
            throw new XcException("反射未正常获取到SpringFactoriesLoader中的loadSpringFactories方法", e);
        }
        method.setAccessible(true);
        Map<String, List<String>> res = (Map<String, List<String>>) ReflectionUtils.invokeMethod(method, null, classLoader);
        return res;
    }

    public static List<String> loadFactoryNames(String factoryTypeName) {
        return loadFactoryNames(factoryTypeName, getClassLoader());
    }

    public static List<String> loadFactoryNames(String factoryTypeName, @Nullable ClassLoader classLoader) {
        return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
    }
}
