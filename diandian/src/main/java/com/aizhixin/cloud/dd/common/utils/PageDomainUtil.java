package com.aizhixin.cloud.dd.common.utils;

import lombok.Data;

@Data
public class PageDomainUtil {
	private Long totalElements;//总条数
	private Integer totalPages;//总页数
	private Integer pageNumber;//第几页
	private Integer pageSize;//每页多少条
	
	public static PageDomainUtil getPage(Integer total,Integer pageNumber,Integer pageSize){
		PageDomainUtil pageDomain = new PageDomainUtil();
		pageDomain.setTotalElements(Long.valueOf(total));
		pageDomain.setPageNumber(pageNumber);
		pageDomain.setPageSize(pageSize);
		Integer totalPages = 0;
		if(0 == total%pageSize){
			totalPages = total/pageSize;
		}else{
			totalPages = total/pageSize + 1;
		}
		pageDomain.setTotalPages(totalPages);
		return pageDomain;
	}
}
