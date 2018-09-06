///**
// * 
// */
//package com.aizhixin.cloud.data.common.loging;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
///**
// * 统一日志记录配置，目前只记录的web端入口地方的日志
// * 日志拦截器
// * @author zhen.pan
// *
// */
//@Aspect
//@Component
//public class AspectLog {
//	final static private Logger LOG = LoggerFactory.getLogger(AspectLog.class);
//
//    @Pointcut("execution(public * com.aizhixin..service..*Service.*(..))")
//    public void aspectLog(){}
//
//
//    @Around("aspectLog()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            long starttime = System.currentTimeMillis();
//            Object result = joinPoint.proceed();
//            if(LOG.isDebugEnabled()) {
//                LOG.debug("{} {} Params[{}] Response[{}] speed time:[{}]ms", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), result, (System.currentTimeMillis() - starttime));
//            }
//            return result;
//        } catch (IllegalArgumentException e) {
//            throw e;
//        }
//    }
//
//    @AfterThrowing(pointcut = "aspectLog()",throwing= "error")
//    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
//    	LOG.warn("{} {} Params[{}] ,throws: [{}]", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()), error.getMessage());
//    }
//}
