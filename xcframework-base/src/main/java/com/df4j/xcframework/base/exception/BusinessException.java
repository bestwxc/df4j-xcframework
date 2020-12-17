package com.df4j.xcframework.base.exception;

import com.df4j.xcframework.base.constant.Constants;
import org.springframework.util.StringUtils;

import static com.df4j.xcframework.base.exception.ErrorCode.*;

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
        this.errorGroup = StringUtils.isEmpty(errorGroup) ? Constants.BASE_ERROR_GROUP : this.errorGroup;
        this.errorNo = this.judgeErrorNo(cause, errorNo);
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

    public String getMessageWithCode() {
        return String.format("[%s][%d][%s]", this.errorGroup, this.errorNo, this.getMessage());
    }

    private Integer judgeErrorNo(Throwable t, Integer errorNo) {
        if (errorNo != null) {
            return errorNo;
        }

        if (t instanceof XcException) {
            return UNHANDLE_BUSINESS_EXCEPTION;
        }
        if (t instanceof RuntimeException) {
            return UNHANDLE_RUNTIME_EXCEPTION;
        }
        return UNHANDLE_SYSTEM_ERROR;
    }
}
