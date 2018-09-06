package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.async.AsyncTaskBase;
import com.aizhixin.cloud.rollcall.domain.ScheduleInClassRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 课后任务
 */
@Component
public class RollcallClassOutTaskPreprocessService {
    private final static Logger LOG = LoggerFactory.getLogger(RollcallClassOutTaskPreprocessService.class);
    @Autowired
    private RedisDataService redisDataService;
    @Lazy
    @Autowired
    private AsyncTaskBase asyncTaskBase;

    /**
     * 定时处理课后任务
     */
    public void processOutClassStart() {
        Date current = new Date();
        long time = current.getTime();
        Set<Long> orgIds = redisDataService.getCurrentdayOrgs();
        if (null == orgIds || orgIds.isEmpty()) {
            LOG.info("课后任务：当前需要处理的学校列表不存在。");
            return;
        }

        for (Long orgId : orgIds) {
            LOG.info("开始学校ID({})课后处理任务", orgId);
            List<ScheduleRedisDomain> preSchedules = new ArrayList<>();// 初步过滤结果，从当前正在上课的数据里边读取，减少过滤
            List<ScheduleRedisDomain> preSchedulesTrue = new ArrayList<>();// 最终要进入下课任务列表的数据
            Set<String> sids = new HashSet<>();

            List<ScheduleInClassRedisDomain> currentInclassScheduleAndRulerIdList = redisDataService.getOrgInClassSchedule(orgId);// 当前本校正在上课的课堂ID和课堂规则ID列表
            if (null != currentInclassScheduleAndRulerIdList && !currentInclassScheduleAndRulerIdList.isEmpty()) {
                for (ScheduleInClassRedisDomain inclass : currentInclassScheduleAndRulerIdList) {
                    sids.add(inclass.getScheduleId().toString());
                }
                preSchedules = redisDataService.getSchedules(orgId, sids);
            }

            if (null != preSchedules && !preSchedules.isEmpty()) {
                long end = -1;
                for (ScheduleRedisDomain pre : preSchedules) {
                    if (null != pre.getEndDate()) {
                        end = pre.getEndDate().getTime();
                        if (end - time < RollcallClassInTaskPreprocessService.FIVTH_MINUTER && end - time >= RollcallClassInTaskPreprocessService.FIVE_MINUTER) {// 5---15分钟内下课的并且未进入下课历史记录的课堂数据
                            preSchedulesTrue.add(pre);
                        }
                    }
                }
                LOG.info("学校ID({})本次添加新课后任务，总共查询到15分钟内数据({})条", orgId, preSchedulesTrue.size());
                if (preSchedulesTrue.size() > 0) {// 进入异步定时任务
                    asyncTaskBase.stopScheduleClasses(preSchedulesTrue, time, orgId);
                }
            } else {
                LOG.info("学校ID({})本次课后任务没有添加新数据，因为没有正在上课的数据", orgId);
            }
        }
    }
}
