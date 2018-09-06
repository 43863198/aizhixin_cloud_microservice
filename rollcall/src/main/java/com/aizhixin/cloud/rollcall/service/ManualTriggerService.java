package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.async.AsyncTaskBase;
import com.aizhixin.cloud.rollcall.domain.ScheduleInClassRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiagen
 * @date 2018/2/12 14:07
 */
@Service
public class ManualTriggerService {
    private final static Logger log = LoggerFactory.getLogger(ManualTriggerService.class);
    public final static long FIVTH_MINUTER = 15 * 60 * 1000;
    public final static long FIVE_MINUTER = 5 * 60 * 1000;
    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private ClassInTaskPreprocessService classInTaskPreprocessService;
    @Autowired
    private ClassOutTaskPreprocessService classOutTaskPreprocessService;

    /**
     * @author xiagen
     * @date 2018/2/12 14:13
     * @return void
     * @description 手动处理课前初始化数据
     */
    public void dealPreClass(Long orgId, Set<Long> scheduleIds) {
        List<ScheduleRedisDomain> inSchedules = new ArrayList<>();// 立刻初始的数据
        // 根据学校id获取该学校上课列表
        List<ScheduleRedisDomain> scheduleRedisDomainList = redisDataService.getOrgSchedule(orgId);
        // 根据学校id获取该学校进入课堂的信息
        List<ScheduleInClassRedisDomain> scheduleInClassRedisDomainList = redisDataService.getOrgInClassSchedule(orgId);
        // 获取进入课堂的id集合
        List<Long> scheduleIdList = new ArrayList<>();
        if (null != scheduleInClassRedisDomainList && 0 < scheduleInClassRedisDomainList.size()) {
            for (ScheduleInClassRedisDomain scheduleInClassRedisDomain : scheduleInClassRedisDomainList) {
                scheduleIdList.add(scheduleInClassRedisDomain.getScheduleId());
            }
        }
        for (ScheduleRedisDomain scheduleRedisDomain : scheduleRedisDomainList) {
            for (Long scheduleId : scheduleIds) {
                if (!scheduleIdList.contains(scheduleId) && scheduleId.longValue() == scheduleRedisDomain.getScheduleId()) {
                    inSchedules.add(scheduleRedisDomain);
                }
            }
        }
        if (!inSchedules.isEmpty()) {
            classInTaskPreprocessService.startStudentInClasses(inSchedules, orgId);
        }
    }

    /**
     * @author hsh
     * @date 2018/2/12 15:12
     * @return void
     * @description 手动结束课程数据
     */
    public void stopClass(Long orgId, Set<String> scheduleIds) {
        log.info("开始处理学校ID({})课后任务", orgId);
        // List<ScheduleRedisDomain> preSchedules = new ArrayList<>();// 需要下课的任务列表
        // Set<String> sids = new HashSet<>();

        // List<ScheduleInClassRedisDomain> currentInclassScheduleAndRulerIdList = redisDataService.getOrgInClassSchedule(orgId);// 当前本校正在上课的课堂ID和课堂规则ID列表
        // if (null != currentInclassScheduleAndRulerIdList && !currentInclassScheduleAndRulerIdList.isEmpty()) {
        // for (ScheduleInClassRedisDomain inclass : currentInclassScheduleAndRulerIdList) {
        // String sid = inclass.getScheduleId().toString();
        // for (String scheduleId : scheduleIds) {
        // if (scheduleId.equals(sid)) {
        // sids.add(sid);
        // break;
        // }
        // }
        // }
        // preSchedules = redisDataService.getSchedules(orgId, sids);
        // }

        List<ScheduleRedisDomain> preSchedules = redisDataService.getSchedules(orgId, scheduleIds);

        if (null != preSchedules && !preSchedules.isEmpty()) {
            log.debug("学校ID({}) 下课任务数据({})条", orgId, preSchedules.size());
            classOutTaskPreprocessService.startStudentOutClasses(preSchedules, orgId);
        } else {
            log.info("学校ID({})没有正在上课的数据", orgId);
        }
    }

}
