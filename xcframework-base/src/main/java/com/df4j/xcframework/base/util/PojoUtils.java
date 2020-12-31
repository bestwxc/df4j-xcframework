package com.df4j.xcframework.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PojoUtils {

    private static Logger logger = LoggerFactory.getLogger(PojoUtils.class);

    public static List fromList(Class clazz, List sourceList) {
        List result = new ArrayList();
        for (int i = 0; i < sourceList.size(); i++) {
            Object t = from(clazz, sourceList.get(i));
            result.add(t);
        }
        return result;
    }

    public static <T> T from(Class<T> clazz, Object... sources) {
        T t = BeanUtils.instantiateClass(clazz);
        return from(t, sources);
    }


    public static <T> T from(T target, Object... sources) {
        final Class clazz = target.getClass();
        ReflectionUtils.doWithFields(clazz, field -> {
            for (int i = 0; i < sources.length; i++) {
                final Class sourceClazz = sources[i].getClass();
                fromField(target, sourceClazz, field);
            }
        });
        return target;
    }

    public static <T> T from(Class<T> clazz, Object source) {
        T t = BeanUtils.instantiateClass(clazz);
        return from(t, source);
    }

    public static <T> T from(T target, Object source) {
        final Class clazz = target.getClass();
        ReflectionUtils.doWithFields(clazz, field -> fromField(target, source, field));
        return target;
    }

    public static void fromField(Object target, Object source, Field targetField) {
        try {
            final Class sourceClazz = source.getClass();
            String fieldName = targetField.getName();
            if("serialVersionUID".equals(fieldName)) {
                return;
            }
            Field sourceField = ReflectionUtils.findField(sourceClazz, fieldName);
            if (!ObjectUtils.isEmpty(sourceField)) {
                sourceField.setAccessible(true);
                Object value = ReflectionUtils.getField(sourceField, source);
                targetField.setAccessible(true);
                ReflectionUtils.setField(targetField, target, value);
            }
        } catch (Exception e) {
            logger.error("设置目标pojo值错误,source:{}, target:{}, field:{}",
                    source.getClass().getName(), target.getClass().getName(), targetField.getName());
        }
    }
}
