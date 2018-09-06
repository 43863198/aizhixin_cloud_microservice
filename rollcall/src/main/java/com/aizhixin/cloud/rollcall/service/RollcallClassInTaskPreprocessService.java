package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.async.AsyncTaskBase;
import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 课前任务初始化
 */
@Component
public class RollcallClassInTaskPreprocessService {
    private final static Logger LOG = LoggerFactory.getLogger(RollcallClassInTaskPreprocessService.class);
    public final static long FIVTH_MINUTER = 15 * 60 * 1000;
    public final static long FIVE_MINUTER = 5 * 60 * 1000;
    @Autowired
    private RedisDataService redisDataService;

    @Lazy
    @Autowired
    private AsyncTaskBase asyncTaskBase;

    /**
     * 定时处理课前任务
     */
    public void processInClassStart() {
        Date current = new Date();
        long time = current.getTime();
        Set<Long> orgIds = redisDataService.getCurrentdayOrgs();
        if (null == orgIds || orgIds.isEmpty()) {
            LOG.info("课前任务：当前需要处理的学校列表不存在，请检查天任务是否运行。");
            return;
        }
        long temp = -1;
        for (Long orgId : orgIds) {
            LOG.info("开始学校ID({})课前处理任务", orgId);
            List<ScheduleRedisDomain> preSchedules = new ArrayList<>();//初步过滤结果
            List<ScheduleRedisDomain> schedules = redisDataService.getOrgSchedule(orgId);
            for (ScheduleRedisDomain s : schedules) {
                if (null != s.getStartDate()) {
                    temp = s.getStartDate().getTime() - time;
                    if (temp < FIVTH_MINUTER && temp >= FIVE_MINUTER) {//距离当前时间5到15分钟的数据，并且在历史和准备启动和当前列表中都不存在的课堂才能进入准确启动的任务
                        preSchedules.add(s);
                    }
                }
            }

            if (!preSchedules.isEmpty()) {
                //启动异步任务开始当前课程
                asyncTaskBase.startScheduleClasses(preSchedules, time, orgId);
                LOG.info("学校ID({})本次添加课前任务({})条，总共查询到5---15分钟内数据({})条", orgId, preSchedules.size(), preSchedules.size());
            } else {
                LOG.info("学校ID({})本次没有添加新课前任务，总共查询到5---15分钟内数据({})条", orgId, preSchedules.size());
            }
        }
    }
}
