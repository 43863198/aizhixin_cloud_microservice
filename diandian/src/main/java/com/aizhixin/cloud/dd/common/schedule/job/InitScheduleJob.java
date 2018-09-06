//package com.aizhixin.cloud.dd.common.schedule.job;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.core.support.WebApplicationContextUtils;
//import org.springframework.web.core.support.WebApplicationObjectSupport;
//
//import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
//import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
//
//public class InitScheduleJob implements Job {
//
//    private final static Logger log = LoggerFactory
//            .getLogger(InitScheduleJob.class);
//
//    @Override
//    public void execute(JobExecutionContext core)
//            throws JobExecutionException {
//        InitScheduleService initScheduleService = (InitScheduleService) SpringContextUtil
//                .getBean("initScheduleService");
//        initScheduleService.initSchedule();
//    }
//
//}
