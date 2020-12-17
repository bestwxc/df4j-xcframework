package com.df4j.xcframework.base.util;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class MapUtils {

    /**
     * 从map中获取对象
     *
     * @param map
     * @param key
     * @return
     */
    public Object getObject(Map map, String key) {
        checkMapAndKey(map, key);
        return map.get(key);
    }

    /**
     * 从map中获取字符串
     *
     * @param map
     * @param key
     * @param trim
     * @param allowNull
     * @param defaultValue
     * @return
     */
    public static String getString(Map map, String key, boolean trim, boolean allowNull, String defaultValue) {
        checkMapAndKey(map, key);
        Object obj = map.get(key);
        boolean isNull = ObjectUtils.isEmpty(obj);
        if (!isNull && obj instanceof String && trim) {
            isNull = ObjectUtils.isEmpty(String.valueOf(obj).trim());
        }
        Assert.isTrue(!isNull || allowNull, String.format("从map中获取不到值,key:%s", key));
        return isNull ? defaultValue : String.valueOf(obj);
    }


    /**
     * 从map中获取字符串
     *
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Map map, String key, String defaultValue) {
        return getString(map, key, true, true, defaultValue);
    }

    /**
     * 从map中获取字符串
     *
     * @param map
     * @param key
     * @return
     */
    public static String getString(Map map, String key) {
        return getString(map, key, true, false, null);
    }


    /**
     * 校验map和key
     *
     * @param map
     * @param key
     */
    private static void checkMapAndKey(Map map, String key) {
        Assert.notNull(map, "map不能为空!");
        Assert.isTrue(!com.df4j.xcframework.base.util.StringUtils.isEmpty(key), "key不能为空!");
    }

    /**
     * 从Map中获取指定类型的对象
     *
     * @param map
     * @param key
     * @param tClass
     * @param allowNull
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T get(Map map, String key, Class<T> tClass, boolean allowNull, T defaultValue) {
        Assert.notNull(tClass, "class类型不能为空!");
        Assert.isTrue(!tClass.equals(String.class), "class类型不能为String!");
        Object obj = getObject(map, key);

        if (ObjectUtils.isEmpty(obj)) {
            Assert.isTrue(allowNull, String.format("从map中获取不到值,key:%s", key));
            return defaultValue;
        } else {
            if (tClass.isAssignableFrom(Number.class)) {
                Method method = ReflectionUtils.findMethod(tClass, "valueOf");
                return (T) ReflectionUtils.invokeMethod(method, null, obj);
            } else {
                return (T) obj;
            }
        }
    }

}
