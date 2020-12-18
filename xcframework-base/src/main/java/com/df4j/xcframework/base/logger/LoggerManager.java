package com.df4j.xcframework.base.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 日志管理类
 */
public class LoggerManager {

    // 默认sys Logger 名称
    public static final String DEFAULT_SYS_LOGGER_NAME = "defaultSysLogger";
    // 默认biz Logger 名称
    public static final String DEFAULT_BIZ_LOGGER_NAME = "defaultBizLogger";

    public static Logger getLogger(String loggerName) {
        Assert.notNull(loggerName, "loggerName must not be null!");
        return LoggerFactory.getLogger(loggerName);
    }

    // 获取SysLogger
    public static Logger getSysLogger(){
        return getLogger(DEFAULT_SYS_LOGGER_NAME);
    }

    // 获取BizLogger
    public static Logger getBizLogger(){
        return getLogger(DEFAULT_BIZ_LOGGER_NAME);
    }
}
