/**
 * 
 */
package com.aizhixin.cloud.school.common.core;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * 分页相关辅助工具类
 * @author zhen.pan
 *
 */
public class PageUtil {

	/**
	 * 初始化分页为一个不引起后台报错的合适的数值
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static PageRequest createNoErrorPageRequest(final Integer pageNumber, final Integer pageSize) {
		if(null == pageNumber || null == pageSize || 0 >= pageSize) {
			return new PageRequest(0, 20);
		}
		if(pageNumber - 1 <= 0) {
			return new PageRequest(0, pageSize);
		}
		return new PageRequest(pageNumber - 1, pageSize);
	}
	
	public static PageRequest createNoErrorPageRequestAndSortType(final Integer pageNumber, final Integer pageSize,String sortType,String cm) {
		Sort sort=null;
		if(sortType.equals("asc")){
			sort=new Sort(Direction.ASC,cm);
		}else{
			sort=new Sort(Direction.DESC,cm);
		}
		
		
		if(null == pageNumber || null == pageSize || 0 >= pageSize) {
			return new PageRequest(0, 20,sort);
		}
		if(pageNumber - 1 <= 0) {
			return new PageRequest(0, pageSize,sort);
		}
		return new PageRequest(pageNumber - 1, pageSize,sort);
	}
	
	/**
	 * 计算总页数
	 * @param count
	 * @param pagesize
	 * @return
	 */
	public static int cacalatePagesize(long count, int pagesize) {
		return (int) (count / pagesize + (count % pagesize == 0 ? 0 : 1));
	}
}
