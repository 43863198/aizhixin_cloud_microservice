/**
 *
 */
package com.aizhixin.cloud.ew.common.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * 分页相关辅助工具类
 *
 * @author zhen.pan
 */
public class PageUtil {

	public static final int DEFAULT_OFFSET = 1;

	public static final int MIN_OFFSET = 1;

	public static final int DEFAULT_LIMIT = 20;

	public static final int MAX_LIMIT = 100;

	/**
	 * 初始化分页为一个不引起后台报错的合适的数值
	 *
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static PageRequest createNoErrorPageRequest(final Integer pageNumber, final Integer pageSize) {
		int pn = 0;
		int ps = 20;
		if (null != pageNumber && pageNumber - 1 >= 0) {
			pn = pageNumber - 1;
		}
		if (null != pageSize && pageSize > 0) {
			ps = pageSize;
		}
		return new PageRequest(pn, ps);
	}

	/**
	 * 创建分页可排序
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortType
	 * @param cm
	 * @return
	 */
	public static PageRequest createNoErrorPageRequestAndSortType(final Integer pageNumber, final Integer pageSize,
			String sortType, String cm) {
		Sort sort = null;
		if (sortType.equals("asc")) {
			sort = new Sort(Direction.ASC, cm);
		} else {
			sort = new Sort(Direction.DESC, cm);
		}

		if (null == pageNumber || null == pageSize || 0 >= pageSize) {
			return new PageRequest(0, 10, sort);
		}
		if (pageNumber - 1 <= 0) {
			return new PageRequest(0, pageSize, sort);
		}
		return new PageRequest(pageNumber - 1, pageSize, sort);
	}

	/**
	 * 创建分页可多个参数排序
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortType1
	 * @param cm1
	 * @param sortType2
	 * @param cm2
	 * @return
	 */
	public static PageRequest createNoErrorPageRequestAndSortTypes(final Integer pageNumber, final Integer pageSize,
			String sortType1, String cm1, String sortType2, String cm2) {

		List<Order> orders = new ArrayList<Order>();
		if (sortType1.equals("asc")) {
			orders.add(new Order(Direction.ASC, cm1));
		} else {
			orders.add(new Order(Direction.DESC, cm1));
		}
		if (sortType2.equals("asc")) {
			orders.add(new Order(Direction.ASC, cm2));
		} else {
			orders.add(new Order(Direction.DESC, cm2));
		}

		if (null == pageNumber || null == pageSize || 0 >= pageSize) {
			return new PageRequest(0, 10, new Sort(orders));
		}
		if (pageNumber - 1 <= 0) {
			return new PageRequest(0, pageSize, new Sort(orders));
		}
		return new PageRequest(pageNumber - 1, pageSize, new Sort(orders));
	}

	/**
	 * 计算总页数
	 * 
	 * @param count
	 * @param pageSize
	 * @return
	 */
	public static int totalPages(long count, int pageSize) {
		return (int) (count / pageSize + (count % pageSize == 0 ? 0 : 1));
	}
}
