package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化工具类
 */
public class DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_NUM_PATTERN = "yyyyMMdd";
    public static final String TIME_NUM_PATTERN = "HHmmss";

    /**
     * 将日期格式化为默认字符串形式
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, DATE_TIME_PATTERN);
    }

    /**
     * 将日期格式化为指定格式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 将字符串按照默认格式转化
     *
     * @param dateString
     * @return
     */
    public static Date parse(String dateString) {
        return parse(dateString, DATE_TIME_PATTERN);
    }

    /**
     * 将字符串转化为Date
     *
     * @param dateString
     * @param pattern
     * @return
     */
    public static Date parse(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new XcException("日期转换错误！", e);
        }
    }

    /**
     * 获取当前时间的格式化字符串
     *
     * @param pattern
     * @return
     */
    public static String now(String pattern) {
        Date now = new Date();
        return format(now, pattern);
    }

    /**
     * 获取当前时间的格式化字符串
     *
     * @return
     */
    public static String now() {
        return now(DATE_TIME_PATTERN);
    }


    /**
     * 获取当前日期的格式化字符串
     *
     * @return
     */
    public static String today() {
        return now(DATE_NUM_PATTERN);
    }
}
