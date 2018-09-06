//package com.aizhixin.cloud.dd.common.schedule;
//
//
//import com.aizhixin.cloud.dd.common.schedule.job.CloseCounselorRollCallJob;
//import com.aizhixin.cloud.dd.common.schedule.job.InitScheduleJob;
//import com.aizhixin.cloud.dd.common.schedule.job.InitStudentSignInfoJob;
//import com.aizhixin.cloud.dd.common.schedule.job.InitTodayTeachClassIdsJob;
//import com.aizhixin.cloud.dd.common.schedule.job.RollCallTypeCheckJob;
//
//import org.hibernate.service.spi.ServiceException;
//import org.quartz.*;
//import org.quartz.impl.matchers.GroupMatcher;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StopWatch;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Transactional
//public class InitSchedule {
//
//    private final Logger log = LoggerFactory.getLogger(InitSchedule.class);
//
//    @Autowired
//    SchedulerFactoryBean schedulerFactoryBean;
//
//    public void scheduleJobs() throws SchedulerException {
//
//
//        Scheduler scheduler = schedulerFactoryBean.getScheduler();
//
//        System.out.println("定时器开始进行初始化操作...");
//        list(scheduler);
//        startSchedule(scheduler);
//        System.out.println("定时器初始化操作完成...");
//
//    }
//
//    public void startSchedule(Scheduler scheduler) throws SchedulerException {
//        // 清除原有调度任务。
//        list(scheduler);
//        // 启动初始化排课信息定时任务
//        startInitScheduleJob(scheduler);
//        startInitSignInJob(scheduler);
//        closeRollCallEverJob(scheduler);
//        countMidJob(scheduler);
//        startInitTodayTeachClassIdsJob(scheduler);
//        scheduler.start();
//    }
//
//    private void startInitScheduleJob(Scheduler scheduler)
//            throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(InitScheduleJob.class)
//                .withIdentity("InitScheduleJobDetail", "dd").build();
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("0 20 0 * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("InitScheduleJobTrigger", "dd")
//                .withSchedule(scheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//
//        log.info("凌晨获取排课信息 启动成功 ...");
//    }
//
//    private void startInitSignInJob(Scheduler scheduler)
//            throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(InitStudentSignInfoJob.class)
//                .withIdentity("InitStudentSignInfoJobDetail", "dd").build();
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("0 0,10,20,30,40,50 * * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("InitStudentSignInfoJobTrigger", "dd")
//                .withSchedule(scheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//
//        log.info("课前初始化状态 启动成功 ...");
//
//    }
//
//    private void closeRollCallEverJob(Scheduler scheduler)
//            throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(CloseCounselorRollCallJob.class)
//                .withIdentity("CloseCounselorRollCallJob", "dd").build();
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("0 0 2 * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("CloseCounselorRollCallJobTrigger", "dd")
//                .withSchedule(scheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//
//        log.info("自动关闭辅导员点名 启动成功...");
//    }
//
//    private void countMidJob(Scheduler scheduler)
//            throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(RollCallTypeCheckJob.class)
//                .withIdentity("RollCallTypeCheckJob", "dd").build();
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("30 * * * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("RollCallTypeCheckJobTrigger", "dd")
//                .withSchedule(scheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//
//        log.info("计算中值任务 启动成功...");
//    }
//
//    private void startInitTodayTeachClassIdsJob(Scheduler scheduler)
//            throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(InitTodayTeachClassIdsJob.class)
//                .withIdentity("InitTodayTeachClassIdsJobDetail", "dd").build();
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
//                .cronSchedule("0 0 1 * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("InitTodayTeachClassIdsJobTrigger", "dd")
//                .withSchedule(scheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//
//        log.info("每天晚上初始化当天学生和教学班对应关系到redis 启动成功 ...");
//    }
//
//
//    /**
//     * 所有任务列表 2016年10月9日上午11:16:59
//     */
//    public List <StopWatch.TaskInfo> list(Scheduler scheduler) {
//        List <StopWatch.TaskInfo> list = new ArrayList <>();
//
//        try {
//            for (String groupJob : scheduler.getJobGroupNames()) {
//                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupJob))) {
//                    delete(scheduler, jobKey.getName(), jobKey.getGroup());
//                    scheduler.deleteJob(jobKey);
//                    System.out.println(jobKey.getName() + " " + jobKey.getGroup());
//                }
//            }
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
//
//    /**
//     * 2017年4月20日 meihua.li
//     */
//    public void delete(Scheduler scheduler, String jobName, String jobGroup) {
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
//        try {
//            if (checkExists(scheduler, jobName, jobGroup)) {
//                scheduler.pauseTrigger(triggerKey);
//                scheduler.unscheduleJob(triggerKey);
//            }
//        } catch (SchedulerException e) {
//            throw new ServiceException(e.getMessage());
//        }
//    }
//
//    /**
//     * 验证是否存在
//     *
//     * @param jobName
//     * @param jobGroup
//     * @throws SchedulerException 2016年10月8日下午5:30:43
//     */
//    private boolean checkExists(Scheduler scheduler, String jobName,
//                                String jobGroup) throws SchedulerException {
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
//        return scheduler.checkExists(triggerKey);
//    }
//
//    /**
//     * 从数据库中找到已经存在的job，并重新开户调度
//     */
//    public void resumeJob() {
//        try {
//            Scheduler scheduler = schedulerFactoryBean.getScheduler();
//            // ①获取调度器中所有的触发器组
//            List <String> triggerGroups = scheduler.getTriggerGroupNames();
//            // ②重新恢复在tgroup1组中，名为trigger1_1触发器的运行
//            for (int i = 0; i < triggerGroups.size(); i++) {
//                List <String> triggers = scheduler.getTriggerGroupNames();
//                for (int j = 0; j < triggers.size(); j++) {
//                    Trigger tg = scheduler.getTrigger(new TriggerKey(triggers
//                            .get(j), triggerGroups.get(i)));
//                    // ②-1:根据名称判断
//                    if (tg instanceof SimpleTrigger
//                            && tg.getDescription().equals("tgroup1.trigger1_1")) {
//                        // ②-1:恢复运行
//                        scheduler.resumeJob(new JobKey(triggers.get(j),
//                                triggerGroups.get(i)));
//                    }
//                }
//
//            }
//            scheduler.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }
//}
