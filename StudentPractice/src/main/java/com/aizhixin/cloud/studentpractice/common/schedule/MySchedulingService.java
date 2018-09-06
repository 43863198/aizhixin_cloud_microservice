package com.aizhixin.cloud.studentpractice.common.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aizhixin.cloud.studentpractice.common.service.DistributeLock;
import com.aizhixin.cloud.studentpractice.score.service.CounselorCountService;
import com.aizhixin.cloud.studentpractice.score.service.ScoreService;
import com.aizhixin.cloud.studentpractice.summary.service.EnterpriseCountService;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryReplyCountService;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryService;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskCountService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountService;
import com.aizhixin.cloud.studentpractice.task.service.SignStatisticalService;
import com.aizhixin.cloud.studentpractice.task.service.TaskStatisticalService;

/**
 * 定时任务入口
 */
@Component
public class MySchedulingService {
    final static private Logger LOG = LoggerFactory.getLogger(MySchedulingService.class);
    @Autowired
    private DistributeLock distributeLock;
    @Autowired
    private TaskStatisticalService taskStatisticalService;
    @Autowired
	private MentorTaskCountService mentorTaskCountService;
    @Autowired
	private SummaryService summaryService;
    @Autowired
   	private SummaryReplyCountService summaryReplyCountService;
    @Autowired
	private EnterpriseCountService enterpriseCountService;
    @Autowired
   	private  PeopleCountDetailService peopleCountDetailService;
    @Autowired
   	private  PeopleCountService peopleCountService;
    @Autowired
   	private  SignStatisticalService signStatisticalService;
    @Autowired
   	private  ScoreService scoreService;
    @Autowired
    private CounselorCountService counselorCountService;

    /**
     * 定时统计实践任务  已关联实践参与计划
     */
    @Scheduled(cron = "0 30 4 * * ?")
    public void taskStatistics() {
        if (distributeLock.getTaskStatisticsLock()) {
            LOG.info("开始启动定时统计实践任务");
            taskStatisticalService.taskStatistics(null);
            LOG.info("定时统计实践任务,执行完成");
        } else {
            LOG.info("启动定时统计实践任务，获取锁失败");
        }
    }

    /**
     * 定时更新实践学校学生信息  已关联实践参与计划
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void countPeopleNum() {
    	  if (distributeLock.getCountPeopleNumLock()) {
              LOG.info("开始启动定时更新实践学校学生信息");
              taskStatisticalService.countPeopleNum(null);
              LOG.info("定时更新实践学校学生信息,执行完成");
          } else {
              LOG.info("启动定时更新实践学校学生信息，获取锁失败");
          }
    }
    
    /**
     * 定时更新导师任务完成情况统计信息  已关联实践参与计划
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void countMentorTaskNum() {
    	  if (distributeLock.getMentorTaskNumLock()) {
              LOG.info("开始启动定时更新导师任务完成情况统计信息");
              mentorTaskCountService.saveCountTask();
              LOG.info("定时更新导师任务完成情况统计信息,执行完成");
          } else {
              LOG.info("启动定时更新导师任务完成情况统计信息，获取锁失败");
          }
    }
    
    
    /**
     * 定时统计学生日报周报月报信息  已关联实践参与计划
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void countSummaryNum() {
    	  if (distributeLock.getCountSummaryNumLock()) {
              LOG.info("开始启动定时定时统计学生日报周报月报信息");
              summaryService.countSummaryNumTask();
              LOG.info("定时更新定时统计学生日报周报月报信息,执行完成");
          } else {
              LOG.info("启动定时更新定时统计学生日报周报月报信息，获取锁失败");
          }
    }

    /**
     * 已关联实践参与计划
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void countSummaryReply() {
    	  if (distributeLock.getSummaryReplyLock()) {
              LOG.info("开始启动定时更新实践日志信息");
              summaryReplyCountService.summaryReplyNumCountTask();
              LOG.info("定时更新实践日志信息,执行完成");
          } else {
              LOG.info("启动定时更新实践日志信息，获取锁失败");
          }
    }
    
    /**
	 * 统计企业的实践企业导师数量
	 * 无需与实践计划关联
	 */
    @Scheduled(cron = "0 20 2 * * ?")
    public void countEnterprise() {
    	  if (distributeLock.getEnterpriseCountLock()) {
              LOG.info("开始启动定时更新企业实践人数信息");
              enterpriseCountService.enterpriseCountTask();
              LOG.info("定时更新企业实践人数信息,执行完成");
          } else {
              LOG.info("启动定时更新企业实践人数信息，获取锁失败");
          }
    }
    
    /**
     * 与实践计划已关联
     */
    @Scheduled(cron = "0 40 2 * * ?")
    public void practiceJoin() {
    	  if (distributeLock.getPracticeJoinLock()) {
              LOG.info("开始启动定时更新学生是否提交实践任务或日志信息");
              peopleCountDetailService.updateJoinPracticeTask();
              LOG.info("定时更新学生是否提交实践任务或日志信息,执行完成");
          } else {
              LOG.info("启动定时更新学生是否提交实践任务或日志信息，获取锁失败");
          }
    }
    
    /**
     * 无需与实践计划关联
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void countJoin() {
    	  if (distributeLock.getCountJoinLock()) {
              LOG.info("开始启动定时更新学生参与实践人数统计信息");
              peopleCountService.updateJoinStuNumTask();
              LOG.info("定时更新更新学生参与实践人数统计信息,执行完成");
          } else {
              LOG.info("启动定时更新学生参与实践人数统计信息，获取锁失败");
          }
    }
    
    /**
     * 无需与实践计划关联
     */
    @Scheduled(cron = "0 20 3 * * ?")
    public void countSummaryByClass() {
    	  if (distributeLock.getCountSummaryByClassLock()) {
              LOG.info("开始启动定时更新班级学生提交日志周志统计信息");
              peopleCountService.updateSummaryNumTask();
              LOG.info("定时更新班级学生提交日志周志统计,执行完成");
          } else {
              LOG.info("启动定时更新班级学生提交日志周志统计，获取锁失败");
          }
    }
    
    /**
     * 与实践计划已关联
     */
    @Scheduled(cron = "0 40 3 * * ?")
    public void countReportByStu() {
    	  if (distributeLock.getCountReportByStuLock()) {
              LOG.info("开始启动定时更新学生提交实践报告统计信息");
              peopleCountDetailService.updateStuReportStatusTask();
              LOG.info("定时更新学生提交实践报告统计信息,执行完成");
          } else {
              LOG.info("启动定时更新学生提交实践报告统计信息，获取锁失败");
          }
    }
    
    /**
     * 无需与实践计划关联
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void countReportByClass() {
    	  if (distributeLock.getCountReportByClassLock()) {
              LOG.info("开始启动定时更新班级学生提交实践报告统计信息");
              peopleCountService.updateReportNumTask();
              LOG.info("定时更新班级学生提交实践报告统计信息,执行完成");
          } else {
              LOG.info("启动定时更新班级学生提交实践报告统计信息，获取锁失败");
          }
    }
    
    /**
     * 与实践计划已关联
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void synSignInfor() {
    	  if (distributeLock.getSynSignLock()) {
              LOG.info("开始启动定时更新实践考勤信息");
              signStatisticalService.synSignInforTask();
              LOG.info("定时更新实践考勤信息,执行完成");
          } else {
              LOG.info("启动定时更新实践考勤信息，获取锁失败");
          }
    }
    
    
    @Scheduled(cron = "0 30 5 * * ?")
    public void autoScoreTask() {
    	  if (distributeLock.getScoreCountLock()) {
              LOG.info("开始启动定时统计实践参与计划内学生成绩");
              scoreService.scoreTask(null);
              LOG.info("定时更新统计实践参与计划内学生成绩,执行完成");
          } else {
              LOG.info("启动定时更新统计实践参与计划内学生成绩，获取锁失败");
          }
    }
    
    @Scheduled(cron = "0 0 5 * * ?")
    public void counselorCountTask() {
    	  if (distributeLock.getCounselorCountLock()) {
              LOG.info("开始启动定时统计辅导员指导过程信息");
              counselorCountService.counselorCountTask();
              LOG.info("定时更新辅导员指导过程信息,执行完成");
          } else {
              LOG.info("启动定时更新辅导员指导过程信息，获取锁失败");
          }
    }
    
    @Scheduled(cron = "0 20 5 * * ?")
    public void openCounselorCallTask() {
    	  if (distributeLock.openCounselorCallLock()) {
              LOG.info("开始开启辅导员点名任务");
              counselorCountService.openCounselorCallTask();
              LOG.info("开启辅导员点名任务,执行完成");
          } else {
              LOG.info("开启辅导员点名任务，获取锁失败");
          }
    }
    
    @Scheduled(cron = "0 40 5 * * ?")
    public void closeCounselorCallTask() {
    	  if (distributeLock.closeCounselorCallLock()) {
              LOG.info("开始关闭辅导员点名任务");
              counselorCountService.closeCounselorCallTask();
              LOG.info("关闭辅导员点名任务,执行完成");
          } else {
              LOG.info("关闭辅导员点名任务，获取锁失败");
          }
    }
  
}
