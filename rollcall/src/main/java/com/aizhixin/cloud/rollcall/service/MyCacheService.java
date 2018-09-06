package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.core.ErrorCode;
import com.aizhixin.cloud.rollcall.common.core.PublicErrorCode;
import com.aizhixin.cloud.rollcall.common.exception.CommonException;
import com.aizhixin.cloud.rollcall.common.service.DistributeLock;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.core.RollCallConstants;
import com.aizhixin.cloud.rollcall.domain.*;
import com.aizhixin.cloud.rollcall.entity.ScheduleRollCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 自定义缓存相关操作
 */
@Component
public class MyCacheService {
    private final static Logger LOG = LoggerFactory.getLogger(MyCacheService.class);
    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private TeachingclassService teachingclassService;
    @Autowired
    private RollCallService rollCallService;
    @Autowired
    private DistributeLock distributeLock;

    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;
    @Lazy
    @Autowired
    private ClassInTaskPreprocessService classInTaskPreprocessService;

    /**
     * 初始化课堂规则、老师课堂规则、课堂信息（课表数据）、老师课表、教学班列表
     *
     * @param dbRulers 数据库课堂规则（内含数据库课堂信息）
     * @param rulers 缓存课堂规则
     * @param teacherRulers 教学规则列表
     * @param schedules 课堂信息
     * @param teacherSchedules 教师的课堂信息
     * @param teachingclassSchedules 教学班列表
     */
    private void initRulerScheduleTeachingclass(List<ScheduleRollCall> dbRulers, Map<String, ScheduleRollCallRedisDomain> rulers, Map<String, Set<Long>> teacherRulers,
        Map<String, ScheduleRedisDomain> schedules, Map<String, Set<Long>> teacherSchedules, Map<Long, Set<Long>> teachingclassSchedules) {
        for (ScheduleRollCall rule : dbRulers) {
            ScheduleRedisDomain s = new ScheduleRedisDomain(); // 课堂信息
            ScheduleRollCallRedisDomain r = new ScheduleRollCallRedisDomain();// 课堂规则
            scheduleRollCallService.objCopy(rule, s, r);// 课堂及规则的从数据库实体对象初始化相关字段
            Long teacherId = s.getTeacherId();
            if (null != teacherId) {// 初始化老师的课表和课堂规则列表
                String teacherStr = teacherId.toString();
                Set<Long> scheduleIds = teacherSchedules.get(teacherStr);
                if (null == scheduleIds) {
                    scheduleIds = new HashSet<>();
                    teacherSchedules.put(teacherStr, scheduleIds);
                }
                if (!scheduleIds.contains(s.getScheduleId())) {
                    scheduleIds.add(s.getScheduleId());
                }
                Set<Long> rulerIds = teacherRulers.get(teacherStr);
                if (null == rulerIds) {
                    rulerIds = new HashSet<>();
                    teacherRulers.put(teacherStr, rulerIds);
                }
                if (!rulerIds.contains(r.getId())) {
                    rulerIds.add(r.getId());
                }
            } else {
                LOG.warn("课堂ID{}缺失老师ID信息", s.getScheduleId());
            }

            // 课堂规则如果开启打卡机，则添加教学班（用于同步学生数据）
            if (null != s.getTeachingclassId() && s.getTeachingclassId() > 0) {
                Set<Long> tcschedules = teachingclassSchedules.get(s.getTeachingclassId());
                if (null == tcschedules) {
                    tcschedules = new HashSet<>();
                    teachingclassSchedules.put(s.getTeachingclassId(), tcschedules);
                }
                if (!tcschedules.contains(s.getScheduleId())) {
                    tcschedules.add(s.getScheduleId());
                }

            } else {
                LOG.warn("课堂ID{}缺失教学班ID信息", s.getScheduleId());
            }

            schedules.put(s.getScheduleId().toString(), s);
            rulers.put(r.getId().toString(), r);
        }
    }

    /**
     * 初始化学生课堂列表和课堂学生列表 (如果没有开启打卡机，学生签到列表没有相应的数据) 做教学班ID和课堂ID转换操作（缓存是按照课堂ID来组织的）
     *
     * @param teachingclassSchedules 教学班课堂对应表
     * @param studentSchedules 学生的课堂列表
     * @param scheduleStudents 课堂的学生列表
     */
    private Set<Long> initScheduleStudentAndStudentScheduleAndRollcall(Map<Long, Set<Long>> teachingclassSchedules, Map<String, Set<Long>> studentSchedules,
        Map<String, Set<Long>> scheduleStudents, Map<Long, Map<String, RollcallRedisDomain>> rollcallMap, Map<String, ScheduleRedisDomain> schedules,
        Map<String, ScheduleRollCallRedisDomain> rulers) {
        Set<Long> studentIdSet = new HashSet<>();
        for (Long teachingClassId : teachingclassSchedules.keySet()) {// 按照教学班初始化学生信息
            List<StudentDomain> slist = teachingclassService.listStudents(teachingClassId);
            if (null == slist || slist.isEmpty()) {
                LOG.warn("教学班ID{}缺失学生信息", teachingClassId);
                continue;
            }
            Set<Long> scheduleIds = teachingclassSchedules.get(teachingClassId);
            if (null == scheduleIds || scheduleIds.isEmpty()) {
                LOG.warn("教学班ID{}缺失课堂信息", teachingClassId);
                continue;
            }

            for (StudentDomain s : slist) {
                studentIdSet.add(s.getStudentId());
            }

            Map<Long, Set<Long>> scheduleStuIds = new HashMap<>();
            Set<Long> sids = new HashSet<>();
            if (!scheduleIds.isEmpty()) {
                sids.addAll(scheduleIds);
                scheduleStuIds = studentLeaveScheduleService.getScheduleLeaveStudents(sids);// 请假课程的学生//课堂id及对应请假学生的id列表
            }

            for (Long sid : scheduleIds) {
                ScheduleRedisDomain schedule = schedules.get(sid.toString());// 课堂信息
                if (null == schedule) {
                    LOG.warn("课堂ID{}没有找到课堂信息", sid);
                    continue;
                }
                ScheduleRollCallRedisDomain ruler = rulers.get(schedule.getScheduleRollCallId().toString());
                if (null == ruler) {
                    LOG.warn("根据课堂ID({})没有找到课堂规则ID", sid);
                    continue;
                }
                Map<String, RollcallRedisDomain> studentsRollcallMap = rollcallMap.get(ruler.getId());
                if (null == studentsRollcallMap) {
                    studentsRollcallMap = new HashMap<>();
                    rollcallMap.put(ruler.getId(), studentsRollcallMap);
                }

                Set<Long> leaveStudentIds = scheduleStuIds.get(sid);// 课堂请假的学生数据
                initStudentSignList(ruler, schedule, leaveStudentIds, slist, studentSchedules, scheduleStudents, studentsRollcallMap);// 初始化课堂的学生签到列表
            }
        }
        return studentIdSet;
    }

    /**
     * 初始化学生签到列表
     * 
     * @param ruler
     * @param schedule
     * @param leaveStudentIds
     * @param studentList
     * @param studentSchedules
     * @param scheduleStudents
     * @param studentsRollcallMap
     */
    private void initStudentSignList(ScheduleRollCallRedisDomain ruler, ScheduleRedisDomain schedule, Set<Long> leaveStudentIds, List<StudentDomain> studentList,
        Map<String, Set<Long>> studentSchedules, Map<String, Set<Long>> scheduleStudents, Map<String, RollcallRedisDomain> studentsRollcallMap) {
        Set<Long> scheduleStudentList = new HashSet<>();
        for (StudentDomain s : studentList) {// 逐个学生处理
            String studentIdStr = null;
            if (null != s.getStudentId() && s.getStudentId() > 0) {
                studentIdStr = s.getStudentId().toString();
            } else {
                LOG.warn("课堂{}缺失学生ID信息", schedule.getScheduleId());
                continue;
            }
            scheduleStudentList.add(s.getStudentId());// 教学班学生

            if (ruler.getIsOpenRollcall()) {// 如果打卡机开启，则初始化
                RollcallRedisDomain rollcallRedisDomain = new RollcallRedisDomain();// 签到列表
                rollCallService.objCopy(s, rollcallRedisDomain, schedule);

                if (null != leaveStudentIds && !leaveStudentIds.isEmpty() && leaveStudentIds.contains(rollcallRedisDomain.getStudentId())) {
                    rollcallRedisDomain.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                }
                studentsRollcallMap.put(studentIdStr, rollcallRedisDomain);
            }

            Set<Long> stuSchedules = studentSchedules.get(studentIdStr);// 学生课程表
            if (null == stuSchedules) {
                stuSchedules = new HashSet<>();
                studentSchedules.put(studentIdStr, stuSchedules);
            }
            stuSchedules.add(schedule.getScheduleId());
        }
        scheduleStudents.put(schedule.getScheduleId().toString(), scheduleStudentList);// 课程学生列表
    }

    /**
     * 更新老师的课堂信息和课堂规则数据
     *
     * @param addOrDel 新增true;删除false
     * @param teacherId 老师ID
     * @param ruleId 课堂规则ID
     * @param scheduleId 课堂ID
     * @param orgId 学校ID
     */
    private void teacherRulerAndScheduleAddOrDel(boolean addOrDel, Long teacherId, Long ruleId, Long scheduleId, Long orgId) {
        if (null != teacherId) {
            Set<Long> ruleIds = redisDataService.getTeacherRulers(orgId, teacherId);
            if (null == ruleIds) {
                ruleIds = new HashSet<>();
            }
            if (addOrDel) {
                if (!ruleIds.contains(ruleId)) {
                    ruleIds.add(ruleId);
                }
            } else {
                ruleIds.remove(ruleId);
            }
            if (ruleIds.size() > 0) {
                redisDataService.cacheTeacherRulers(orgId, teacherId, ruleIds);// 更新老师的课堂规则ID列表
            } else {
                redisDataService.delTeacherRulers(orgId, teacherId);
            }

            Set<Long> teacherScheduleIds = redisDataService.getTeacherSchedule(orgId, teacherId);
            if (null == teacherScheduleIds) {
                teacherScheduleIds = new HashSet<>();
            }
            if (addOrDel) {
                if (!teacherScheduleIds.contains(scheduleId)) {
                    teacherScheduleIds.add(scheduleId);
                }
            } else {
                teacherScheduleIds.remove(scheduleId);
            }
            if (teacherScheduleIds.size() > 0) {
                redisDataService.cacheTeacherSchedule(orgId, teacherId, teacherScheduleIds);// 更新老师课表
            } else {
                redisDataService.delTeacherSchedule(orgId, teacherId);
            }
        }
    }

    /**
     * 获取老师对应课程的签到规则
     *
     * @param orgId
     * @param teacherId
     * @param courseId
     * @return
     */
    private List<ScheduleRollCallRedisDomain> getTeacherCourseRulers(Long orgId, Long teacherId, Long courseId) {
        List<ScheduleRollCallRedisDomain> rulerList = new ArrayList<>();// 查找对应课程的课堂规则
        Set<Long> rids = redisDataService.getTeacherRulers(orgId, teacherId);// 获取老师的当天所有课堂规则
        if (null == rids || rids.isEmpty()) {
            // throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID(" + teacherId + ")当天没有课堂规则数据");
            return rulerList;
        }
        Set<String> rulerStrIds = new HashSet<>();
        for (Long r : rids) {
            rulerStrIds.add(r.toString());
        }
        List<ScheduleRollCallRedisDomain> rulers = redisDataService.getRulers(orgId, rulerStrIds);
        if (null == rulers || rulers.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID(" + teacherId + ")根据课堂规则ID列表没有查找到对应的课堂规则信息");
        }
        for (ScheduleRollCallRedisDomain r : rulers) {
            if (courseId.longValue() != r.getCourseId()) {
                continue;
            }
            rulerList.add(r);
        }
        return rulerList;
    }

    /**
     * 关闭打卡机并修改课堂规则
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @param courseId 课程ID
     * @param rollCallType 点名类型
     * @param lateTime 迟到时间
     */
    private void closePunchCardMachine(Long orgId, Long teacherId, Long courseId, String rollCallType, Integer lateTime) {
        Map<String, ScheduleRollCallRedisDomain> rulerMap = new HashMap<>();
        List<ScheduleRollCallRedisDomain> rulerList = getTeacherCourseRulers(orgId, teacherId, courseId);// 查找对应课程的课堂规则
        if (rulerList.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "老师ID(" + teacherId + ")当天没有课堂规则数据");
        }
        for (ScheduleRollCallRedisDomain r : rulerList) {
            r.setRollCallType(rollCallType);
            r.setCourseLaterTime(lateTime);
            r.setIsOpenRollcall(false);
            rulerMap.put(r.getId().toString(), r);
        }
        if (!rulerMap.isEmpty()) {
            redisDataService.cacheRulers(orgId, rulerMap);
        }
    }

    /**
     * 初始化学生的签到列表
     *
     * @param scheduleList 签到列表
     */
    public void initStudentListByScheduleRedisList(Long orgId, List<ScheduleRedisDomain> scheduleList, Map<String, ScheduleRollCallRedisDomain> rulers) {
        Map<Long, Set<Long>> teachingclassSchedules = new HashMap<>();// 教学班的课堂ID(用于教学班ID和课堂ID的匹配转换)
        Map<String, ScheduleRedisDomain> schedules = new HashMap<>();
        // Map<Long, Long> scheduleRulerMap = new HashMap<>();
        for (ScheduleRedisDomain s : scheduleList) {
            schedules.put(s.getScheduleId().toString(), s);
            // scheduleRulerMap.put(s.getScheduleId(), s.getScheduleRollCallId());
            if (null != s.getTeachingclassId() && s.getTeachingclassId() > 0) {
                Set<Long> tcschedules = teachingclassSchedules.get(s.getTeachingclassId());
                if (null == tcschedules) {
                    tcschedules = new HashSet<>();
                    teachingclassSchedules.put(s.getTeachingclassId(), tcschedules);
                }
                tcschedules.add(s.getScheduleId());
            } else {
                LOG.warn("课堂ID{}缺失教学班ID信息", s.getScheduleId());
            }
        }

        // Set<Long> studentIds = new HashSet<>();
        Map<String, Set<Long>> studentSchedules = new HashMap<>();// 学生课堂
        Map<String, Set<Long>> scheduleStudents = new HashMap<>();// 课堂学生

        Map<Long, Map<String, RollcallRedisDomain>> rollcallMap = new HashMap<>();

        // 分类初始化各类信息
        Set<Long> studentIds = initScheduleStudentAndStudentScheduleAndRollcall(teachingclassSchedules, studentSchedules, scheduleStudents, rollcallMap, schedules, rulers);
        if (!studentIds.isEmpty()) {
            for (Long sid : studentIds) {
                Set<Long> studentScheduleList = redisDataService.getStudentSchedule(orgId, sid);
                Set<Long> studentHaveScheduleList = studentSchedules.get(sid.toString());
                if (null == studentHaveScheduleList || studentHaveScheduleList.isEmpty()) {// 没有变化，不需要保存
                    studentSchedules.remove(sid.toString());
                    continue;
                }
                if (null != studentScheduleList && !studentScheduleList.isEmpty()) {
                    studentHaveScheduleList.addAll(studentScheduleList);
                    studentSchedules.put(sid.toString(), studentHaveScheduleList);
                }
            }
            if (!studentSchedules.isEmpty()) {
                redisDataService.cacheStudentSchedule(orgId, studentSchedules);// 写学生的课堂列表
            }
            if (!scheduleStudents.isEmpty()) {
                redisDataService.cacheScheduleStudent(orgId, scheduleStudents);// 写课堂的学生列表
            }
            if (!rollcallMap.isEmpty()) {
                // 按照课堂逐个写入学生的签到列表
                for (Map.Entry<Long, Map<String, RollcallRedisDomain>> e : rollcallMap.entrySet()) {
                    redisDataService.cacheScheduleRollcall(orgId, e.getKey(), e.getValue());
                }
            }
        } else {
            LOG.warn("初始化学生签到列表，没有找到学生信息");
        }
    }

    /**
     * 打开并重置签到规则
     *
     * @param orgId 学校ID
     * @param rulerIds 签到规则列表ID列表
     */
    private void openAndResetRuler(Long orgId, Set<String> rulerIds, String rollcalltype, String authCode, int lateTime) {
        if (!rulerIds.isEmpty()) {// 签到规则重置，打开打卡机
            List<ScheduleRollCallRedisDomain> rulers = redisDataService.getRulers(orgId, rulerIds);
            if (null != rulers || !rulers.isEmpty()) {
                Map<String, ScheduleRollCallRedisDomain> rulerMap = new HashMap<>();
                for (ScheduleRollCallRedisDomain r : rulers) {
                    r.setLocaltion(authCode);
                    r.setIsOpenRollcall(Boolean.TRUE);
                    r.setIsInClassroom(CourseRollCallConstants.COURSE_IN);
                    r.setRollCallType(rollcalltype);
                    r.setCourseLaterTime(lateTime);
                    rulerMap.put(r.getId().toString(), r);
                }
                redisDataService.cacheRulers(orgId, rulerMap);
            }
        }
    }

    /**
     * 重置学生的签到列表
     *
     * @param orgId 学校ID
     * @param schedules 课堂ID
     */
    private void resetStudentSignList(Long orgId, List<ScheduleRedisDomain> schedules) {
        if (null != schedules && !schedules.isEmpty()) {
            for (ScheduleRedisDomain schedule : schedules) {
                Set<Long> students = redisDataService.getScheduleStudent(orgId, schedule.getScheduleId());
                if (null != students && !students.isEmpty()) {
                    Set<String> stuStrIds = new HashSet<>();
                    for (Long sid : students) {
                        stuStrIds.add(sid.toString());
                    }
                    List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(orgId, schedule.getScheduleRollCallId(), stuStrIds);
                    if (null != studentRollcallList && !studentRollcallList.isEmpty()) {
                        Map<String, RollcallRedisDomain> rollcallMap = new HashMap<>();
                        for (RollcallRedisDomain r : studentRollcallList) {
                            r.resetSign();// 重置签到数据
                            rollcallMap.put(r.getStudentId().toString(), r);
                        }
                        redisDataService.cacheScheduleRollcall(orgId, schedule.getScheduleRollCallId(), rollcallMap);
                    } else {
                        LOG.warn("课堂ID({})没有找到学生签到列表信息", schedule.getScheduleId());
                    }
                } else {
                    LOG.warn("课堂ID({})没有找到学生信息", schedule.getScheduleId());
                }
            }
        }
    }

    /**
     * 初始化学校课堂规则、课堂信息及老师课堂规则、老师课堂、学生课堂、课堂学生关系、学生签到列表
     *
     * @param orgId 学校ID
     * @param rulerList 课堂规则
     */
    public void initSchoolScheduleAndRuler(Long orgId, List<ScheduleRollCall> rulerList) {
        Map<String, ScheduleRollCallRedisDomain> rulers = new HashMap<>();// 课堂规则
        Map<String, Set<Long>> teacherRulers = new HashMap<>();// 老师的课堂规则
        Map<String, ScheduleRedisDomain> schedules = new HashMap<>();// 课表
        Map<String, Set<Long>> teacherSchedules = new HashMap<>();// 老师的课表

        Map<Long, Set<Long>> teachingclassSchedules = new HashMap<>();// 教学班的课堂ID(用于教学班ID和课堂ID的匹配转换)
        // 初始化课堂规则、老师课堂规则、课堂信息（课表数据）、老师课表、教学班列表
        initRulerScheduleTeachingclass(rulerList, rulers, teacherRulers, schedules, teacherSchedules, teachingclassSchedules);

        Map<String, Set<Long>> studentSchedules = new HashMap<>();// 学生课堂
        Map<String, Set<Long>> scheduleStudents = new HashMap<>();// 课堂学生

        Map<Long, Map<String, RollcallRedisDomain>> rollcallMap = new HashMap<>();// 学生按照课堂的签到列表

        // 初始化学生课堂列表和课堂学生列表和学生签到列表
        initScheduleStudentAndStudentScheduleAndRollcall(teachingclassSchedules, studentSchedules, scheduleStudents, rollcallMap, schedules, rulers);

        redisDataService.cacheRulers(orgId, rulers);// 写课堂规则列表
        redisDataService.cacheTeacherRulers(orgId, teacherRulers);// 写老师课堂规则列表

        redisDataService.cacheSchedules(orgId, schedules);// 写课堂列表
        redisDataService.cacheTeacherSchedules(orgId, teacherSchedules);// 写老师课堂列表
        long start = System.currentTimeMillis();
        try {
            if (studentSchedules.size() > 500) {// 批量写，防止超时
                Map<String, Set<Long>> studentSchedulesBatch = new HashMap<>();// 学生课堂
                int i = 0;
                for (java.util.Map.Entry<String, Set<Long>> e : studentSchedules.entrySet()) {
                    studentSchedulesBatch.put(e.getKey(), e.getValue());
                    i++;
                    if (0 == i % 500) {
                        redisDataService.cacheStudentSchedule(orgId, studentSchedulesBatch);// 写学生的课堂列表
                        studentSchedulesBatch.clear();
                    }
                }
                if (!studentSchedulesBatch.isEmpty()) {
                    redisDataService.cacheStudentSchedule(orgId, studentSchedulesBatch);// 写学生的课堂列表
                }
            } else {
                redisDataService.cacheStudentSchedule(orgId, studentSchedules);// 写学生的课堂列表
            }
            LOG.info("写学校{}学生的当天的课堂列表时间：{}", orgId, System.currentTimeMillis() - start);
        } catch (Exception e) {
            LOG.warn("写学校{}学生的当天的课堂列表出错，时间：{}", orgId, System.currentTimeMillis() - start);
            LOG.warn("写学生的当天的课堂列表出错：orgId:{},size:{},error:", orgId, studentSchedules.size(), e);
        }
        redisDataService.cacheScheduleStudent(orgId, scheduleStudents);// 写课堂的学生列表

        // 按照课堂逐个写入学生的签到列表
        for (Map.Entry<Long, Map<String, RollcallRedisDomain>> e : rollcallMap.entrySet()) {
            redisDataService.cacheScheduleRollcall(orgId, e.getKey(), e.getValue());
        }
    }

    /**
     * 添加新课堂 应该是当前时间之后30分钟以外开始的当天课程，目前没有限制精确时间
     *
     * @param scheduleId 课堂ID
     */
    @Transactional(readOnly = true)
    public void addSchedule(Long scheduleId) {
        if (null == scheduleId || scheduleId <= 0) {
            return;
        }

        ScheduleRollCall dbRuler = scheduleRollCallService.findBySchedule(scheduleId);
        if (null == dbRuler || null == dbRuler.getSchedule()) {
            LOG.warn("添加新的课堂ID({})没有从数据库找到对应的课堂及课堂规则数据", scheduleId);
            return;
        }
        // 添加课堂规则
        ScheduleRedisDomain s = new ScheduleRedisDomain();
        ScheduleRollCallRedisDomain r = new ScheduleRollCallRedisDomain();
        scheduleRollCallService.objCopy(dbRuler, s, r);
        Long orgId = s.getOrgId();
        Long teacherId = s.getTeacherId();
        redisDataService.cacheRuler(s.getOrgId(), r.getId(), r);// 新增课堂规则
        redisDataService.cacheSchedule(orgId, s.getScheduleId(), s);// 新增课堂信息
        teacherRulerAndScheduleAddOrDel(true, teacherId, r.getId(), s.getScheduleId(), orgId);// 添加老师课堂规则及课堂信息

        List<ScheduleRedisDomain> needInitStudentList = new ArrayList<>();
        long rollcallSize = redisDataService.getScheduleRollcallSize(dbRuler.getSchedule().getOrganId(), dbRuler.getId());// 是否初始化学生，如果没有，需要初始化学生
        if (rollcallSize <= 0) {// 如果没有初始化学生，需要初始化学生
            needInitStudentList.add(s);
        }
        Map<String, ScheduleRollCallRedisDomain> rulers = new HashMap<>();
        rulers.put(r.getId().toString(), r);
        initStudentListByScheduleRedisList(orgId, needInitStudentList, rulers);// 初始化学生签到列表及课堂学生、修改学生课堂信息
    }

    /**
     * 停止当天的课程 应该是当前时间之后30分钟以外的开始的当天课程，目前没有限制精确时间
     *
     * @param scheduleId 课堂ID
     * @param orgId 学校ID
     */
    public void stopSchedule(Long scheduleId, Long orgId) {
        if (null == scheduleId || scheduleId <= 0) {
            return;
        }
        // 删除课堂规则
        ScheduleRedisDomain scheudle = redisDataService.getSchedule(orgId, scheduleId);
        if (null == scheudle) {
            LOG.warn("删除课堂ID({})没有从缓存中找到对应的数据", scheduleId);
        }
        Long sid = scheduleId;
        Long rid = scheudle.getScheduleRollCallId();
        Long teacherId = scheudle.getTeacherId();

        redisDataService.delRuler(orgId, rid);// 删除规则
        redisDataService.delSchedule(orgId, sid);// 删除课堂
        teacherRulerAndScheduleAddOrDel(false, teacherId, rid, sid, orgId);// 删除老师对应的课堂信息和课堂规则信息

        redisDataService.delScheduleStudent(orgId, sid);// 删除课堂及学生关系
        Set<Long> stdIds = redisDataService.getScheduleStudent(orgId, sid);// 课堂学生
        if (null != stdIds && !stdIds.isEmpty()) {
            Map<String, Set<Long>> studentScheduleMap = new HashMap<>();
            for (Long studentId : stdIds) {
                Set<Long> ss = redisDataService.getStudentSchedule(orgId, studentId);
                if (null != ss && !ss.isEmpty()) {
                    if (ss.remove(sid)) {
                        studentScheduleMap.put(studentId.toString(), ss);
                    }
                }
            }
            redisDataService.cacheStudentSchedule(orgId, studentScheduleMap);// 更新学生课堂信息
        }
    }

    /**
     * 清理所有缓存数据
     */
    public void clearAllCache() {
        redisDataService.clearAllCache();
    }

    /**
     * 清理所有缓存数据
     */
    public void clearZookeeperData() {
        distributeLock.cleanZookeeperTaskData();
    }

    /**
     * 获取老师的签到规则
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @return 签到规则列表
     */
    public List<ScheduleRollCallRedisDomain> getTeacherScheduleRollCall(Long orgId, Long teacherId) {
        return redisDataService.getTeacherScheduleRollCall(orgId, teacherId);
    }

    /**
     * 获取学生的课堂数据
     *
     * @param orgId 学校ID
     * @param studentId 学生ID
     * @return 当天课程表
     */
    public List<ScheduleRedisDomain> getStudentSchedule(Long orgId, Long studentId) {
        return redisDataService.getStudentScheduleDomain(orgId, studentId);
    }

    /**
     * 获取学生当前进入当前课堂的签到规则和课堂ID列表
     *
     * @param orgId 学校ID
     * @param studentId 学生ID
     * @return 当前时间课堂规则及课堂列表
     */
    public List<StudentInClassScheduleRedisDomain> getStudentCurrentInClass(Long orgId, Long studentId) {
        if (null != orgId && orgId > 0 && null != studentId && studentId > 0) {
            return redisDataService.getStudentInClasses(orgId, studentId);
        }
        return null;
    }

    /**
     * 获取当前学生当前课堂的签到规则
     *
     * @param orgId 学校ID
     * @param studentId 学生ID
     * @return 签到规则
     */
    public List<CurrentScheduleRulerDomain> getStudentCurrentRulerInClass(Long orgId, Long studentId) {
        List<CurrentScheduleRulerDomain> scheduleRulerList = new ArrayList<>();
        List<StudentInClassScheduleRedisDomain> list = getStudentCurrentInClass(orgId, studentId);
        if (null != list) {
            Map<Long, Long> rulerScheduleMap = new HashMap<>();
            Set<String> rulerIds = new HashSet<>();
            for (StudentInClassScheduleRedisDomain r : list) {
                rulerIds.add(r.getScheduleRollCallId().toString());
                rulerScheduleMap.put(r.getScheduleRollCallId(), r.getScheduleId());
            }
            List<ScheduleRollCallRedisDomain> rulers = redisDataService.getRulers(orgId, rulerIds);
            if (null != rulers) {
                for (ScheduleRollCallRedisDomain r : rulers) {
                    Long sheduleId = rulerScheduleMap.get(r.getId());
                    if (null != sheduleId) {
                        CurrentScheduleRulerDomain csrd = new CurrentScheduleRulerDomain(r);
                        csrd.setScheduleId(sheduleId);
                        scheduleRulerList.add(csrd);
                    }
                }
            }
        }
        return scheduleRulerList;
    }

    /**
     * 修改课堂打卡机
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @param courseId 课程ID
     * @param rollCallType 点名方式
     * @param lateTime 迟到时间
     * @param isOpen 是否打开打卡机
     * @param authCode
     */
    public void updateRuler(Long orgId, Long teacherId, Long courseId, String rollCallType, int lateTime, String isOpen, String authCode) {
        if (null == orgId || null == teacherId || null == courseId || StringUtils.isEmpty(rollCallType) || StringUtils.isEmpty(isOpen)) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校ID、老师ID、课程ID、签到规则类型、是否打开打卡机是必须的");
        }
        if (!CourseRollCallConstants.OPEN_ROLLCALL.equals(isOpen)) {// 如果不是打开打卡机，仅修改课堂规则
            closePunchCardMachine(orgId, teacherId, courseId, rollCallType, lateTime);// 关闭打卡机并且修改课堂规则
            return;
        }
        // 打开打卡机，需要对课中数据做一些处理
        Set<Long> teacherSchedules = redisDataService.getTeacherSchedule(orgId, teacherId);
        if (null == teacherSchedules || teacherSchedules.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "当前老师没有找到当天的课堂ID列表");
        }
        Set<String> sids = new HashSet<>();
        for (Long sid : teacherSchedules) {
            sids.add(sid.toString());
        }

        List<ScheduleRedisDomain> schedules = redisDataService.getSchedules(orgId, sids);
        if (null == schedules || schedules.isEmpty()) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课堂ID列表没有查找到对应的课堂信息");
        }

        List<ScheduleRedisDomain> schedulePre = new ArrayList<>();
        long time = System.currentTimeMillis();
        Set<String> needResetRulerList = new HashSet<>();// 需要重置签到规则
        Set<String> needResetRulerNextList = new HashSet<>();// 需要重置签到规则

        for (ScheduleRedisDomain schedule : schedules) {
            if (null != schedule && schedule.getCourseId().longValue() == courseId) {
                long startTime = schedule.getStartDate().getTime() - RollcallClassInTaskPreprocessService.FIVE_MINUTER;//
                if (time >= startTime && time <= schedule.getEndDate().getTime()) {// 判断已经开课的逻辑
                    schedulePre.add(schedule);
                    needResetRulerList.add(schedule.getScheduleRollCallId().toString());
                    continue;
                }
            }
            if (time < schedule.getStartDate().getTime()) {
                needResetRulerNextList.add(schedule.getScheduleRollCallId().toString());
            }
        }
        // Date currentDate = new Date();
        // 如果是课中，并且是打开打卡机，需要重置中值，学生的状态
        // 课后功能实现以后在处理这块的逻辑
        // List<ScheduleRedisDomain> needInitStudentList = new ArrayList<>();// 需要初始化学生
        // Set<String> needResetRulerList = new HashSet<>();// 需要重置签到规则
        // List<ScheduleRedisDomain> needResetSignList = new ArrayaList<>();// 需要重置学生的签到状态
        // Set<String> rulerIdList = new HashSet<>();
        // long time = System.currentTimeMillis();
        // for (ScheduleRedisDomain schedule : schedules) {
        // long startTime = schedule.getStartDate().getTime() - RollcallClassInTaskPreprocessService.FIVE_MINUTER;//
        // if (time >= startTime && time <= schedule.getEndDate().getTime()) {// 判断已经开课的逻辑
        // // 需重置中值和学生已经签到的状态
        // needResetRulerList.add(schedule.getScheduleRollCallId().toString());
        // needResetSignList.add(schedule);
        // }
        //
        // long rollcallSize = redisDataService.getScheduleRollcallSize(orgId, schedule.getScheduleRollCallId());// 是否初始化学生，如果没有，需要初始化学生
        // if (rollcallSize <= 0) {// 如果没有初始化学生，需要初始化学生
        // needInitStudentList.add(schedule);
        // if (!rulerIdList.contains(schedule.getScheduleRollCallId().toString())) {
        // rulerIdList.add(schedule.getScheduleRollCallId().toString());
        // }
        // }
        // }
        // if (!needInitStudentList.isEmpty()) {
        // List<ScheduleRollCallRedisDomain> rulerList = redisDataService.getRulers(orgId, rulerIdList);
        // Map<String, ScheduleRollCallRedisDomain> rulers = new HashMap<>();
        // for (ScheduleRollCallRedisDomain ruler : rulerList) {
        // rulers.put(ruler.getId().toString(), ruler);
        // }
        // initStudentListByScheduleRedisList(orgId, needInitStudentList, rulers);// 初始化学生签到列表及课堂学生、修改学生课堂信息
        // }
        // resetStudentSignList(orgId, needResetSignList);// 学生签到列表的重置
        if (!schedulePre.isEmpty()) {
            classInTaskPreprocessService.startStudentInClasses(schedulePre, orgId);
        }
        if (!needResetRulerList.isEmpty()) {
            openAndResetRuler(orgId, needResetRulerList, rollCallType, authCode, lateTime);// 签到规则重置，打开打卡机
        }
        if (!needResetRulerNextList.isEmpty()) {
            openAndResetRuler(orgId, needResetRulerNextList, rollCallType, "", lateTime);// 签到规则重置，打开打卡机
        }
    }

    /**
     * 开启数字随堂点 一定在课中
     *
     * @param orgId 学校ID
     * @param rulerId 规则ID
     * @param rollCallType 点名类型
     */
    public void open(Long orgId, Long scheduleId, Long rulerId, String rollCallType, String authCode) {
        if (null == orgId || null == rulerId) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校ID、签到规则ID是必须的");
        }
        ScheduleRollCallRedisDomain ruler = redisDataService.getRuler(orgId, rulerId);
        if (null == ruler) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据签到规则ID没有查找到签到规则数据");
        }
        ruler.setClassroomRollCall(CourseRollCallConstants.OPEN_CLASSROOMROLLCALL);
        ruler.setRollCallType(rollCallType);
        ruler.setLocaltion(authCode);
        ruler.setCourseLaterTime(0);
        ruler.setIsOpenRollcall(Boolean.TRUE);
        ruler.setScheduleId(scheduleId);
        redisDataService.cacheRuler(orgId, rulerId, ruler);
        List<ScheduleRedisDomain> needInitSignList = new ArrayList<>();
        // long rollcallSize = redisDataService.getScheduleRollcallSize(orgId, rulerId);
        // if (rollcallSize <= 0) {
        // 需初始化学生签到列表
        if (null != ruler.getScheduleId()) {
            ScheduleRedisDomain schedule = redisDataService.getSchedule(orgId, scheduleId);
            if (null != schedule) {
                needInitSignList.add(schedule);
            }
        }
        // }
        Map<String, ScheduleRollCallRedisDomain> rulers = new HashMap<>();
        rulers.put(ruler.getId().toString(), ruler);
        // 需要处理课中数据，如果没有初始化学生签到列表，需要初始化签到列表，如果是课中，需要重置中值，学生的状态(后续处理)
        if (!needInitSignList.isEmpty()) {
            initStudentListByScheduleRedisList(orgId, needInitSignList, rulers);// 初始化学生签到列表及课堂学生、修改学生课堂信息
            resetStudentSignList(orgId, needInitSignList);// 学生签到列表的重置
        }

        // if (!needInitSignList.isEmpty()) {
        // classInTaskPreprocessService.startStudentInClasses(needInitSignList, orgId);
        // }

    }

    /**
     * 关闭随堂点
     *
     * @param orgId 学校ID
     * @param rulerId 规则ID
     */
    public void close(Long orgId, Long rulerId) {// 一定在课中
        if (null == orgId || null == rulerId) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "学校ID、签到规则ID是必须的");
        }
        ScheduleRollCallRedisDomain ruler = redisDataService.getRuler(orgId, rulerId);
        if (null == ruler) {
            throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据签到规则ID没有查找到签到规则数据");
        }
        ruler.setClassroomRollCall(CourseRollCallConstants.CLOSED_CLASSROOMROLLCALL);

        redisDataService.cacheRuler(orgId, rulerId, ruler);
        // 立即计算学生的状态(后续处理)
    }

    /**
     * 获取缓存中的课堂规则
     *
     * @param orgId 学校ID
     * @param rulerId 规则ID
     * @return 课堂规则
     */
    public ScheduleRollCallRedisDomain getRuler(Long orgId, Long rulerId) {
        if (null != orgId && orgId > 0 && null != rulerId && rulerId > 0) {
            return redisDataService.getRuler(orgId, rulerId);
        }
        return null;
    }

    /**
     * 没有做时间验证，由调用方控制调用的时间
     *
     * @param orgId 学校ID
     * @param scheduleId 课堂规则ID
     * @param srcTeacherId 源老师ID
     * @param destTeacherId 修改最终目标老师ID
     * @param destTeacherName 目标老师的姓名
     */
    public void updateCacheCourseTeacher(Long orgId, Long scheduleId, Long srcTeacherId, Long destTeacherId, String destTeacherName) {
        Set<Long> teacherRulers = redisDataService.getTeacherRulers(orgId, srcTeacherId);
        Set<Long> teacherSchedules = redisDataService.getTeacherSchedule(orgId, srcTeacherId);

        if (null != teacherSchedules && !teacherSchedules.isEmpty()) {
            if (!teacherSchedules.contains(scheduleId)) {// 找到源老师的课堂信息
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "没有找到源老师的课堂信息");
            }
            ScheduleRedisDomain schedule = redisDataService.getSchedule(orgId, scheduleId);
            if (null == schedule) {
                throw new CommonException(ErrorCode.ID_IS_REQUIRED, "根据课堂ID没有找到源老师的课堂信息");
            }

            if (teacherSchedules.contains(scheduleId)) {
                teacherSchedules.remove(scheduleId);
                redisDataService.cacheTeacherSchedule(orgId, srcTeacherId, teacherSchedules);// 更新源老师的课堂ID列表
            }
            if (null == teacherRulers || teacherRulers.isEmpty()) {
                if (teacherRulers.contains(schedule.getScheduleRollCallId())) {
                    teacherRulers.remove(schedule.getScheduleRollCallId());
                    redisDataService.cacheTeacherRulers(orgId, srcTeacherId, teacherRulers);// 更新源老师的课堂规则ID列表
                }
            }

            // 更新课堂信息和目标老师的信息
            schedule.setTeacherId(destTeacherId);
            schedule.setTeacherName(destTeacherName);
            redisDataService.cacheSchedule(orgId, scheduleId, schedule);// 更新课堂信息里边的老师信息为目标老师

            teacherSchedules = redisDataService.getTeacherSchedule(orgId, destTeacherId);
            if (null == teacherSchedules) {
                teacherSchedules = new HashSet<>();
            }
            if (!teacherSchedules.contains(scheduleId)) {
                teacherSchedules.add(scheduleId);
                redisDataService.cacheTeacherSchedule(orgId, destTeacherId, teacherSchedules);// 更新目标老师的课堂ID列表
            }

            teacherRulers = redisDataService.getTeacherRulers(orgId, destTeacherId);
            if (null == teacherRulers) {
                teacherRulers = new HashSet<>();
            }
            if (!teacherRulers.contains(schedule.getScheduleRollCallId())) {
                teacherRulers.add(schedule.getScheduleRollCallId());
                redisDataService.cacheTeacherRulers(orgId, srcTeacherId, teacherRulers);// 更新目标老师的课堂规则ID列表
            }
        }
    }

    public boolean getLock() {
        return distributeLock.getLock();
    }

    public void setStudentLeave(Long orgId, Long scheduleId, Long studentId) {
        if (null == orgId || null == scheduleId || null == studentId || orgId <= 0 || scheduleId <= 0 || studentId <= 0) {
            return;
        }
        ScheduleRedisDomain schedule = redisDataService.getSchedule(orgId, scheduleId);
        if (null == schedule) {
            return;
        }
        RollcallRedisDomain rollcall = redisDataService.getStudentScheduleRollcall(orgId, schedule.getScheduleRollCallId(), studentId);
        if (null != rollcall) {
            rollcall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            redisDataService.cacheStudentScheduleRollcall(orgId, schedule.getScheduleRollCallId(), studentId, rollcall);
        }
    }

    /**
     * 初始化课堂对应的学生的签到列表
     *
     * @param schedule
     * @param leaveStudentIds
     * @param scheduelStudentIdList
     * @param studentInClassesSchedules
     * @return
     */
    public Map<String, RollcallRedisDomain> initStudentRollcallList(ScheduleRedisDomain schedule, Set<Long> leaveStudentIds, Set<Long> scheduelStudentIdList,
        Map<String, List<StudentInClassScheduleRedisDomain>> studentInClassesSchedules) {
        Map<String, RollcallRedisDomain> studentsRollcallMap = new HashMap<>();
        List<StudentDomain> slist = teachingclassService.listStudents(schedule.getTeachingclassId());
        if (null == slist || slist.isEmpty()) {
            LOG.warn("教学班ID{}缺失学生信息", schedule.getTeachingclassId());
            return studentsRollcallMap;
        }
        for (StudentDomain s : slist) {// 逐个学生处理
            String studentIdStr = null;
            if (null != s.getStudentId() && s.getStudentId() > 0) {
                studentIdStr = s.getStudentId().toString();
            } else {
                LOG.warn("教学班{}缺失学生ID信息", schedule.getTeachingclassId());
                continue;
            }
            scheduelStudentIdList.add(s.getStudentId());

            StudentInClassScheduleRedisDomain studentInClassScheduleRedisDomain = new StudentInClassScheduleRedisDomain();
            studentInClassScheduleRedisDomain.setScheduleId(schedule.getScheduleId());
            studentInClassScheduleRedisDomain.setScheduleRollCallId(schedule.getScheduleRollCallId());
            studentInClassScheduleRedisDomain.setStudentId(s.getStudentId());

            List<StudentInClassScheduleRedisDomain> studentInclassList = studentInClassesSchedules.get(studentIdStr);
            if (null == studentInclassList) {
                studentInclassList = new ArrayList<>();
                studentInClassesSchedules.put(studentIdStr, studentInclassList);
            }
            studentInclassList.add(studentInClassScheduleRedisDomain);

            RollcallRedisDomain rollcallRedisDomain = new RollcallRedisDomain();// 签到列表
            rollCallService.objCopy(s, rollcallRedisDomain, schedule);
            if (null != leaveStudentIds && !leaveStudentIds.isEmpty() && leaveStudentIds.contains(rollcallRedisDomain.getStudentId())) {
                rollcallRedisDomain.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
            }
            studentsRollcallMap.put(studentIdStr, rollcallRedisDomain);
        }
        return studentsRollcallMap;
    }

    /**
     * 学校当前正在上课的课堂ID和课堂规则ID列表
     *
     * @param orgId 学校ID
     * @return 正在课堂中的两个ID列表
     */
    public List<ScheduleInClassRedisDomain> getOrgCurrentInclassData(Long orgId) {
        List<ScheduleInClassRedisDomain> inClassScheduleList = redisDataService.getOrgInClassSchedule(orgId);// 学校当前进入课堂的数据，两个ID，方便后续处理
        if (null == inClassScheduleList) {
            inClassScheduleList = new ArrayList<>();
        }
        return inClassScheduleList;
    }

    /**
     * 根据规则ID列表，获取具体规则数据列表
     *
     * @param orgId 学校ID
     * @param rulerIdList 规则ID列表
     * @return 规则详细数据列表，可能返回null
     */
    public List<ScheduleRollCallRedisDomain> getOrgRulerList(Long orgId, Set<String> rulerIdList) {
        return redisDataService.getRulers(orgId, rulerIdList);
    }

    /**
     * 根据规则ID列表，获取具体规则数据Map
     *
     * @param orgId 学校ID
     * @param rulerIdList 规则ID列表
     * @return 规则详细数据Map，不为null
     */
    public Map<String, ScheduleRollCallRedisDomain> getOrgRulerMap(Long orgId, Set<String> rulerIdList) {
        Map<String, ScheduleRollCallRedisDomain> inClassRulerMap = new HashMap<>();
        List<ScheduleRollCallRedisDomain> rulers = getOrgRulerList(orgId, rulerIdList);
        if (null != rulers && !rulers.isEmpty()) {
            for (ScheduleRollCallRedisDomain ruler : rulers) {
                inClassRulerMap.put(ruler.getId().toString(), ruler);
            }
        }
        return inClassRulerMap;
    }

    /**
     * 获取课堂对应学生的签到列表的大小（判断课堂是否初始化学生签到列表）
     *
     * @param orgId 学校ID
     * @param rulerId 规则ID
     * @return 课堂对应的学生的签到列表的数量
     */
    public long getScheduleStudentRollcallSize(Long orgId, Long rulerId) {
        return redisDataService.getScheduleRollcallSize(orgId, rulerId);
    }

    public Set<Long> getScheduleStudentIdList(Long orgId, Long scheduleId) {
        return redisDataService.getScheduleStudent(orgId, scheduleId);
    }

    /**
     * 获取学生当天的所有签到状态
     *
     * @param orgId
     * @param studentId
     * @return
     */
    public Map<String, RollcallRedisDomain> getCurrentDayStudentSignType(Long orgId, Long studentId) {
        List<ScheduleRedisDomain> studentScheduleDomains = redisDataService.getStudentScheduleDomain(orgId, studentId);
        if (null == studentScheduleDomains || studentScheduleDomains.isEmpty()) {
            return null;
        }

        Map<String, RollcallRedisDomain> rollcallMap = new HashMap<>();
        if (studentScheduleDomains != null && !studentScheduleDomains.isEmpty()) {
            for (ScheduleRedisDomain studentScheduleDomain : studentScheduleDomains) {
                if (studentScheduleDomain == null) {
                    continue;
                }
                RollcallRedisDomain rollcallRedisDomain = redisDataService.getStudentScheduleRollcall(orgId, studentScheduleDomain.getScheduleRollCallId(), studentId);
                if (null == rollcallRedisDomain) {
                    continue;
                }
                rollcallMap.put(studentScheduleDomain.getScheduleRollCallId().toString(), rollcallRedisDomain);
            }
        }

        return rollcallMap;
    }

    /**
     * 获取学生当天课程列表
     *
     * @param orgId
     * @param studentId
     * @return
     */
    public List<ScheduleRedisDomain> getCurrentDayStudentCourseList(Long orgId, Long studentId) {
        return redisDataService.getStudentScheduleDomain(orgId, studentId);
    }

    public RollcallRedisDomain getStudentRollcall(Long orgId, Long scheduleId, Long studentId) {
        ScheduleRedisDomain schedule = redisDataService.getSchedule(orgId, scheduleId);
        if (null == schedule) {
            throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "课堂信息Redis中不存在");
        }
        return redisDataService.getStudentScheduleRollcall(orgId, schedule.getScheduleRollCallId(), studentId);
    }
}