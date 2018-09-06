/**
 * 
 */
package com.aizhixin.cloud.io.common.core;

/**
 * API返回JSON数据常用标签名称定义
 * @author zhen.pan
 *
 */
public interface ApiReturnConstants {
	/**
	 * 保存返回的数据
	 */
	String DATA = "data";
	/**
	 * 查询结果
	 * 成功或者失败
	 */
	String RESULT = "result";
	/**
	 * 异常码
	 */
	String CODE = "code";
	/**
	 * 出错原因
	 */
	String CAUSE = "cause";
	/**
	 * 分页数据
	 */
	String PAGE = "page";
	/**
	 * ID
	 */
	String ID = "ID";
}
