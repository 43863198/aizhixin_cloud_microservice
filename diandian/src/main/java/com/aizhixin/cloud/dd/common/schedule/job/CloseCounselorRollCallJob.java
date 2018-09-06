//package com.aizhixin.cloud.dd.common.schedule.job;
//
//import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
//import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
//import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 每天晚上关闭所有的导员点名
// */
//public class CloseCounselorRollCallJob implements Job {
//
//    private final static Logger log = LoggerFactory
//            .getLogger(CloseCounselorRollCallJob.class);
//
//    @Override
//    public void execute(JobExecutionContext core)
//            throws JobExecutionException {
//        RollCallEverService rollCallEverService = (RollCallEverService) SpringContextUtil
//                .getBean("rollCallEverService");
//        rollCallEverService.scheduleCloseRollCallEver();
//    }
//
//}
