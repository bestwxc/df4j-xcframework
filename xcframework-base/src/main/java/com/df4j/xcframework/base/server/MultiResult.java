package com.df4j.xcframework.base.server;

import com.df4j.xcframework.base.constant.ResultType;

import java.util.List;

public class MultiResult<T> extends Result<List<T>> {
    private boolean page = false;

    public MultiResult() {
        this(false, null);
    }

    public MultiResult(List<T> list) {
        this(false, list);
    }

    public MultiResult(boolean page, List<T> list) {
        super(ResultType.LIST);
        this.setResult(list);
        this.page = page;
    }

    @Override
    public List<T> getResult() {
        return super.getResult();
    }

    @Override
    public void setResult(List<T> result) {
        super.setResult(result);
    }
}
