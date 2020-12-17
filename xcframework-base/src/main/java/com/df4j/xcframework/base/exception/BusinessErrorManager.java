package com.df4j.xcframework.base.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.df4j.xcframework.base.constant.Constants.BASE_ERROR_GROUP;
import static com.df4j.xcframework.base.exception.ErrorCode.*;

public class BusinessErrorManager {

    private static Logger logger = LoggerFactory.getLogger(BusinessErrorManager.class);

    private static Map<String, Map<Integer, String>> allErrorMap;

    static {
        // 创建errorMap
        allErrorMap = new HashMap<>();

        Map<Integer, String> xcframeworkErrorMap = new HashMap<>();
        xcframeworkErrorMap.put(SUCCESS, "成功");
        xcframeworkErrorMap.put(UNHANDLE_RUNTIME_EXCEPTION, "未处理的运行时异常");
        xcframeworkErrorMap.put(UNHANDLE_BUSINESS_EXCEPTION, "未处理的业务异常");
        xcframeworkErrorMap.put(UNHANDLE_SYSTEM_ERROR, "未处理的系统异常");
        xcframeworkErrorMap.put(INCORRECT_REQUEST_FORMAT, "不正确的请求格式");
        xcframeworkErrorMap.put(INCORRECT_REQUEST_ARG, "不正确的请求参数");
        xcframeworkErrorMap.put(INCORRECT_CREDENTIALS, "不正确的密码或令牌");
        xcframeworkErrorMap.put(AUTHORIZED_EXPIRED, "认证过期");
        xcframeworkErrorMap.put(UNAYTHORIZED, "未授权");
        xcframeworkErrorMap.put(UNLOGIN, "未登录");
        xcframeworkErrorMap.put(INCORRECT_SQL, "不正确的SQL语句");
        xcframeworkErrorMap.put(DUPLICATE_RECORD, "记录重复");
        // 放入不可变map，防止其他地方获取map后进行修改，只允许通过该类的接口进行修改
        allErrorMap.put(BASE_ERROR_GROUP, Collections.unmodifiableMap(xcframeworkErrorMap));
    }

    public static void setErrorMap(String errorGroupCode, Map<Integer, String> errorMap) {
        if (BASE_ERROR_GROUP.equals(errorGroupCode)) {
            logger.warn("XCF框架默认的errorMap不允许动态设置，请使用其他errorGroupCode");
            return;
        }
        if (errorMap == null || errorMap.isEmpty()) {
            logger.warn("即将清空errorMap,errorGroupCode:{}", errorGroupCode);
        }
        // 放入不可变map，防止其他地方获取map后进行修改，只允许通过该类的接口进行修改
        allErrorMap.put(errorGroupCode, Collections.unmodifiableMap(errorMap));
        logger.info("设置errorMap,errorGroupCode:{}, errorMap:{}", errorGroupCode, errorMap.toString());
    }

    public static Map<Integer, String> getErrorMap(String errorGroupCode) {
        return allErrorMap.get(errorGroupCode);
    }

    public static String getErrorInfo(String errorGroupCode, Integer errorNo) {
        if (StringUtils.isEmpty(errorGroupCode)) {
            errorGroupCode = BASE_ERROR_GROUP;
        }
        return allErrorMap.get(errorGroupCode).get(errorNo);
    }
}
