/**
 * 
 */
package com.aizhixin.cloud.ew.common.exception;

import lombok.Getter;

/**
 * 通用非检查异常
 * 主要用来传递错误码和错误原因
 * @author zhen.pan
 *
 */
public class CommonException extends RuntimeException {
	private static final long serialVersionUID = -1760130004947794055L;
	@Getter protected Integer code;
	@Getter protected Object errors;
	
	public CommonException(Integer code, String message) {
		super(message);
		this.code = code;
	}
}
