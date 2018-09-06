//package com.aizhixin.cloud.dd.common.schedule.job;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.core.support.WebApplicationObjectSupport;
//
//import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
//import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
//import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
//
//public class InitStudentSignInfoJob extends WebApplicationObjectSupport implements Job {
//
//    private final static Logger log = LoggerFactory.getLogger(InitStudentSignInfoJob.class);
//
//    @Override
//    public void execute(JobExecutionContext core) {
//        log.info("开始执行任务,time:" + DateFormatUtil.getNow());
//        ScheduleService scheduleService = (ScheduleService) SpringContextUtil.getBean("scheduleService");
//        scheduleService.executePerTenMinutes(null);
//    }
//}
