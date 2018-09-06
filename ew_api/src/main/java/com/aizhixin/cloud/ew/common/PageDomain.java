package com.aizhixin.cloud.ew.common;

import lombok.Data;

@Data
public class PageDomain {
	private Long totalElements;//总条数
	private Integer totalPages;//总页数
	private Integer pageNumber;//第几页
	private Integer pageSize;//每页多少条
	
	public PageDomain() {
    }

    public PageDomain(Long totalElements, Integer totalPages, Integer pageNumber, Integer pageSize) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
