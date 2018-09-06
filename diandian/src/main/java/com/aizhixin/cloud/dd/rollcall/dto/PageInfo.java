package com.aizhixin.cloud.dd.rollcall.dto;

import java.util.LinkedList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PageInfo<T> {

    @NotNull
    private Long totalCount;
    @NotNull
    private Integer pageCount;
    @NotNull
    private Integer offset;
    @NotNull
    private Integer limit;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List <T> data = new LinkedList();

    public static PageInfo getPageInfo(Pageable pageable) {
        if (null == pageable) {
            throw new NullPointerException();
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setOffset(pageable.getPageNumber());
        pageInfo.setLimit(pageable.getPageSize());
        return pageInfo;
    }

    public static PageInfo getPageInfo(Page page, Pageable pageable) {
        if (null == pageable) {
            throw new NullPointerException();
        }
        if (null == page) {
            throw new NullPointerException();
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setOffset(pageable.getPageNumber());
        pageInfo.setLimit(pageable.getPageSize());
        pageInfo.setData(page.getContent());
        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setPageCount(page.getTotalPages());
        return pageInfo;
    }

    public static PageInfo setPageInfo(PageInfo pageInfo, Page page) {
        pageInfo.setData(page.getContent());
        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setPageCount(page.getTotalPages());
        return pageInfo;
    }

    @SuppressWarnings("rawtypes")
    public List getData() {
        return data;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
