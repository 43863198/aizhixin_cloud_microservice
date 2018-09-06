/**
 * 
 */
package com.aizhixin.cloud.school.common.exception;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.aizhixin.cloud.school.common.core.ErrorCode;

import java.util.HashMap;
import java.util.Map;


/**
 * 统一异常处理
 * @author zhen.pan
 *
 */
@SuppressWarnings("deprecation")
@RestControllerAdvice
public class ExceptionHandle {
	/**
	 * @param e
	 * @return 验证异常
	 */
	@ResponseStatus(value = HttpStatus.OK)
	@ExceptionHandler(ExceptionValidation.class)
	public Map<String,Object> doRequiredExceptionHandle(ExceptionValidation e) {
		Map<String,Object> result=new HashMap<>();
		result.put("result",Boolean.FALSE);
		result.put("code",e.getCode());
		result.put("cause",e.getMessage());
		return result;
	}
}
