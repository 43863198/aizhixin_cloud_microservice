package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.scheduling.MySchedulingService;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.core.RollCallConstants;
import com.aizhixin.cloud.rollcall.domain.*;
import com.aizhixin.cloud.rollcall.entity.RollCall;
import com.aizhixin.cloud.rollcall.entity.ScheduleRollCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 退出课堂 按照学校为单位组织数据结构
 */
@Component
public class ClassOutTaskPreprocessService {
    private final static long FIVE_SS = 5000;// 5秒
    private final static Logger LOG = LoggerFactory.getLogger(ClassOutTaskPreprocessService.class);
    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Autowired
    private ClassInTaskPreprocessService classInTaskPreprocessService;
    @Autowired
    private RollCallService rollCallService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

//    @Autowired
//    private PushMonitor pushMonitor;

    @Lazy
    @Autowired
    private MySchedulingService mySchedulingService;

    /**
     * 将要退出课堂内模式时间控制
     *
     * @param preSchedules 准备退出课堂相关的详细信息
     * @param time 任务启动的基准时间
     */
    public void timeToOutClasses(List<ScheduleRedisDomain> preSchedules, long time, Long orgId) {
        if (null != preSchedules && !preSchedules.isEmpty()) {
            ScheduleRedisDomain s = preSchedules.get(0);
            long temp = s.getEndDate().getTime() - time;
            if (temp <= FIVE_SS) {// 小于5秒钟的数据立即开始
                startStudentOutClasses(preSchedules, orgId);
            } else {
                try {
                    Thread.sleep(temp - FIVE_SS);// 休眠直到课程结束前5秒钟
                    startStudentOutClasses(preSchedules, orgId);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.warn("课后预处理还有({})条数据在等待退出中，但等待被意外终止", preSchedules.size());
                }
            }
        }
    }

    public boolean isNormal(Date classStartDate, int lateTime, Date timestamp) {
        if (0 == lateTime) {
            lateTime = 1000;
        }
        Long currentTime = 0l;
        if (timestamp == null) {
            currentTime = System.currentTimeMillis();
        } else {
            currentTime = timestamp.getTime();
        }
        long laterTime = (lateTime + 1) * 60 * 1000;
        Date date = new Date(currentTime - laterTime);
        if (classStartDate.before(date)) {
            return false;// 迟到
        }
        return true;// 正常
    }

    /**
     * 启动学生的退出当前课程
     *
     * @param preSchedules 准备退出当前课堂的数据
     * @param orgId 学校
     */
    public void startStudentOutClasses(List<ScheduleRedisDomain> preSchedules, Long orgId) {
        // 更新当前正在上课的课堂列表
        // 更新课堂规则
        // 更新学生当前正在课堂内的课堂列表
        // 生成学生的签到列表
        Set<String> rulerIds = new HashSet<>();
        Set<Long> outclassScheduleIds = new HashSet<>();// 准备退出课堂的课堂ID列表
        for (ScheduleRedisDomain s : preSchedules) {
            rulerIds.add(s.getScheduleRollCallId().toString());
            outclassScheduleIds.add(s.getScheduleId());
        }

        updateCurrentInclassScheduleList(orgId, outclassScheduleIds);// 更新当前本校正在上课的课堂ID和课堂规则ID列表
        Map<String, ScheduleRollCallRedisDomain> outclassRulerMap = getAndUpdateRulerOutclass(orgId, rulerIds);// 获取并且更新退出当前课堂的课堂规则
        Map<String, List<StudentInClassScheduleRedisDomain>> newCurrentInClassStudentScheduleAndRulerIdList = updateStudentScheduleList(orgId, outclassScheduleIds);// 更新学生当前正在课堂内的课堂列表
        Map<Long, Set<Long>> scheduleLeaveStudentIds = studentLeaveScheduleService.getScheduleLeaveStudents(outclassScheduleIds);// 课堂请假的学生列表

        List<RollCall> rollCallList = new ArrayList<>();
        Map<Long, ScheduleRollCallRedisDomain> ruleMap = new HashMap<>();
        Set ruleIdSet = new HashSet();
        for (ScheduleRedisDomain s : preSchedules) {

            long beginTime = System.currentTimeMillis();

            LOG.debug("执行课后任务:课堂ID({})。", s.getScheduleId());
            ScheduleRollCallRedisDomain ruler = outclassRulerMap.get(String.valueOf(s.getScheduleRollCallId()));
            if (null == ruler) {
                LOG.warn("退出课堂ID({})时，没有找到课堂规则ID({})", s.getScheduleId(), s.getScheduleRollCallId());
                continue;
            }

            ruleMap.put(ruler.getId(), ruler);
            ruleIdSet.add(ruler.getId());

            Set<Long> scheduleLeaveStudents = scheduleLeaveStudentIds.get(s.getScheduleId());// 当前课程所有请假的学生ID列表
            Set<Long> ss = redisDataService.getScheduleStudent(orgId, s.getScheduleId());// 获取课堂的学生数据
            if (null != ss && !ss.isEmpty()) {
                Set<String> studentIds = new HashSet<>();
                for (Long sid : ss) {
                    studentIds.add(sid.toString());
                }
                List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(orgId, s.getScheduleRollCallId(), studentIds);// 获取学生的签到列表
                if (null == studentRollcallList || studentRollcallList.isEmpty()) {
                    LOG.warn("退出课堂ID({})时，根据学生ID列表，没有找到学生签到列表数据", s.getScheduleId(), s.getScheduleRollCallId());
                    continue;
                }
                int execptionlCount = 0;
                for (RollcallRedisDomain rollcall : studentRollcallList) {// 计算异常考勤的数据，去除请假的学生之外，如果没有学生签到，本次考勤不计算
                    if (RollCallConstants.TYPE_UNCOMMITTED.equals(rollcall.getType()) || RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(rollcall.getType())) {
                        execptionlCount++;
                    }
                }
                if (execptionlCount == studentRollcallList.size()) {
                    ruler.setIsOpenRollcall(Boolean.FALSE);
                    ruler.setAttendance(String.valueOf(0));
                    LOG.warn("该节课，学生未进行签到操作，不计算入考勤。课堂ID({}),execptionCount:({})", s.getScheduleId(), execptionlCount);
                    continue;
                }

                LOG.info("需要进行课后处理的课程ID({})：,需要处理的学生数量为:{},其点名类型为:{}", s.getScheduleId(), studentRollcallList.size(), ruler.getRollCallType());
                doStudentRollcall(studentRollcallList, rollCallList, ruler, s, scheduleLeaveStudents);// 计算最终考勤结果

                ruler.setAttendance(rollCallService.calculateAttendanceRollCall(studentRollcallList, orgId));

                Map<String, RollcallRedisDomain> studentRollcallMap = new HashMap<>();
                for (RollcallRedisDomain domain : studentRollcallList) {
                    studentRollcallMap.put(domain.getStudentId().toString(), domain);
                }
                redisDataService.cacheScheduleRollcall(orgId, ruler.getId(), studentRollcallMap);

            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("退出课堂：根据课堂ID({}),没有找到对应的学生ID列表信息", s.getScheduleId());
                }
            }

            // 监控任务

//            try {
//                if (mySchedulingService.getExecute()) {
//                    pushMonitor.addOutClass(s, (System.currentTimeMillis() - beginTime), Boolean.TRUE, null);
//                }
//            } catch (Exception e) {
//
//            }
        }

        if (!outclassRulerMap.isEmpty()) {
            redisDataService.cacheRulers(orgId, outclassRulerMap);// 修改课堂规则，退出当前课堂
        }
        if (!newCurrentInClassStudentScheduleAndRulerIdList.isEmpty()) {
            redisDataService.cacheStudentInClasses(orgId, newCurrentInClassStudentScheduleAndRulerIdList);
            LOG.info("退出了学校ID({})的({})个学生的课堂数据", orgId, newCurrentInClassStudentScheduleAndRulerIdList.size());
        }

        // 课堂规则入库
        if (!ruleMap.isEmpty()) {
            List<ScheduleRollCall> scheduleRollcalls = scheduleRollCallService.findAllByIds(ruleIdSet);
            if (null != scheduleLeaveStudentIds) {
                for (ScheduleRollCall scheduleRollcall : scheduleRollcalls) {
                    ScheduleRollCallRedisDomain scheduleRollCallRedisDomain = ruleMap.get(scheduleRollcall.getId());
                    scheduleRollcall.setAttendance(scheduleRollCallRedisDomain.getAttendance());
                    scheduleRollcall.setClassRoomRollCall(scheduleRollCallRedisDomain.getClassroomRollCall());
                    scheduleRollcall.setIsInClassroom(Boolean.FALSE);
                    scheduleRollcall.setIsOpenRollcall(scheduleRollCallRedisDomain.getIsOpenRollcall());
                    scheduleRollcall.setCourseLaterTime(scheduleRollCallRedisDomain.getCourseLaterTime());
                    scheduleRollcall.setLocaltion(scheduleRollCallRedisDomain.getLocaltion());
                    scheduleRollcall.setRollCallType(scheduleRollCallRedisDomain.getRollCallType());
                }
            }
            scheduleRollCallService.save(scheduleRollcalls);
        }

        if (mySchedulingService.getExecute()) {
            if (!rollCallList.isEmpty()) {
                rollCallService.save(rollCallList);// 保存学生的签到列表
            }
        }

    }

    /**
     * 更新本学校当前正在课堂中的数据
     *
     * @param orgId
     * @param scheduleIds
     */
    private void updateCurrentInclassScheduleList(Long orgId, Set<Long> scheduleIds) {
        List<ScheduleInClassRedisDomain> currentInclassScheduleAndRulerIdList = redisDataService.getOrgInClassSchedule(orgId);// 当前本校正在上课的课堂ID和课堂规则ID列表
        if (null != currentInclassScheduleAndRulerIdList && !currentInclassScheduleAndRulerIdList.isEmpty()) {
            List<ScheduleInClassRedisDomain> newCurrentInclassScheduleAndRulerIdList = new ArrayList<>();
            for (ScheduleInClassRedisDomain sin : currentInclassScheduleAndRulerIdList) {
                if (!scheduleIds.contains(sin.getScheduleId())) {
                    newCurrentInclassScheduleAndRulerIdList.add(sin);
                }
            }
            if (!newCurrentInclassScheduleAndRulerIdList.isEmpty()) {
                redisDataService.cacheOrgInClassSchedule(orgId, newCurrentInclassScheduleAndRulerIdList);// 更新当前本校正在上课的课堂ID和课堂规则ID列表
            }
        }
    }

    /**
     * 更新退出当前课堂的课堂规则
     *
     * @param orgId
     * @param rulerIds
     * @return
     */
    private Map<String, ScheduleRollCallRedisDomain> getAndUpdateRulerOutclass(Long orgId, Set<String> rulerIds) {
        Map<String, ScheduleRollCallRedisDomain> outclassRulerMap = new HashMap<>();// 退出当前课堂的课堂规则
        List<ScheduleRollCallRedisDomain> rulers = redisDataService.getRulers(orgId, rulerIds);// 修改课堂规则，退出当前课堂
        if (null != rulers && !rulers.isEmpty()) {
            for (ScheduleRollCallRedisDomain ruler : rulers) {
                ruler.setIsInClassroom(CourseRollCallConstants.COURSE_OUT);// 课堂规则退出课堂
                outclassRulerMap.put(ruler.getId().toString(), ruler);
            }
        }
        return outclassRulerMap;
    }

    /**
     * 更新当前正在上课的学生的当前课堂及规则ID列表
     *
     * @param orgId
     * @param outclassScheduleIds
     * @return
     */
    private Map<String, List<StudentInClassScheduleRedisDomain>> updateStudentScheduleList(Long orgId, Set<Long> outclassScheduleIds) {
        Map<String, List<StudentInClassScheduleRedisDomain>> studentInClassesSchedules = new HashMap<>();// 学生当前可以签到的课堂ID规则ID列表
        for (Long scheduleId : outclassScheduleIds) {
            Set<Long> scheduelStudentIdList = redisDataService.getScheduleStudent(orgId, scheduleId);// 获取课堂的学生数据
            if (null != scheduelStudentIdList && !scheduelStudentIdList.isEmpty()) {
                classInTaskPreprocessService.initStudentCurrentSchedule(orgId, studentInClassesSchedules, scheduelStudentIdList);
                for (Long studentId : scheduelStudentIdList) {
                    List<StudentInClassScheduleRedisDomain> newCurrentInClassStudentScheduleAndRulerIdList = new ArrayList<>();
                    List<StudentInClassScheduleRedisDomain> currentInClassStudentScheduleAndRulerIdList = studentInClassesSchedules.get(studentId.toString());// 学生当前正在上课的课堂ID规则ID列表
                    if (null != currentInClassStudentScheduleAndRulerIdList && !currentInClassStudentScheduleAndRulerIdList.isEmpty()) {
                        for (StudentInClassScheduleRedisDomain d : currentInClassStudentScheduleAndRulerIdList) {
                            if (d.getScheduleId().longValue() != scheduleId.longValue()) {
                                newCurrentInClassStudentScheduleAndRulerIdList.add(d);
                            }
                        }
                    }
                    studentInClassesSchedules.put(studentId.toString(), newCurrentInClassStudentScheduleAndRulerIdList);// 更新学生当前正在上课的列表
                }
            }
        }
        return studentInClassesSchedules;
    }

    /**
     * 计算最终考勤结果并生成学生签到列表
     *
     * @param studentRollcallList
     * @param rollCallList
     * @param ruler
     * @param schedule
     * @param scheduleLeaveStudents
     */
    public void doStudentRollcall(List<RollcallRedisDomain> studentRollcallList, List<RollCall> rollCallList, ScheduleRollCallRedisDomain ruler, ScheduleRedisDomain schedule,
        Set<Long> scheduleLeaveStudents) {
        for (RollcallRedisDomain r : studentRollcallList) {
            String type = r.getType();
            r.setLastType(type);
            if (type.equals(RollCallConstants.TYPE_UNCOMMITTED)) {
                r.setType(RollCallConstants.TYPE_TRUANCY);
            } else if (type.equals(RollCallConstants.TYPE_COMMITTED)) {
                boolean isNormal = false;
                if (CourseRollCallConstants.OPEN_CLASSROOMROLLCALL.equals(type)) {
                    isNormal = true;
                } else {
                    isNormal = isNormal(schedule.getStartDate(), ruler.getCourseLaterTime() == null ? 15 : ruler.getCourseLaterTime(), r.getSignTime());
                }
                if (isNormal) {
                    r.setType(RollCallConstants.TYPE_NORMA);
                } else {
                    r.setType(RollCallConstants.TYPE_LATE);
                }
            } else if (type.equals(RollCallConstants.TYPE_EXCEPTION)) {
                r.setType(RollCallConstants.TYPE_TRUANCY);
            }

            if (null != scheduleLeaveStudents && scheduleLeaveStudents.contains(r.getStudentId())) {
                r.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            }
            if (null != rollCallList) {
                rollCallList.add(rollCallService.createRollCallByCache(r, schedule));
            }
        }
    }
}
