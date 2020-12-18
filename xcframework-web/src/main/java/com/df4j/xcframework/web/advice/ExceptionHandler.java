package com.df4j.xcframework.web.advice;

import com.df4j.xcframework.base.exception.BusinessException;
import com.df4j.xcframework.base.exception.XcException;
import com.df4j.xcframework.base.server.Result;
import com.df4j.xcframework.base.util.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.df4j.xcframework.base.exception.ErrorCode.*;

@ControllerAdvice
public class ExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler

    @ResponseBody
    public Result handler(Throwable t) {
        Integer errorNo = null;
        String errorInfo = null;
        if (t instanceof BusinessException) {
            BusinessException be = (BusinessException) t;
            errorNo = be.getErrorNo();
            errorInfo = be.getMessage();
            logger.info("业务异常，errorNo:{},errorInfo:{}", errorNo, errorInfo, t);
        } else if (t instanceof HttpMessageNotReadableException) {
            errorNo = INCORRECT_REQUEST_FORMAT;
            errorInfo = "请求接口格式不正确";
            logger.warn("errorNo:{},errorInfo:{}", errorNo, errorInfo, t);
        } else if (t instanceof XcException) {
            errorNo = UNHANDLE_BUSINESS_EXCEPTION;
            errorInfo = t.getMessage();
            logger.error("errorNo:{},errorInfo:{}", errorNo, errorInfo, t);
        } else if (t instanceof RuntimeException) {
            errorNo = UNHANDLE_RUNTIME_EXCEPTION;
            errorInfo = t.getMessage();
            logger.error("errorNo:{},errorInfo:{}", errorNo, errorInfo, t);
        } else {
            errorNo = UNHANDLE_SYSTEM_ERROR;
            errorInfo = "系统异常:" + t.getMessage();
            logger.error("errorNo:{},errorInfo:{}", errorNo, errorInfo, t);
        }
        return ResultUtils.error(errorNo, errorInfo);
    }
}
