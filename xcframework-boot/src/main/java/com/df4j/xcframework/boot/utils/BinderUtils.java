package com.df4j.xcframework.boot.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class BinderUtils {
    public static <T> T binder(Environment environment, String type, Class<T> clazz) {
        Assert.notNull(clazz, "clazz对象不能为空");
        Assert.notNull(environment, "environment对象不能为空");
        T obj = BeanUtils.instantiateClass(clazz);
        binder(environment, type, obj);
        return obj;
    }

    public static <T> T binder(Environment environment, String[] types, Class<T> clazz) {
        Assert.notNull(types, "types不能为空");
        T obj = null;
        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                obj = binder(environment, types[i], clazz);
            } else {
                binder(environment, types[i], obj);
            }
        }
        return obj;
    }

    public static void binder(Environment environment, String type, Object obj) {
        Assert.notNull(obj, "obj对象不能为空");
        Assert.notNull(environment, "environment对象不能为空");
        Binder.get(environment).bind(type, Bindable.ofInstance(obj));
    }

    public static void binder(Environment environment, String[] types, Object obj) {
        Assert.notNull(types, "types不能为空");
        for (int i = 0; i < types.length; i++) {
            binder(environment, types[i], obj);
        }
    }
}
