/**
 * 
 */
package com.aizhixin.cloud.io.common.exception;

import com.aizhixin.cloud.io.common.core.ErrorCode;
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


/**
 * 统一异常处理
 * @author zhen.pan
 *
 */
@SuppressWarnings("deprecation")
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RestControllerAdvice
public class ExceptionHandle {
	/**
	 * 通用异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler(CommonException.class)
	public ExceptionMessage doRequiredExceptionHandle(CommonException e) {
		return new ExceptionMessage(e.getCode(), e.getMessage());
	}
	/**
	 * 未授权异常
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(NoAuthenticationException.class)
	public ExceptionMessage doNoAuthenticationException(NoAuthenticationException e) {
		return new ExceptionMessage(e.getCode(), e.getMessage());
	}

	/*************************************DefaultHandlerExceptionResolver*************************************************************/

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoSuchRequestHandlingMethodException.class)
	public ExceptionMessage doNoSuchRequestHandlingMethodException(NoSuchRequestHandlingMethodException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ExceptionMessage doHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ExceptionMessage doHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ExceptionMessage doHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(MissingPathVariableException.class)
	public ExceptionMessage doMissingPathVariableException(MissingPathVariableException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ExceptionMessage doMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(ServletRequestBindingException.class)
	public ExceptionMessage doServletRequestBindingException(ServletRequestBindingException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(ConversionNotSupportedException.class)
	public ExceptionMessage doConversionNotSupportedException(ConversionNotSupportedException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(TypeMismatchException.class)
	public ExceptionMessage doTypeMismatchException(TypeMismatchException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ExceptionMessage doHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(HttpMessageNotWritableException.class)
	public ExceptionMessage doHttpMessageNotWritableException(HttpMessageNotWritableException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ExceptionMessage doMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(MissingServletRequestPartException.class)
	public ExceptionMessage doMissingServletRequestPartException(MissingServletRequestPartException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(BindException.class)
	public ExceptionMessage doBindException(BindException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public ExceptionMessage doNoHandlerFoundException(TypeMismatchException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ExceptionHandler(AsyncRequestTimeoutException.class)
	public ExceptionMessage doAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SPRING_EXCEPTION_CODE, e.getMessage());
	}
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ExceptionMessage doException(Exception e) {
		e.printStackTrace();
		return new ExceptionMessage(ErrorCode.SYSTEM_EXCEPTION_CODE, null != e ? e.getMessage(): "NullPointerException");
	}
}
