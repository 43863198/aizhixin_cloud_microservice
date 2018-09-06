package com.aizhixin.cloud.rollcall.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aizhixin.cloud.rollcall.common.util.DateUtil;
import com.aizhixin.cloud.rollcall.core.CourseRollCallConstants;
import com.aizhixin.cloud.rollcall.domain.RollcallRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRollCallRedisDomain;
import com.aizhixin.cloud.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.rollcall.entity.Schedule;
import com.aizhixin.cloud.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.rollcall.repository.ScheduleRollCallRepository;

@Service
public class ScheduleRollCallService {

    private final static Logger LOG = LoggerFactory.getLogger(ScheduleRollCallService.class);

    @Autowired
    private ScheduleRollCallRepository scheduleRollCallRepository;

    @Autowired
    private RollCallService rollCallService;

    @Autowired
    private RedisDataService redisDataService;

    @Transactional
    public ScheduleRollCall save(ScheduleRollCall scheduleRollCall) {
        return scheduleRollCallRepository.save(scheduleRollCall);
    }

    @Transactional
    public List<ScheduleRollCall> save(List<ScheduleRollCall> scheduleRollCalles) {
        return scheduleRollCallRepository.save(scheduleRollCalles);
    }

    @Transactional(readOnly = true)
    public ScheduleRollCall findBySchedule(Long scheduleId) {
        return scheduleRollCallRepository.findBySchedule_Id(scheduleId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleRollCall> findTeachDateAndOrgId(String teachDate, Long orgId) {
        return scheduleRollCallRepository.findBySchedule_teachDateAndSchedule_organId(teachDate, orgId);
    }

    @Transactional(readOnly = true)
    public List<ScheduleRollCall> findAllByIds(Set<Long> ids) {
        return scheduleRollCallRepository.findAllByIdIn(ids);
    }

    /**
     * 课程设置对象初始化
     *
     * @param scheduleRollCall 课堂设置对象
     * @param schedule 课表对象
     * @param courseRollCall 老师课程设置对象
     */
    public void initRuler(ScheduleRollCall scheduleRollCall, Schedule schedule, CourseRollCall courseRollCall) {
        scheduleRollCall.setSchedule(schedule);
        if (null != courseRollCall) {
            scheduleRollCall.setRollCallType(courseRollCall.getRollCallType());
            scheduleRollCall.setCourseLaterTime(courseRollCall.getLateTime());

            if (!CourseRollCallConstants.OPEN_ROLLCALL.equals(courseRollCall.getIsOpen())) {// 如果老师课程规则没有数据或者没有打开打卡机
                scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
            } else {
                scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);
            }
        } else {
            scheduleRollCall.setRollCallType(CourseRollCallConstants.TYPE_ROLL_CALL_AUTOMATIC);
            scheduleRollCall.setCourseLaterTime(15);
            scheduleRollCall.setIsOpenRollcall(Boolean.FALSE);
        }
        scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.NOT_OPEN_CLASSROOMROLLCALL);

        scheduleRollCall.setIsInClassroom(Boolean.FALSE);
        scheduleRollCall.setLocaltion("");
        scheduleRollCall.setIsInClassroom(Boolean.FALSE);
    }

    /**
     * 课表及课堂规则属性拷贝
     *
     * @param dbRuler 数据库的课堂规则实体，内含数据库课堂实体
     * @param schedule 课堂信息
     * @param ruler 课堂规则
     */
    public void objCopy(ScheduleRollCall dbRuler, ScheduleRedisDomain schedule, ScheduleRollCallRedisDomain ruler) {
        if (null != dbRuler.getSchedule()) {
            ruler.setCourseId(dbRuler.getSchedule().getCourseId());
            ruler.setScheduleId(schedule.getScheduleId());
            schedule.setClassRoomName(dbRuler.getSchedule().getClassRoomName());
            schedule.setCourseId(dbRuler.getSchedule().getCourseId());
            schedule.setCourseName(dbRuler.getSchedule().getCourseName());
            schedule.setDayOfWeek(dbRuler.getSchedule().getDayOfWeek());
            schedule.setOrgId(dbRuler.getSchedule().getOrganId());
            schedule.setPeriodId(dbRuler.getSchedule().getPeriodId());
            schedule.setPeriodNo(dbRuler.getSchedule().getPeriodNo());
            schedule.setPeriodNum(dbRuler.getSchedule().getPeriodNum());
            schedule.setScheduleId(dbRuler.getSchedule().getId());
            schedule.setScheduleEndTime(dbRuler.getSchedule().getScheduleEndTime());
            schedule.setScheduleStartTime(dbRuler.getSchedule().getScheduleStartTime());
            schedule.setSemesterId(dbRuler.getSchedule().getSemesterId());
            schedule.setSemesterName(dbRuler.getSchedule().getSemesterName());
            schedule.setTeachingclassId(dbRuler.getSchedule().getTeachingclassId());
            schedule.setTeachingclassCode(dbRuler.getSchedule().getTeachingclassCode());
            schedule.setTeachingclassName(dbRuler.getSchedule().getTeachingclassName());
            schedule.setTeachDate(dbRuler.getSchedule().getTeachDate());
            schedule.setTeacherId(dbRuler.getSchedule().getTeacherId());
            schedule.setTeacherName(dbRuler.getSchedule().getTeacherNname());
            schedule.setWeekId(dbRuler.getSchedule().getWeekId());
            schedule.setWeekName(dbRuler.getSchedule().getWeekName());
        }
        if (!StringUtils.isEmpty(schedule.getWeekName())) {
            schedule.setWeekNo(new Integer(schedule.getWeekName()));
        }
        if (!StringUtils.isEmpty(schedule.getTeachDate())) {
            if (!StringUtils.isEmpty(schedule.getScheduleStartTime())) {
                schedule.setStartDate(DateUtil.parseMinute(schedule.getTeachDate() + " " + schedule.getScheduleStartTime()));
            }
            if (!StringUtils.isEmpty(schedule.getScheduleEndTime())) {
                schedule.setEndDate(DateUtil.parseMinute(schedule.getTeachDate() + " " + schedule.getScheduleEndTime()));
            }
        }
        schedule.setScheduleRollCallId(dbRuler.getId());

        ruler.setCourseName(schedule.getCourseName());
        ruler.setTeachingclassName(schedule.getTeachingclassName());

        ruler.setId(dbRuler.getId());
        ruler.setIsOpenRollcall(dbRuler.getIsOpenRollcall());
        ruler.setRollCallType(dbRuler.getRollCallType());
        ruler.setLocaltion(dbRuler.getLocaltion());
        ruler.setCourseLaterTime(dbRuler.getCourseLaterTime());
        ruler.setClassroomRollCall(dbRuler.getClassRoomRollCall());
        ruler.setIsInClassroom(CourseRollCallConstants.COURSE_BEFORE);
    }

    /**
     * 批量修改考勤
     *
     * @param orgId
     * @param ruleId
     * @param studentIds
     */
    @Transactional
    public void updateRollCall(Long orgId, Long ruleId, Set<String> studentIds, String type) {
        List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(orgId, ruleId, studentIds);
        if (null == studentRollcallList || studentRollcallList.isEmpty()) {
            LOG.warn("退出课堂ID({})时，根据学生ID列表，没有找到学生签到列表数据", ruleId);
            return;
        }
        Map<String, RollcallRedisDomain> studentRollcallMap = new HashMap<>();
        for (RollcallRedisDomain domain : studentRollcallList) {
            domain.setLastType(domain.getType());
            domain.setType(type);
            domain.setCanRollCall(Boolean.FALSE);
            studentRollcallMap.put(domain.getStudentId().toString(), domain);
        }
        redisDataService.cacheScheduleRollcall(orgId, ruleId, studentRollcallMap);
    }

    /**
     * 获取到课率
     *
     * @param orgId
     * @param scheduleRollCallId
     * @return
     */
    public String getCurrentScheduleRollCallAttendance(Long orgId, Long scheduleRollCallId) {
        Set<Long> ss = redisDataService.getScheduleStudent(orgId, scheduleRollCallId);// 获取课堂的学生数据
        if (null != ss && !ss.isEmpty()) {
            Set<String> studentIds = new HashSet<>();
            for (Long sid : ss) {
                studentIds.add(sid.toString());
            }
            List<RollcallRedisDomain> studentRollcallList = redisDataService.getScheduleRollcall(orgId, scheduleRollCallId, studentIds);
            if (null == studentRollcallList || studentRollcallList.isEmpty()) {
                LOG.warn("退出课堂ID({})时，根据学生ID列表，没有找到学生签到列表数据", scheduleRollCallId);
                return null;
            }

            // 计算到课率
            return rollCallService.calculateAttendanceRollCall(studentRollcallList, orgId);
        }
        return null;
    }
}
