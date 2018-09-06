/**
 * 
 */
package com.aizhixin.cloud.io.common.core;

import org.springframework.data.domain.PageRequest;

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
