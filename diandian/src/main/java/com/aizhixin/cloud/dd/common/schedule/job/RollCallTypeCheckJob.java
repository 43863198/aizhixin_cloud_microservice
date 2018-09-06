//package com.aizhixin.cloud.dd.common.schedule.job;
//
//import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
//import com.aizhixin.cloud.dd.common.utils.SpringContextUtil;
//import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
//import com.aizhixin.cloud.dd.rollcall.serviceV2.RollCallServiceV2;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.core.support.WebApplicationObjectSupport;
//
//public class RollCallTypeCheckJob extends WebApplicationObjectSupport implements Job {
//
//    private final static Logger log = LoggerFactory.getLogger(RollCallTypeCheckJob.class);
//
//    @Override
//    public void execute(JobExecutionContext core) {
//        log.info("开始执行任务计算中值,time:" + DateFormatUtil.getNow());
//        RollCallServiceV2 rollCallServiceV2 = (RollCallServiceV2) SpringContextUtil.getBean(RollCallServiceV2.class);
//        rollCallServiceV2.checkRollCallTypeSchedule();
//    }
//}
