package com.df4j.xcframework.base.server;

import com.df4j.xcframework.base.constant.ResultType;

public class SingleResult<T> extends Result<T> {

    public SingleResult() {
        super(ResultType.OBJECT);
    }

    public SingleResult(Object result) {
        this();
        this.setResult((T) result);
    }

}
