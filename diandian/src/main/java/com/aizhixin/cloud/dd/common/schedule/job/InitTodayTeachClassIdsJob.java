//package com.aizhixin.cloud.dd.common.schedule.job;
//
//import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
//import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
//import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
//import com.aizhixin.cloud.dd.rollcall.serviceV2.StuTeachClassService;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 每天晚上初始化当天学生和教学班对应关系到redis
// */
//public class InitTodayTeachClassIdsJob implements Job {
//
//    private final static Logger log = LoggerFactory
//            .getLogger(InitTodayTeachClassIdsJob.class);
//
//    @Override
//    public void execute(JobExecutionContext core)
//            throws JobExecutionException {
//
//    	log.info("每天晚上初始化当天学生和教学班对应关系到redis定时任务开始执行");
//    	StuTeachClassService stuTeachClassService = (StuTeachClassService) SpringContextUtil
//                .getBean("stuTeachClassService");
//    	stuTeachClassService.saveStuTeachClassIds();;
//    }
//
//}
