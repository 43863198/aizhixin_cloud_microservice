package com.aizhixin.cloud.dd.common.schedule;

import javax.annotation.PostConstruct;

import com.aizhixin.cloud.dd.counsellorollcall.thread.StudentSignThread;
import com.aizhixin.cloud.dd.counsellorollcall.thread.UpdateRollcallMessageThread;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.TempGroupService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallTeacherService;
import com.aizhixin.cloud.dd.rollcall.service.RollCallStatsService;
import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.aizhixin.cloud.dd.common.services.DistributeLock;
import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.AlarmClockService;
import com.aizhixin.cloud.dd.orgStructure.client.SynchronizedDataService;
import com.aizhixin.cloud.dd.rollcall.service.InitScheduleService;
import com.aizhixin.cloud.dd.rollcall.serviceV2.StuTeachClassService;
import com.aizhixin.cloud.dd.rollcall.thread.TaskThread;

/**
 * Created by LIMH on 2017/11/21.
 * <p>
 * 定时任务入口
 */
@Component
public class MyScheduleService {

    private final Logger log = LoggerFactory.getLogger(MyScheduleService.class);
    @Autowired
    private TaskThread taskThread;

    @Autowired
    private StudentSignThread studentSignThread;

//    暂无用
//    @Autowired
//    private UpdateRollcallCacheThread updateRollcallCacheThread;

    @Autowired
    private UpdateRollcallMessageThread updateRollcallMessageThread;

    @Autowired
    private DistributeLock distributeLock;

    @Lazy
    @Autowired
    private InitScheduleService initScheduleService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RollCallEverService rollCallEverService;

    @Autowired
    private RollCallStatsService rollCallStatsService;

    @Autowired
    private StuTeachClassService stuTeachClassService;
    @Autowired
    private SynchronizedDataService synchronizedDataService;

    @Autowired
    private AlarmClockService alarmClockService;

    @Autowired
    private TempGroupService groupService;

    @Autowired
    private CounselorRollcallTeacherService counselorRollcallTeacherService;

    @Value("${schedule.execute}")
    private Boolean execute = true;

    /**
     * 凌晨获取排课信息
     */
    @Scheduled(cron = "0 20 0 * * ?")
    public void dayDataTask() {
        if (distributeLock.getDayInitLock()) {
            if (execute) {
                log.info("开始启动天粒度数据预处理任务");
                initScheduleService.initSchedule();
            }
            distributeLock.delDayInitLock();
        } else {
            log.info("启动天粒度数据预处理任务，获取锁失败");
        }
    }

    /**
     * 课前课后
     */
    @Scheduled(cron = "1 0,10,20,30,40,50 * * * ?")
    public void classOutAndIn() {
        if (distributeLock.getClassOutAndInLock()) {
            if (execute) {
                log.info("开启课前课后预处理任务");
                scheduleService.executePerTenMinutes(null);
            }
            distributeLock.delClassOutAndInLock();
        } else {
            log.info("启动课前课后预处理任务，获取锁失败");
        }
    }

    /**
     * 关闭辅导员点名 无用
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void closeRollCallEver() {
        if (distributeLock.getCloseRollCallEverLock()) {
            log.info("开启关闭辅导员点名预处理任务");
            rollCallEverService.scheduleCloseRollCallEver();
            distributeLock.delCloseRollCallEverLock();
        } else {
            log.info("启动关闭辅导员点名预处理任务，获取锁失败");
        }
    }

    /**
     * 计算中值
     */
    @Scheduled(cron = "30 * * * * ?")
    public void countMedian() {
        if (distributeLock.getCountMedianLock()) {
            if (execute) {
                log.info("开启计算中值预处理任务");
                initScheduleService.checkRollCallTypeSchedule();
            }
            distributeLock.delCountMedianLock();
        } else {
            log.info("启动计算中值预处理任务，获取锁失败");
        }
    }

    /**
     * 初始化当天学生和教学班对应关系到redis
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void startInitTodayTeachClassIds() {
        if (distributeLock.getStartInitTodayTeachClassIdsLock()) {
            log.info("开启初始化当天学生和教学班对应关系到redis预处理任务");
            stuTeachClassService.saveStuTeachClassIds();
            distributeLock.delStartInitTodayTeachClassIdsLock();
        } else {
            log.info("启动初始化当天学生和教学班对应关系到redis预处理任务，获取锁失败");
        }
    }

    /**
     * @Title: refOrgData
     * @Description: 刷新组织架构任务
     * @return: void
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void refOrgData() {
        if (distributeLock.getRefOrgDataLock()) {
            synchronizedDataService.synData();
            distributeLock.delRefOrgDataLock();
        } else {
            log.info("刷新组织架构任务，获取锁失败");
        }
    }

    /**
     * @Title: refRollCallStats
     * @Description: 刷新考勤统计
     * @return: void
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void refRollCallStats() {
        if (distributeLock.getRefRollCallLock()) {
            rollCallStatsService.initStatsData();
            distributeLock.delRefRollCallLock();
        } else {
            log.info("刷新学生考勤统计，获取锁失败");
        }
    }

    /**
     * 导员点名定时任务（开启和结束）
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void startInitCounsellorRollcall() {
        if (distributeLock.getCounsellorRollcallLock()) {
            log.info("处理导员点名任务");
            alarmClockService.needAlarmClockTempGroup();
            distributeLock.delCounsellorRollcallLock();
        } else {
            log.info("处理导员点名任务，获取锁失败");
        }
    }

    /**
     * 导员点名应用点名规则
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void applyCounsellorRollcallRule() {
        if (distributeLock.getCounsellorRollcallRuleLock()) {
            log.info("导员点名应用点名规则");
            counselorRollcallTeacherService.applyCounsellorRollcallRule();
            distributeLock.delCounsellorRollcallRuleLock();
        } else {
            log.info("导员点名应用点名规则，获取锁失败");
        }
    }

    @Scheduled(cron = "1 0 0 * * ?")
    public void closeAllTempGroup() {
        if (distributeLock.getCounsellorRollcallCloseAllLock()) {
            log.info("处理关闭导员点名任务");
            groupService.closeAllTempGroup();
            distributeLock.delCounsellorRollcallCloseAllLock();
        } else {
            log.info("处理关闭导员点名任务，获取锁失败");
        }
    }

    /**
     * 定时删除在线选宿舍出现的死锁和dian一下产生的锁
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanDianTask() {
        distributeLock.deleteChooseRoomDieLock();
        distributeLock.deleteTaskDianLock();
    }

    @PostConstruct
    public void start() {
        log.info("redis监听启动--------");
        taskThread.start();
        log.info("队列模式读取redis学生签到数据线程启动--------");
        studentSignThread.start();

//        暂无用
//        log.info("更新学生点名redis数据线程启动--------");
//        updateRollcallCacheThread.start();

        log.info("更新学生点名消息线程启动--------");
        updateRollcallMessageThread.start();
    }


    public Boolean getExecute() {
        return execute;
    }
}
