package com.df4j.xcframework.base.util;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Bean工具类
 */
public class BeanUtils {
    /**
     * Bean深拷贝
     *
     * @param bean
     * @return
     */
    public static Object cloneBean(Object bean) {
        return cloneBean(bean, bean.getClass());
    }

    /**
     * Bean深拷贝
     *
     * @param bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T cloneBean(Object bean, Class<T> clazz) {
        Assert.notNull(bean, "bean对象不能为空");
        Assert.notNull(clazz, "class不能为空");
        Assert.isTrue(clazz.equals(bean.getClass()), "bean的类型与传入的clazz不一致");
        T newInstance = org.springframework.beans.BeanUtils.instantiateClass(clazz);
        org.springframework.beans.BeanUtils.copyProperties(bean, newInstance);
        return newInstance;
    }

    /**
     * 设置值
     *
     * @param object
     * @param fields
     * @param values
     */
    public static void setValues(Object object, String[] fields, Object[] values) {
        setValues(object, fields, values, false);
    }

    /**
     * 设置值
     *
     * @param object
     * @param fields
     * @param values
     * @param overwrite
     */
    public static void setValues(Object object, String[] fields, Object[] values, boolean overwrite) {
        for (int i = 0; i < fields.length; i++) {
            if (object instanceof Map) {
                Map temp = (Map) object;
                if (overwrite || !temp.containsKey(fields[i])) {
                    temp.put(fields[i], values[i]);
                }
            } else {
                Field field = ReflectionUtils.findField(object.getClass(), fields[i]);
                field.setAccessible(true);
                Object value = ReflectionUtils.getField(field, object);
                if (overwrite || ObjectUtils.isEmpty(value)) {
                    ReflectionUtils.setField(field, object, values[i]);
                }
            }
        }
    }
}
