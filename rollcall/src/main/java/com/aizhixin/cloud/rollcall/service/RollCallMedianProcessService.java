package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.async.AsyncTaskBase;
import com.aizhixin.cloud.rollcall.common.util.GDMapUtil;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.core.RollCallConstants;
import com.aizhixin.cloud.rollcall.domain.*;
import com.aizhixin.cloud.rollcall.entity.OrganSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by LIMH on 2017/11/20.
 */
@Component
public class RollCallMedianProcessService {
    private final static Logger LOG = LoggerFactory.getLogger(RollCallMedianProcessService.class);

    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private OrganSetService organSetService;
    @Lazy
    @Autowired
    private AsyncTaskBase asyncTaskBase;
//    @Lazy
//    @Autowired
//    private ClassOutTaskPreprocessService classOutTaskPreprocessService;

    /**
     * 获取需要计算中值的学校
     */
    public void processRollCallMedian() {
        Set<Long> currentdayOrgs = redisDataService.getCurrentdayOrgs();
        if (currentdayOrgs != null && !currentdayOrgs.isEmpty()) {
            for (Long orgId : currentdayOrgs) {
                // 启动异步处理线程
                asyncTaskBase.rollCallMedian(orgId);
            }
        }
    }

    /**
     * 计算某学校中值
     */
    public void rollCallMedianOrg(Long orgId) {
        try {
            Long beginTime = System.currentTimeMillis();
            OrganSet organSet = organSetService.getByOrganId(orgId);
            List<ScheduleInClassRedisDomain> scheduleInClassRedisDomains = redisDataService.getOrgInClassSchedule(orgId);
            if (scheduleInClassRedisDomains != null && !scheduleInClassRedisDomains.isEmpty()) {
                for (ScheduleInClassRedisDomain scheduleInClassRedisDomain : scheduleInClassRedisDomains) {
                    rollCallMedianSchedule(organSet, scheduleInClassRedisDomain);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("开始学校ID({}),计算中值任务耗时:({})", orgId, (System.currentTimeMillis() - beginTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("计算中值异常,学校id为:({})", orgId);
        }
    }

    public void rollCallMedianSchedule(OrganSet organSet, ScheduleInClassRedisDomain scheduleInClassRedisDomain) {

        Long orgId = organSet.getOrganId();
        Long scheduleId = scheduleInClassRedisDomain.getScheduleId();
        Long scheduleRollCallId = scheduleInClassRedisDomain.getScheduleRollCallId();

        try {
            Long signSize = redisDataService.getOrgInClassScheduleMedianSize(orgId, scheduleRollCallId);// 签到人数缓存
            ScheduleRedisDomain schedule = redisDataService.getSchedule(orgId, scheduleId);
            if (null == schedule) {
                LOG.warn("获取课程信息id({})异常", scheduleId);
                return;
            }
            // 小于设置的签到人数，直接返回
            if (null == signSize || signSize.longValue() <= organSet.getCalcount().longValue()) {
                return;
            }
            List<LocaltionDomain> localtionDomainList = redisDataService.getOrgInClassScheduleMedian(orgId, scheduleRollCallId);

            median(localtionDomainList, organSet, scheduleInClassRedisDomain, schedule.getStartDate());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("计算中值异常,排课id为:({})", scheduleId);
        }
    }

    /**
     * 中值计算
     *
     * @param list
     * @param organSet
     * @param scheduleInClassRedisDomain
     * @param startDate
     * @throws Exception
     */
    public void median(List<LocaltionDomain> list, OrganSet organSet, ScheduleInClassRedisDomain scheduleInClassRedisDomain, Date startDate) throws Exception {

        Long organId = organSet.getOrganId();
        Long scheduleRollCallId = scheduleInClassRedisDomain.getScheduleRollCallId();
        // 开始计算中值以及置信度
        GDMapUtil gdMap = new GDMapUtil();
        Set<String> studentIds = new HashSet<>();

        for (LocaltionDomain localtionDTO : list) {
            gdMap.put(localtionDTO.getLo());
            studentIds.add(localtionDTO.getId().toString());
        }

        int level = gdMap.getConfiLevel(organSet.getDeviation());
        if (level < organSet.getConfilevel().intValue()) {
            LOG.info("课堂规则id:({}),当前置信度({}),配置置信度({})", scheduleInClassRedisDomain.getScheduleRollCallId(), level, organSet.getConfilevel());
            return;
        }
        String medianValue = gdMap.getMidDistribution();

        ScheduleRollCallRedisDomain scheduleRollCallRedisDomain = redisDataService.getRuler(organId, scheduleRollCallId);

        if (null == scheduleRollCallRedisDomain) {
            LOG.warn("计算中值，获取课堂规则为空，课堂id为:({})", scheduleRollCallId);
        }

        // 将中值写入redis
        scheduleRollCallRedisDomain.setLocaltion(medianValue);
        redisDataService.cacheRuler(organId, scheduleRollCallId, scheduleRollCallRedisDomain);

        Thread.sleep(20);

        // 修改考勤
        // List<Long> ss = redisDataService.getScheduleStudent(organId, scheduleRollCallId);// 获取课堂的学生数据
        if (null != studentIds && !studentIds.isEmpty()) {
            // for (Long sid : ss) {
            // studentIds.add(sid.toString());
            // }
            List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(organId, scheduleRollCallId, studentIds);
            if (null == studentRollcallList || studentRollcallList.isEmpty()) {
                LOG.warn("退出课堂ID({})时，根据学生ID列表，没有找到学生签到列表数据", scheduleRollCallId);
                return;
            }
            Map rollcallMap = new HashMap<>();
            for (RollcallRedisDomain domain : studentRollcallList) {
                // 如果是正常状态就不进行计算了
                if (RollCallConstants.TYPE_NORMA.equals(domain.getType())) {
                    continue;
                }
                domain.setLastType(domain.getType());
                double distance = gdMap.compareMid(domain.getGpsLocation());
                int dis = (int)(distance / 10);
                if (dis < 1) {
                    dis = 1;
                }
                dis = Integer.parseInt(String.valueOf(dis) + "0");
                // 结果
                if (distance < organSet.getDeviation().intValue()) {
                    boolean isNormal = false;
                    if (CourseRollCallConstants.OPEN_CLASSROOMROLLCALL.equals(scheduleRollCallRedisDomain.getClassroomRollCall())) {
                        isNormal = true;
                    } else {
//                        isNormal = classOutTaskPreprocessService.isNormal(startDate, scheduleRollCallRedisDomain.getCourseLaterTime(), domain.getSignTime());
                    }
                    if (isNormal) {
                        domain.setType(RollCallConstants.TYPE_NORMA);
                    } else {
                        domain.setType(RollCallConstants.TYPE_LATE);
                    }
                    domain.setDistance("  <" + dis + "m");
                } else {
                    domain.setType(RollCallConstants.TYPE_EXCEPTION);
                    domain.setDistance("  >" + organSet.getDeviation().intValue() + "m");
                }
                rollcallMap.put(domain.getStudentId().toString(), domain);
            }
            redisDataService.cacheScheduleRollcall(organId, scheduleRollCallId, rollcallMap);
        }
    }

}
