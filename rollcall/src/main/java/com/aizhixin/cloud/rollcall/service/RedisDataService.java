package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDataService {
    public final static Integer DATA_TTL = 1;
    public final static String PRE = "rollcall:";
    public final static String RULER = PRE + "ruler:";// 签到规则
    public final static String TEACHER_RULER = PRE + "ruler:teacher:";// 老师签到规则
    public final static String SCHEDULE = PRE + "schedule:";// 课程表
    public final static String TEACHER_SCHEDULE = PRE + "teacher:schedule:";// 老师课表
    public final static String STUDENT_SCHEDULE = PRE + "student:schedule:";// 学生课表
    public final static String SCHEDULE_STUDENT = PRE + "schedule:student:";// 课堂学生
    // public final static String ORG_START_SCHEDULE = PRE + "start:schedule:";//开始的课堂
    public final static String ORG_STUDENT_INCLASS_SCHEDULE = PRE + "student:inclass:schedule:";// 学生开始的课堂
    public final static String ORG_INCLASS_SCHEDULE = PRE + "org:inclass:schedule:";// 学校当前开始的课堂
    public final static String ORG_INCLASS_SCHEDULE_MEDIAN = PRE + "org:inclass:schedule:median:";// 学校当前开始的课堂的中值
    public final static String CURRENTDAY_ORG = PRE + "currentday:org";// 当天有课表的所有学校
    public final static String SCHEDULE_ROLLCALL = PRE + "schedule:rollcall";// 课堂学生签到列表
    public final static String SCHEDULE_ORG_SCHDULE = PRE + "orgschedule";// 当天所有进入课堂的任务
    public final static String SCHEDULE_ORG_CURRENT_INCLASS = PRE + "cuurent";// 当天所有进入课堂的任务
    @Autowired
    private RedisTemplate redisTemplate;

    private void expire(String key) {
        redisTemplate.expire(key, DATA_TTL, TimeUnit.DAYS);
    }

    private void expire(String key, long ttl) {
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public void cacheRulers(Long orgId, Map<String, ScheduleRollCallRedisDomain> rulers) {
        String key = RULER + orgId.toString();
        redisTemplate.opsForHash().putAll(key, rulers);
        expire(key);
    }

    /**
     * 添加课堂规则
     *
     * @param orgId 学校ID
     * @param ruleId 课堂签到规则ID
     * @param rule 课堂签到规则
     */
    public void cacheRuler(Long orgId, Long ruleId, ScheduleRollCallRedisDomain rule) {
        String key = RULER + orgId.toString();
        redisTemplate.opsForHash().put(key, ruleId.toString(), rule);
        expire(key);
    }

    public void delRuler(Long orgId, Long ruleId) {
        String key = RULER + orgId.toString();
        redisTemplate.opsForHash().delete(key, ruleId.toString());
    }

    public List<ScheduleRollCallRedisDomain> getRulers(Long orgId, Set<String> rulerIds) {
        String key = RULER + orgId.toString();
        return (List<ScheduleRollCallRedisDomain>)redisTemplate.opsForHash().multiGet(key, rulerIds);
    }

    public ScheduleRollCallRedisDomain getRuler(Long orgId, Long ruleId) {
        String key = RULER + orgId.toString();
        return (ScheduleRollCallRedisDomain)redisTemplate.opsForHash().get(key, ruleId.toString());
    }

    /**
     * 批量保存老师的签到规则
     *
     * @param orgId
     * @param teacherRulers
     */
    public void cacheTeacherRulers(Long orgId, Map<String, Set<Long>> teacherRulers) {
        String key = TEACHER_RULER + orgId.toString();
        redisTemplate.opsForHash().putAll(key, teacherRulers);
        expire(key);
    }

    /**
     * 保存老师的签到规则
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @param ruleIds 课堂签到规则ID列表
     */
    public void cacheTeacherRulers(Long orgId, Long teacherId, Set<Long> ruleIds) {
        String key = TEACHER_RULER + orgId.toString();
        redisTemplate.opsForHash().put(key, teacherId.toString(), ruleIds);
        expire(key);
    }

    /**
     * 获取老师的课堂规则ID列表
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @return 课堂签到规则ID列表
     */
    public Set<Long> getTeacherRulers(Long orgId, Long teacherId) {
        String key = TEACHER_RULER + orgId.toString();
        return (Set<Long>)redisTemplate.opsForHash().get(key, teacherId.toString());
    }

    /**
     * 删除老师的课堂规则ID列表
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     */
    public void delTeacherRulers(Long orgId, Long teacherId) {
        String key = TEACHER_RULER + orgId.toString();
        redisTemplate.opsForHash().delete(key, teacherId.toString());
    }

    /**
     * 读取老师的所有课堂规则
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @return 所有课堂规则
     */
    public List<ScheduleRollCallRedisDomain> getTeacherScheduleRollCall(Long orgId, Long teacherId) {
        Set<Long> rids = getTeacherRulers(orgId, teacherId);
        List<ScheduleRollCallRedisDomain> rules = new ArrayList<>();
        if (null != rids && !rids.isEmpty()) {
            List<String> rc = new ArrayList<>();
            for (Long rid : rids) {
                rc.add(rid.toString());
            }
            return (List<ScheduleRollCallRedisDomain>)redisTemplate.opsForHash().multiGet(RULER + orgId, rc);
        }
        return rules;
    }

    /**
     * 写课表到Redis
     *
     * @param orgId 学校ID
     * @param schedules 课堂课表详细信息
     */
    public void cacheSchedules(Long orgId, Map<String, ScheduleRedisDomain> schedules) {
        String key = SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().putAll(key, schedules);
        expire(key);
    }

    public void cacheSchedule(Long orgId, Long scheduleId, ScheduleRedisDomain scheduleRedisDomain) {
        String key = SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().put(key, scheduleId.toString(), scheduleRedisDomain);
        expire(key);
    }

    /**
     * 获取多个课堂数据，根据ID列表
     *
     * @param orgId 学校ID
     * @param scheduleIds 课堂ID课表
     * @return
     */
    public List<ScheduleRedisDomain> getSchedules(Long orgId, Set<String> scheduleIds) {
        String key = SCHEDULE + orgId.toString();
        return (List<ScheduleRedisDomain>)redisTemplate.opsForHash().multiGet(key, scheduleIds);
    }

    public void delSchedule(Long orgId, Long scheduleId) {
        String key = SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().delete(key, scheduleId.toString());
    }

    /**
     * 按照学校获取所有课表数据
     *
     * @param orgId 学校ID
     * @return 课堂课表列表详细信息
     */
    public List<ScheduleRedisDomain> getOrgSchedule(Long orgId) {
        String key = SCHEDULE + orgId.toString();
        return (List<ScheduleRedisDomain>)redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取单个课堂信息
     *
     * @param orgId 学校ID
     * @param scheduleId 课堂IDd
     * @return 课堂课表详细信息
     */
    public ScheduleRedisDomain getSchedule(Long orgId, Long scheduleId) {
        String key = SCHEDULE + orgId.toString();
        return (ScheduleRedisDomain)redisTemplate.opsForHash().get(key, scheduleId.toString());
    }

    /**
     * 写老师的课堂信息
     *
     * @param orgId 学校ID
     * @param teacherId 老师ID
     * @param teacherScheduleIds 课堂ID列表
     */
    public void cacheTeacherSchedule(Long orgId, Long teacherId, Set<Long> teacherScheduleIds) {
        String key = TEACHER_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().put(key, teacherId.toString(), teacherScheduleIds);
        expire(key);
    }

    public void cacheTeacherSchedules(Long orgId, Map<String, Set<Long>> teacherScheduleIds) {
        String key = TEACHER_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().putAll(key, teacherScheduleIds);
        expire(key);
    }

    public Set<Long> getTeacherSchedule(Long orgId, Long teacherId) {
        String key = TEACHER_SCHEDULE + orgId.toString();
        return (Set<Long>)redisTemplate.opsForHash().get(key, teacherId.toString());
    }

    public void delTeacherSchedule(Long orgId, Long teacherId) {
        String key = TEACHER_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().delete(key, teacherId.toString());
    }

    /**
     * 写课堂的学生列表
     *
     * @param orgId 学校ID
     * @param scheduleId 课堂ID
     * @param students 学生列表
     */
    public void cacheScheduleStudent(Long orgId, Long scheduleId, Set<Long> students) {
        String key = SCHEDULE_STUDENT + orgId.toString();
        redisTemplate.opsForHash().put(key, scheduleId.toString(), students);
        expire(key);
    }

    public void cacheScheduleStudent(Long orgId, Map<String, Set<Long>> students) {
        String key = SCHEDULE_STUDENT + orgId.toString();
        redisTemplate.opsForHash().putAll(key, students);
        expire(key);
    }

    public void delScheduleStudent(Long orgId, Long scheduleId) {
        String key = SCHEDULE_STUDENT + orgId.toString();
        redisTemplate.opsForHash().delete(key, scheduleId.toString());
    }

    /**
     * 获取课堂的学生列表
     *
     * @param orgId 学校ID
     * @param scheduleId 课堂ID
     * @return 学生列表
     */
    public Set<Long> getScheduleStudent(Long orgId, Long scheduleId) {
        String key = SCHEDULE_STUDENT + orgId.toString();
        return (Set<Long>)redisTemplate.opsForHash().get(key, scheduleId.toString());
    }

    /**
     * 写学生的课堂数据
     *
     * @param orgId 学校ID
     * @param schedules 学生的课堂列表ID
     */
    public void cacheStudentSchedule(Long orgId, Long studentId, Set<Long> schedules) {
        String key = STUDENT_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().put(key, studentId.toString(), schedules);
        expire(key);
    }

    public void cacheStudentSchedule(Long orgId, Map<String, Set<Long>> schedules) {
        String key = STUDENT_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().putAll(key, schedules);
        expire(key);
    }

    /**
     * 获取学生的课堂ID列表
     *
     * @param orgId 学校ID
     * @param studentId 学生ID
     * @return 课堂列表ID
     */
    public Set<Long> getStudentSchedule(Long orgId, Long studentId) {
        return (Set<Long>)redisTemplate.opsForHash().get(STUDENT_SCHEDULE + orgId.toString(), studentId.toString());
    }

    /**
     * 获取学生的课表数据
     *
     * @param orgId 学校ID
     * @param studentId 学生ID
     * @return 课表
     */
    public List<ScheduleRedisDomain> getStudentScheduleDomain(Long orgId, Long studentId) {
        Set<Long> schedules = getStudentSchedule(orgId, studentId);

        if (null != schedules && !schedules.isEmpty()) {
            Set<String> scheduleids = new HashSet<>();
            for (Long sid : schedules) {
                scheduleids.add(sid.toString());
            }
            return (List<ScheduleRedisDomain>)redisTemplate.opsForHash().multiGet(SCHEDULE + orgId.toString(), scheduleids);
        }
        return null;
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        Set<String> keys = redisTemplate.keys(PRE + "*");
        if (null != keys && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 写入当前进入课堂模式的学生及对应的课堂数据
     *
     * @param orgId 学校ID
     * @param studentInClasses 已经或准备进入课堂模式的数据
     */
    public void cacheStudentInClasses(Long orgId, Map<String, List<StudentInClassScheduleRedisDomain>> studentInClasses) {
        String key = ORG_STUDENT_INCLASS_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().putAll(key, studentInClasses);
        expire(key);
    }

    /**
     * 获取当前正在上课的学生课堂列表
     *
     * @param orgId 学校ID
     * @param studentId 学生
     * @return 学生当前正在上课的课堂列表
     */
    public List<StudentInClassScheduleRedisDomain> getStudentInClasses(Long orgId, Long studentId) {
        return (List<StudentInClassScheduleRedisDomain>)redisTemplate.opsForHash().get(ORG_STUDENT_INCLASS_SCHEDULE + orgId.toString(), studentId.toString());
    }

    public List<List<StudentInClassScheduleRedisDomain>> getStudentInClasses(Long orgId, Set<String> studentIds) {
        return (List<List<StudentInClassScheduleRedisDomain>>)redisTemplate.opsForHash().multiGet(ORG_STUDENT_INCLASS_SCHEDULE + orgId.toString(), studentIds);
    }

    /**
     * 写入当前学校进入当前课堂的课堂ID及课堂规则ID列表
     *
     * @param orgId
     * @param inClassScheduleList
     */
    public void cacheOrgInClassSchedule(Long orgId, List<ScheduleInClassRedisDomain> inClassScheduleList) {
        String key = ORG_INCLASS_SCHEDULE + orgId.toString();
        redisTemplate.opsForHash().put(key, SCHEDULE_ORG_CURRENT_INCLASS, inClassScheduleList);
        expire(key);
    }

    /**
     * 获取进入课堂的本校的所有课堂ID及课堂规则ID列表
     *
     * @param orgId
     * @return
     */
    public List<ScheduleInClassRedisDomain> getOrgInClassSchedule(Long orgId) {
        return (List<ScheduleInClassRedisDomain>)redisTemplate.opsForHash().get(ORG_INCLASS_SCHEDULE + orgId.toString(), SCHEDULE_ORG_CURRENT_INCLASS);
    }

    /**
     * 写入当天有课表的所有学校ID
     *
     * @param orgIds 学校ID列表
     */
    public void cacheCurrentdayOrgs(Set<Long> orgIds) {
        redisTemplate.opsForHash().put(CURRENTDAY_ORG, SCHEDULE_ORG_SCHDULE, orgIds);
        expire(CURRENTDAY_ORG);
    }

    /**
     * 获取当天有课表的所有学校ID列表
     *
     * @return 学校ID列表
     */
    public Set<Long> getCurrentdayOrgs() {
        return (Set<Long>)redisTemplate.opsForHash().get(CURRENTDAY_ORG, SCHEDULE_ORG_SCHDULE);
    }

    /**
     * 缓存签到规则对应的学生的签到列表
     *
     * @param orgId 学校ID
     * @param rulerId 课堂规则ID
     * @param studentRollcall 学生签到列表
     */
    public void cacheScheduleRollcall(Long orgId, Long rulerId, Map<String, RollcallRedisDomain> studentRollcall) {
        String key = SCHEDULE_ROLLCALL + orgId.toString() + ":" + rulerId;
        redisTemplate.opsForHash().putAll(key, studentRollcall);
        expire(key);
    }

    public List<RollcallRedisDomain> getScheduleRollcall(Long orgId, Long rulerId, Set<String> students) {
        String key = SCHEDULE_ROLLCALL + orgId.toString() + ":" + rulerId;
        return (List<RollcallRedisDomain>)redisTemplate.opsForHash().multiGet(key, students);
    }

    public void cacheStudentScheduleRollcall(Long orgId, Long rulerId, Long studentId, RollcallRedisDomain studentRollcall) {
        String key = SCHEDULE_ROLLCALL + orgId.toString() + ":" + rulerId;
        redisTemplate.opsForHash().put(key, studentId.toString(), studentRollcall);
        expire(key);
    }

    public RollcallRedisDomain getStudentScheduleRollcall(Long orgId, Long rulerId, Long studentId) {
        String key = SCHEDULE_ROLLCALL + orgId.toString() + ":" + rulerId;
        return (RollcallRedisDomain)redisTemplate.opsForHash().get(key, studentId.toString());
    }

    public Long getScheduleRollcallSize(Long orgId, Long rulerId) {
        String key = SCHEDULE_ROLLCALL + orgId.toString() + ":" + rulerId;
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 写入当前学校进入当前课堂的学生位置信息
     */
    public void cacheOrgInClassScheduleMedian(String orgIdAndscheduleId, Map<String, LocaltionDomain> studentLocaltions, long ttl) {
        String key = ORG_INCLASS_SCHEDULE_MEDIAN + orgIdAndscheduleId;
        redisTemplate.opsForHash().putAll(key, studentLocaltions);
        expire(key, ttl);
    }

    /**
     * 获取某学校某课堂学生的签到信息
     *
     * @param orgId
     * @return
     */
    public List<LocaltionDomain> getOrgInClassScheduleMedian(Long orgId, Long scheduleRollcallId) {
        String key = ORG_INCLASS_SCHEDULE_MEDIAN + orgId.toString() + ":" + scheduleRollcallId.toString();
        return (List<LocaltionDomain>)redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取某学校某节课堂为计算中之前的签到人数
     *
     * @param orgId
     * @return
     */
    public Long getOrgInClassScheduleMedianSize(Long orgId, Long scheduleRollcallId) {
        String key = ORG_INCLASS_SCHEDULE_MEDIAN + orgId.toString() + ":" + scheduleRollcallId.toString();
        return redisTemplate.opsForHash().size(key);
    }
}
