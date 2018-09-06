package com.aizhixin.cloud.ew.common;

import javax.validation.constraints.NotNull;

import java.util.LinkedList;
import java.util.List;

public class PageInfo<T> extends BaseDTO {


    @NotNull
    private Long totalCount;
    @NotNull
    private Integer pageCount;
    @NotNull
    private Integer offset;
    @NotNull
    private Integer limit;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private List<T> data = new LinkedList();


    @SuppressWarnings("rawtypes")
	public List getData() {
        return data;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void setData(List data) {
        this.data = data;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
