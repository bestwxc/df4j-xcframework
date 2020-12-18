package com.df4j.xcframework.base.server;

import java.util.List;

public class PageResult<T> extends MultiResult<T> {
    private Integer pageNum = 1;
    private Integer pageSize = 100;
    private Integer total = 0;

    public PageResult() {
        super(true, null);
    }

    public PageResult(List<T> list, Integer pageNum, Integer pageSize, Integer total) {
        super(true, list);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
