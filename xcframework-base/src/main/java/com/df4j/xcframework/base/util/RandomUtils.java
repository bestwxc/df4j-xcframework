package com.df4j.xcframework.base.util;

import java.util.Random;


/**
 * 随机数生成工具类
 */
public class RandomUtils {

    private static final String NUMBERCHAR = "0123456789";
    private static final String LETTERCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 获取随机数
     *
     * @param length
     * @return
     */
    public static String getNumCode(int length) {
        return getCode(NUMBERCHAR, length);
    }

    /**
     * 获取字符型随机数
     *
     * @param length
     * @return
     */
    public static String getStringCode(int length) {
        return getCode(LETTERCHAR, length);
    }

    /**
     * 获取随机串
     *
     * @param from
     * @param length
     * @return
     */
    private static String getCode(String from, int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(from.charAt(random.nextInt(from.length())));
        }
        return sb.toString();
    }
}
