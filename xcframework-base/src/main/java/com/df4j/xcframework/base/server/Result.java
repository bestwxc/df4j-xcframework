package com.df4j.xcframework.base.server;

import com.df4j.xcframework.base.constant.ResultType;
import com.df4j.xcframework.base.exception.ErrorCode;

public abstract class Result<T> {

    private Integer errorNo = ErrorCode.SUCCESS;
    private String errorInfo = "请求成功";
    private String resultType = ResultType.OBJECT;
    private T result;

    public Result(String resultType) {
        this.resultType = resultType;
    }

    public Result(Integer errorNo, String errorInfo, String resultType, T result) {
        this.errorNo = errorNo;
        this.errorInfo = errorInfo;
        this.resultType = resultType;
        this.result = result;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(Integer errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
