package com.df4j.xcframework.base.exception;

import org.springframework.util.StringUtils;

public class BusinessException extends XcException {

    private String errorGroup;

    private Integer errorNo;

    public BusinessException(String errorGroup, Integer errorNo, String message) {
        this(errorGroup, errorNo, message, null);
    }

    public BusinessException(String errorGroup, Integer errorNo, String message, Throwable cause) {
        this(errorGroup, errorNo, message, cause, true, true);
    }

    public BusinessException(String errorGroup, Integer errorNo, String message, Throwable cause,
                             boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorGroup = errorGroup;
        this.errorNo = errorNo;
    }

    public String getErrorGroup() {
        return errorGroup;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    @Override
    public String getMessage() {
        String msg = BusinessErrorManager.getErrorInfo(this.errorGroup, this.errorNo);
        if (!StringUtils.isEmpty(msg)) {
            return msg;
        } else {
            return super.getMessage();
        }
    }
}
