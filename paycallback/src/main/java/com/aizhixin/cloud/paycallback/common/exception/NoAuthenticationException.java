/**
 * 
 */
package com.aizhixin.cloud.paycallback.common.exception;


import com.aizhixin.cloud.paycallback.common.core.PublicErrorCode;
import lombok.Getter;

/**
 * 未授权非检查异常
 * @author zhen.pan
 *
 */
public class NoAuthenticationException extends RuntimeException {
	private static final long serialVersionUID = -1760130004947794055L;
	@Getter protected Integer code;
	
	public NoAuthenticationException() {
		super("未授权");
		this.code = PublicErrorCode.AUTH_EXCEPTION.getIntValue();
	}
	
	public NoAuthenticationException(Integer code, String message) {
		super(message);
		this.code = code;
	}
}
