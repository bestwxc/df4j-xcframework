package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.ErrorCode;
import com.df4j.xcframework.base.server.MultiResult;
import com.df4j.xcframework.base.server.PageResult;
import com.df4j.xcframework.base.server.Result;
import com.df4j.xcframework.base.server.SingleResult;

import java.util.List;

public class ResultUtils {

    public static Result build(Integer errorNo, String errorInfo, Object result) {
        return new SingleResult(errorNo, errorInfo, result);
    }

    public static Result success(Object result) {
        if(result instanceof List) {
            return new MultiResult((List) result);
        } else {
            return new SingleResult(result);
        }
    }

    public static Result success(Integer pageNum, Integer pageSize, Integer total, List result) {
        return new PageResult(pageNum, pageSize, total, result);
    }

    public static Result success() {
        return build(ErrorCode.SUCCESS, "请求成功", null);
    }

    public static Result error(Integer errorNo, String errorInfo) {
        return build(errorNo, errorInfo, null);
    }
}
