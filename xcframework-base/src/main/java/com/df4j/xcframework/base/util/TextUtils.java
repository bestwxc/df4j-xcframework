package com.df4j.xcframework.base.util;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 文本工具类
 */
public class TextUtils {

    /**
     * 使用map中的键值对替换模板中的值
     * 模板中，使用${key}的方式占位
     * @param template
     * @param values
     * @return
     */
    public static String replace(String template, Map values){
        if(ObjectUtils.isEmpty(values)) {
            return template;
        }
        StringSubstitutor stringSubstitutor = new StringSubstitutor(values);
        return stringSubstitutor.replace(template);
    }
}
