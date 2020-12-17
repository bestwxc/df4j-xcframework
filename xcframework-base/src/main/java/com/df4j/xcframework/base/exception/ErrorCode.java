package com.df4j.xcframework.base.exception;

public class ErrorCode {

    /**
     * 成功
     */
    public final static Integer SUCCESS = 0;


    /**
     * 未处理的运行时异常
     */
    public final static Integer UNHANDLE_RUNTIME_EXCEPTION = -90;

    /**
     * 未处理的业务异常
     */
    public final static Integer UNHANDLE_BUSINESS_EXCEPTION = -91;


    /**
     * 未处理的系统异常
     */
    public final static Integer UNHANDLE_SYSTEM_ERROR = -99;


    /**
     * 不正确的请求格式
     */
    public final static Integer INCORRECT_REQUEST_FORMAT = -100;

    /**
     * 不正确的参数
     */
    public final static Integer INCORRECT_REQUEST_ARG = -101;

    /**
     * 不正确的密码或令牌
     */
    public final static Integer INCORRECT_CREDENTIALS = -996;

    /**
     * 授权过期
     */
    public final static Integer AUTHORIZED_EXPIRED = -997;


    /**
     * 未授权
     */
    public final static Integer UNAYTHORIZED = -998;

    /**
     * 未登录
     */
    public final static Integer UNLOGIN = -999;

    /**
     * 不正确的SQL语句
     */
    public final static Integer INCORRECT_SQL = -1000;

    /**
     * 重复的记录
     */
    public final static Integer DUPLICATE_RECORD = -1001;
}
