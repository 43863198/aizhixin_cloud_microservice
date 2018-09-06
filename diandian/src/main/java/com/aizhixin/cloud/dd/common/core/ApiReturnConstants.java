/**
 * 
 */
package com.aizhixin.cloud.dd.common.core;

/**
 * API返回JSON数据常用标签名称定义
 * 
 * @author zhen.pan
 *
 */
public interface ApiReturnConstants {
	/**
	 * 保存返回的数据
	 */
	String DATA = "data";
	/**
	 * 查询结果 成功或者失败
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
	 * 出错原因
	 */
	String MESSAGE = "message";
	/**
	 * 返回结果
	 */
	String SUCCESS = "success";
	/**
	 * 异常码
	 */
	String ERROR = "error";
	/**
	 * 分页数据
	 */
	String PAGE = "page";
	/**
	 * ID
	 */
	String ID = "ID";
}
