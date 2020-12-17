package com.df4j.xcframework.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean、Map互转工具类
 */
public class BeanMapUtils {

    private static Logger logger = LoggerFactory.getLogger(BeanMapUtils.class);

    /**
     * 将对象转换成map
     *
     * @param bean
     * @return
     */
    public static Map<String, ?> toMap(Object bean) {
        Map<String, Object> map = new HashMap<String, Object>();
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            field.setAccessible(true);
            map.put(field.getName(), ReflectionUtils.getField(field, bean));
        });
        return map;
    }

    /**
     * 将map转换成对象
     *
     * @param clazz
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T toBean(Class<T> clazz, Map<String, ?> map) {
        Assert.notNull(clazz, "转换的目标class不能为空");
        T t = BeanUtils.instantiateClass(clazz);
        if (!ObjectUtils.isEmpty(map)) {
            Map temp = new HashMap();
            temp.putAll(map);
            ReflectionUtils.doWithFields(clazz, field -> {
                field.setAccessible(true);
                String key = field.getName();
                if (temp.containsKey(key)) {
                    ReflectionUtils.setField(field, t, temp.remove(key));
                }
            });
            if (!ObjectUtils.isEmpty(temp)) {
                logger.info("map转换为Bean操作存在未转换的键值,class:{}, map: {}", clazz, map);
            }
        }
        return t;
    }
}
