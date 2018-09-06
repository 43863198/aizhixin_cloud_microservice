package com.aizhixin.cloud.rollcall.common.scheduling;

import com.aizhixin.cloud.rollcall.common.service.DistributeLock;
import com.aizhixin.cloud.rollcall.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务入口
 */
@Component
public class MySchedulingService {
    final static private Logger LOG = LoggerFactory.getLogger(MySchedulingService.class);
    @Autowired
    private RollcallDayTaskPreprocessService rollcallDayTaskPreprocessService;
    @Autowired
    private RollcallClassInTaskPreprocessService rollcallClassInTaskPreprocessService;
    @Autowired
    private RollcallClassOutTaskPreprocessService rollcallClassOutTaskPreprocessService;

    @Autowired
    private RollCallMedianProcessService rollCallMedianProcessService;
    @Autowired
    private MyCacheService myCacheService;
    @Autowired
    private DistributeLock distributeLock;

    @Value("${schedule.execute}")
    private Boolean execute;

    /**
     * 临时天预处理定时任务 不生成数据库数据，利用目前点点逻辑生成当天课堂及课堂规则数据
     */
    @Scheduled(cron = "0 20 1 * * ?")
    public void dayDataTask() {
        if (distributeLock.getDayInitLock()) {
            if (!execute) {
                LOG.info("开始启动天粒度数据预处理任务");
                rollcallDayTaskPreprocessService.initRedisCurentDayAllOrg();
            } else {
                rollcallDayTaskPreprocessService.schoolDayPreprocessTask();
            }

        } else {
            LOG.info("启动天粒度数据预处理任务，获取锁失败");
        }
    }

    @Scheduled(cron = "0 55 23 * * ?")
    public void dayDataTaskClearPreDayCacher() {
        LOG.info("开始清理前一天的redis缓存任务");
        myCacheService.clearAllCache();// 不需要分布式锁
        myCacheService.clearZookeeperData();// 清理zookeeper垃圾数据
        LOG.info("清理前一天的redis缓存任务完成");
    }

    /**
     * 课前定时任务
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void classStartTask() {
        if (distributeLock.getInClassLock()) {
            LOG.info("开始启动课前数据预处理任务");
            rollcallClassInTaskPreprocessService.processInClassStart();
        } else {
            LOG.info("启动课前数据预处理任务，获取锁失败");
        }
    }

    /**
     * 课后定时任务 目前暂时不需要
     */
    @Scheduled(cron = "0 1/10 * * * ?")
    public void classEndTask() {
        if (distributeLock.getOutClassLock()) {
            LOG.info("开始启动课后数据预处理任务");
            rollcallClassOutTaskPreprocessService.processOutClassStart();
        } else {
            LOG.info("启动课后数据预处理任务，获取锁失败");
        }
    }

    /**
     * 中值计算任务
     */
    @Scheduled(cron = "30 * * * * ?")
    public void medianTask() {
        if (distributeLock.getMedianLock()) {
            LOG.info("开始启动中值计算处理任务");
            rollCallMedianProcessService.processRollCallMedian();
        } else {
            LOG.info("启动中值计算处理任务，获取锁失败");
        }
    }

    public Boolean getExecute() {
        return execute;
    }
}
