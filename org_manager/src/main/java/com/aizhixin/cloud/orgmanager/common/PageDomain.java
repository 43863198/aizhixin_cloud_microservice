package com.aizhixin.cloud.orgmanager.common;

import lombok.Data;

@Data
public class PageDomain implements java.io.Serializable{
	private Long totalElements;//总条数
	private Integer totalPages;//总页数
	private Integer pageNumber;//第几页
	private Integer pageSize;//每页多少条
}
