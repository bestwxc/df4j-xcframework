package com.df4j.xcframework.base.server;

public abstract class Result<T> {

    private Integer errorNo;
    private String errorInfo;
    private String resultType;
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
