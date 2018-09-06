package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.scheduling.MySchedulingService;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.domain.*;
import com.aizhixin.cloud.rollcall.entity.CourseRollCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 进入课堂
 */
@Component
public class ClassInTaskPreprocessService {
    private final static Logger LOG = LoggerFactory.getLogger(ClassInTaskPreprocessService.class);
    @Autowired
    private RedisDataService redisDataService;
    @Lazy
    @Autowired
    private MyCacheService myCacheService;
    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Autowired
    private CourseRollCallService courseRollCallService;

//    @Autowired
//    private PushMonitor pushMonitor;

    @Autowired
    private MySchedulingService mySchedulingService;

    /**
     * 将要进入课堂内模式时间控制
     * 
     * @param preSchedules 准备开始的课堂相关的详细信息
     * @param time 任务启动的基准时间
     */
    public void timeToSetupClasses(List<ScheduleRedisDomain> preSchedules, long time, Long orgId) {
        if (null != preSchedules && !preSchedules.isEmpty()) {
            ScheduleRedisDomain s = preSchedules.get(0);
            long temp = s.getStartDate().getTime() - time;
            try {
                if (temp < RollcallClassInTaskPreprocessService.FIVE_MINUTER) {// 小于5分钟的数据立即开始
                    startStudentInClasses(preSchedules, orgId);
                } else {
                    Thread.sleep(temp - RollcallClassInTaskPreprocessService.FIVE_MINUTER);// 休眠直到课程开始前5分钟
                    startStudentInClasses(preSchedules, orgId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOG.warn("课前处理启动异常:{}", e);
                LOG.warn("课前预处理还有({})条数据在等待开启中，但休眠被意外终止", preSchedules.size());
            }
        }
    }

    /**
     * 启动课堂规则及其学生进入当前课堂课程
     * 
     * @param preSchedules 准备进入当前课堂的课堂数据
     * @param orgId 学校
     */
    public void startStudentInClasses(List<ScheduleRedisDomain> preSchedules, Long orgId) {
        Set<Long> scheduleIds = new HashSet<>();
        List<ScheduleInClassRedisDomain> inClassScheduleList = myCacheService.getOrgCurrentInclassData(orgId);// 学校当前进入课堂的数据，两个ID，方便后续处理
        if (inClassScheduleList != null) {
            for (ScheduleInClassRedisDomain scheduleInClassRedisDomain : inClassScheduleList) {
                scheduleIds.add(scheduleInClassRedisDomain.getScheduleId());
            }
        }

        Map<String, ScheduleRollCallRedisDomain> inClassRulerMap = new HashMap<>();// 当天进入课堂的所有课堂规则，用于保存，更新课堂规则进入当前课堂
        Set<String> rulerIds = new HashSet<>();
        Set<Long> sids = new HashSet<>();
        for (ScheduleRedisDomain s : preSchedules) {
            rulerIds.add(s.getScheduleRollCallId().toString());
            sids.add(s.getScheduleId());
        }

        if (!rulerIds.isEmpty()) {
            Map<String, ScheduleRollCallRedisDomain> inClassRulerMapRead = myCacheService.getOrgRulerMap(orgId, rulerIds);// 根据规则ID列表，获取具体规则数据
            if (null != inClassRulerMapRead && !inClassRulerMapRead.isEmpty()) {
                for (java.util.Map.Entry<String, ScheduleRollCallRedisDomain> e : inClassRulerMapRead.entrySet()) {
                    if (null != e.getValue()) {
                        inClassRulerMap.put(e.getKey(), e.getValue());
                    }
                }
            }
        }

        Map<Long, Set<Long>> scheduleStuIds = new HashMap<>();
        if (!sids.isEmpty()) {
            scheduleStuIds = studentLeaveScheduleService.getScheduleLeaveStudents(sids);// 请假课程的学生
        }
        Map<Long, Map<String, RollcallRedisDomain>> rollcallMap = new HashMap<>();// 学生签到列表，最后一次更新学生签到列表，课前刚打开打卡机的数据
        Map<String, List<StudentInClassScheduleRedisDomain>> studentInClassesSchedules = new HashMap<>();// 学生当前可以签到的课堂ID规则ID列表
        for (ScheduleRedisDomain s : preSchedules) {

            long beginTime = System.currentTimeMillis();

            // 最后一次检查课堂规则，如果课堂规则状态为关闭状态，则放弃学生进入当前课程
            ScheduleRollCallRedisDomain ruler = inClassRulerMap.get(s.getScheduleRollCallId().toString());
            if (null != ruler) {
                CourseRollCall courseRollCall = courseRollCallService.get(s.getCourseId(), s.getTeacherId());// 老师课程设置规则
                if (null != courseRollCall) {
                    if (!CourseRollCallConstants.OPEN_ROLLCALL.equals(courseRollCall.getIsOpen())) {// 如果老师课程规则没有数据或者没有打开打卡机
                        ruler.setIsOpenRollcall(Boolean.FALSE);
                    } else {
                        ruler.setIsOpenRollcall(Boolean.TRUE);
                    }
                } // 课程打卡机，后续不需要在这儿查询
                if (!ruler.getIsOpenRollcall()) {// 没有开启打卡机
                    LOG.warn("课堂ID({}), 规则ID({})打卡机关闭", s.getScheduleId(), s.getScheduleRollCallId());
                    continue;
                }
            } else {
                LOG.warn("开启进入课堂ID({})时，没有找到课堂规则ID({})", s.getScheduleId(), s.getScheduleRollCallId());
                continue;
            }
            ruler.setIsInClassroom(CourseRollCallConstants.COURSE_IN);// 进入课堂中

            ScheduleInClassRedisDomain sc = new ScheduleInClassRedisDomain();
            sc.setScheduleId(s.getScheduleId());
            sc.setScheduleRollCallId(s.getScheduleRollCallId());

            Set<Long> scheduelStudentIdList = new HashSet<>();

            Map<String, RollcallRedisDomain> studentsRollcallMap
                = myCacheService.initStudentRollcallList(s, scheduleStuIds.get(s.getScheduleId()), scheduelStudentIdList, studentInClassesSchedules);// 初始化学生签到列表及请假信息
            if (!studentsRollcallMap.isEmpty()) {
                rollcallMap.put(ruler.getId(), studentsRollcallMap);
            }
            scheduelStudentIdList = redisDataService.getScheduleStudent(orgId, sc.getScheduleId());// 获取课堂的学生数据

            if (null == scheduelStudentIdList || scheduelStudentIdList.isEmpty()) {
                LOG.warn("进入课堂：根据课堂ID({}),没有找到对应的学生信息", sc.getScheduleId());
                continue;
            }
            if (!scheduleIds.contains(sc.getScheduleId())) {
                inClassScheduleList.add(sc);
            }
            initStudentCurrentSchedule(orgId, studentInClassesSchedules, scheduelStudentIdList);// 获取这堂课所有学生的当前课堂信息

            updateStudentCurrentSchedule(sc, studentInClassesSchedules, scheduelStudentIdList);// 更新学生的当前课堂列表信息

            // 添加监控任务
//            if (mySchedulingService.getExecute()) {
//                pushMonitor.addBeforeClass(s, (System.currentTimeMillis() - beginTime), Boolean.TRUE, null, null);
//            }
        }
        if (!inClassRulerMap.isEmpty()) {// 更新课堂规则，使这些规则进入上课状态
            redisDataService.cacheRulers(orgId, inClassRulerMap);
        }
        redisDataService.cacheOrgInClassSchedule(orgId, inClassScheduleList);// 学校进入当前课堂的信息，方便课后任务处理，计算中值，课后处理数据
        LOG.info("启动了学校ID({})的({})个课堂进入上课状态", orgId, inClassScheduleList.size());
        if (!studentInClassesSchedules.isEmpty()) {
            redisDataService.cacheStudentInClasses(orgId, studentInClassesSchedules);
            LOG.info("启动了学校ID({})的({})个学生的课堂数据", orgId, studentInClassesSchedules.size());
        }
        if (!rollcallMap.isEmpty()) {
            // 按照课堂逐个写入学生的签到列表
            for (Map.Entry<Long, Map<String, RollcallRedisDomain>> e : rollcallMap.entrySet()) {
                redisDataService.cacheScheduleRollcall(orgId, e.getKey(), e.getValue());
            }
        }
    }

    /**
     * 初始化学生当前正在进入课堂的数据到缓存中
     * 
     * @param orgId 学校ID
     * @param studentInClassesSchedules 缓存（学生当前正在进入课堂的数据）
     * @param scheduelStudentIdList 学生列表
     */
    public void initStudentCurrentSchedule(Long orgId, Map<String, List<StudentInClassScheduleRedisDomain>> studentInClassesSchedules, Set<Long> scheduelStudentIdList) {
        Set<String> scheduleStudentSet = new HashSet<>();
        for (Long studentId : scheduelStudentIdList) {
            scheduleStudentSet.add(studentId.toString());
        }
        List<List<StudentInClassScheduleRedisDomain>> studentInClassScheduleRedisDomainsList = redisDataService.getStudentInClasses(orgId, scheduleStudentSet);// 获取这堂课所有学生的当前课堂信息
        if (null != studentInClassScheduleRedisDomainsList && !studentInClassScheduleRedisDomainsList.isEmpty()) {// 初始化进入缓存中
            for (List<StudentInClassScheduleRedisDomain> sincs : studentInClassScheduleRedisDomainsList) {
                if (null == sincs) {
                    continue;
                }
                for (StudentInClassScheduleRedisDomain sinc : sincs) {
                    if (null == sinc || null == sinc.getStudentId()) {
                        // LOG.warn("学生当前课堂信息，缺失学生ID信息:{}", sinc);
                        continue;
                    }
                    List<StudentInClassScheduleRedisDomain> studentCurrentInclassList = studentInClassesSchedules.get(sinc.getStudentId().toString());
                    if (null == studentCurrentInclassList) {
                        studentCurrentInclassList = new ArrayList<>();
                        studentInClassesSchedules.put(sinc.getStudentId().toString(), studentCurrentInclassList);
                    }
                    studentCurrentInclassList.add(sinc);
                }
            }
        }
    }

    /**
     * 更新学生当前正在进入课堂的数据列表
     * 
     * @param sc 课堂及课堂规则ID
     * @param studentInClassesSchedules 学生的当前课堂的缓存
     * @param scheduelStudentIdList 学生列表
     */
    private void updateStudentCurrentSchedule(ScheduleInClassRedisDomain sc, Map<String, List<StudentInClassScheduleRedisDomain>> studentInClassesSchedules,
        Set<Long> scheduelStudentIdList) {
        for (Long studentId : scheduelStudentIdList) {
            StudentInClassScheduleRedisDomain studentInClassScheduleRedisDomain = new StudentInClassScheduleRedisDomain();
            studentInClassScheduleRedisDomain.setScheduleId(sc.getScheduleId());
            studentInClassScheduleRedisDomain.setScheduleRollCallId(sc.getScheduleRollCallId());
            studentInClassScheduleRedisDomain.setStudentId(studentId);
            List<StudentInClassScheduleRedisDomain> currentInClassStudentScheduleAndRulerIdList = studentInClassesSchedules.get(studentId.toString());// 学生当前正在上课的课堂ID规则ID列表
            if (null == currentInClassStudentScheduleAndRulerIdList) {
                currentInClassStudentScheduleAndRulerIdList = new ArrayList<>();
                studentInClassesSchedules.put(studentId.toString(), currentInClassStudentScheduleAndRulerIdList);
            }
            if (currentInClassStudentScheduleAndRulerIdList.isEmpty()) {// 大部分情况已经只执行这个逻辑
                currentInClassStudentScheduleAndRulerIdList.add(studentInClassScheduleRedisDomain);
            } else {// 正常情况，不应该进入这个逻辑，除非课程排重复了。
                // 以下用于判断学生的课堂列表是否已经存在同课堂的数据，如果不存在即添加，可以优化这部分
                boolean scheduleInStudentCurrentInclassList = false;
                for (StudentInClassScheduleRedisDomain sinc : currentInClassStudentScheduleAndRulerIdList) {
                    if (sc.getScheduleId().longValue() == sinc.getScheduleId()) {
                        scheduleInStudentCurrentInclassList = true;
                        break;
                    }
                }
                if (!scheduleInStudentCurrentInclassList) {
                    currentInClassStudentScheduleAndRulerIdList.add(studentInClassScheduleRedisDomain);
                }
            }
        }
    }
}
