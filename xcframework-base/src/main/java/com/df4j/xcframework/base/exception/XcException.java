package com.df4j.xcframework.base.exception;

/**
 * 异常基类
 */
public class XcException extends RuntimeException{

    private static final long serialVersionUID = 8852397145821381533L;

    public XcException() {
    }

    public XcException(String message) {
        super(message);
    }

    public XcException(String message, Throwable cause) {
        super(message, cause);
    }

    public XcException(Throwable cause) {
        super(cause);
    }

    public XcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
