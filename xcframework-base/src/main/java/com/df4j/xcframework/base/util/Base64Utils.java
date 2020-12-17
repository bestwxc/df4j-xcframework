package com.df4j.xcframework.base.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64工具类
 */
public class Base64Utils {

    /**
     * 将字节数组转化Base64字符串
     * @param b
     * @return
     */
    public static String encodeBase64String(byte[] b){
        return Base64.encodeBase64String(b);
    }

    /**
     * 将字符串转化为Base64字符串
     * @param s
     * @return
     */
    public static String encodeBase64String(String s){
        return encodeBase64String(s.getBytes());
    }

    /**
     * 将Base64字符串转化为字节数组
     * @param base64String
     * @return
     */
    public static byte[] decodeBase64(String base64String){
        return Base64.decodeBase64(base64String);
    }

    /**
     * 将Base64字节数组转化为原字节数组
     * @param base64Data
     * @return
     */
    public static byte[] decodeBase64(byte[] base64Data){
        return Base64.decodeBase64(base64Data);
    }
}
