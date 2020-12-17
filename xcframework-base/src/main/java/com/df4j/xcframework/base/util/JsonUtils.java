package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ObjectUtils;

import static com.df4j.xcframework.base.constant.Constants.EMPTY_STRING;

/**
 * Json工具类
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取JavaType
     *
     * @param parametrized
     * @param parameterClasses
     * @return
     */
    public static JavaType getJavaType(Class<?> parametrized, Class... parameterClasses) {
        return objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 将Json转化为字符串
     *
     * @param object
     * @param ignoreError
     * @return
     */
    public static String stringify(Object object, boolean ignoreError) {
        if (ObjectUtils.isEmpty(object)) {
            return EMPTY_STRING;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            if (!ignoreError) {
                throw new XcException("将对象转换为Json出错", e);
            }
        }
        return EMPTY_STRING;
    }

    /**
     * 将对象转化为字符串
     *
     * @param object
     * @return
     */
    public static String stringify(Object object) {
        return stringify(object, true);
    }

    /**
     * 将Json 字符串转化为对象
     *
     * @param json
     * @return
     */
    public static Object parse(String json) {
        return parse(json, Object.class);
    }

    /**
     * 将Json转化为对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parse(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new XcException("将Json转换为对象出错", e);
        }
    }

    /**
     * 将Json转化为对象
     *
     * @param json
     * @param javaType
     * @param <T>
     * @return
     */
    public static <T> T parse(String json, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new XcException("将Json转换为对象出错", e);
        }
    }
}
