package com.aizhixin.cloud.rollcall.service;

import com.aizhixin.cloud.rollcall.common.core.DataValidity;
import com.aizhixin.cloud.rollcall.common.domain.IdNameDomain;
import com.aizhixin.cloud.rollcall.common.util.DateUtil;
import com.aizhixin.cloud.rollcall.domain.DianDianSchoolTimeDomain;
import com.aizhixin.cloud.rollcall.domain.PeriodDomain;
import com.aizhixin.cloud.rollcall.domain.SemesterDomain;
import com.aizhixin.cloud.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.rollcall.entity.Schedule;
import com.aizhixin.cloud.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.rollcall.entity.StudentLeaveSchedule;
import com.aizhixin.cloud.rollcall.monitor.service.PushMonitor;
import com.aizhixin.cloud.rollcall.remote.OrgManagerService;
import com.aizhixin.cloud.rollcall.repository.StudentLeaveScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class RollcallDayTaskPreprocessService {
    private final static Logger LOG = LoggerFactory.getLogger(RollcallDayTaskPreprocessService.class);
    @Autowired
    private OrgManagerService orgManagerService;
    @Autowired
    private CourseRollCallService courseRollCallService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private RedisDataService redisDataService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private MyCacheService myCacheService;

    @Autowired
    private PushMonitor pushMonitor;

    @Autowired
    private StudentLeaveScheduleRepository studentLeaveScheduleRepository;

    /**
     * 保存课堂及课堂规则信息
     * 
     * @param scheduleList
     * @param scheduleRollCallList
     * @return
     */
    private List<ScheduleRollCall> saveScheduleAndRuler(List<Schedule> scheduleList, List<ScheduleRollCall> scheduleRollCallList) {
        // 保存当天该学校的课堂
        if (scheduleList != null && !scheduleList.isEmpty()) {
            scheduleList = scheduleService.save(scheduleList);
            Date d = new Date();
            String str = formatDate(d);
            d = formatDate(str);
            for(Schedule item: scheduleList){
                updateStuLeave(d, item.getTeacherId(), item.getCourseId(), item.getPeriodId(), item.getId());
            }
        }
        if (scheduleRollCallList != null && !scheduleRollCallList.isEmpty()) {
            // 保存当天该学校的课堂对应的课堂规则、课堂设置信息
            return scheduleRollCallService.save(scheduleRollCallList);
        }
        return null;
    }

    private void updateStuLeave(Date d, Long teacherId, Long courseId, Long periodId, Long scheduleId){
        //学生请假更新排课id
        if (d != null) {
            List<StudentLeaveSchedule> leaveList = studentLeaveScheduleRepository.findByTeacherIdAndCourseIdAndRequesDateAndRequestPeriodIdAndDeleteFlag(teacherId, courseId, d, periodId, DataValidity.VALID.getState());
            if (leaveList != null && leaveList.size() > 0) {
                for (StudentLeaveSchedule item : leaveList) {
                    item.setScheduleId(scheduleId);
                }
                studentLeaveScheduleRepository.save(leaveList);
            }
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return f.format(date);
    }

    private Date formatDate(String str) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        try {
            return f.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化学校对应的课表数据d
     * 
     * @param school 学校
     * @param ddList ORG课表
     * @param periodDomainMap 课程节及详细信息
     * @param curDayString 时间字符串
     */
    // @Transactional
    private void initSchoolDay(IdNameDomain school, List<DianDianSchoolTimeDomain> ddList, Map<Integer, PeriodDomain> periodDomainMap, String curDayString) {
        List<Schedule> scheduleList = new ArrayList<>();// 当天课表数据
        List<ScheduleRollCall> scheduleRollCallList = new ArrayList<>();// 对应课表的课堂设置规则
        for (DianDianSchoolTimeDomain sct : ddList) {// 课堂及课堂规则成对出现，没有开打卡机的不需要初始化学生
            try {
                long begin = System.currentTimeMillis();
                Schedule schedule = scheduleService.createSchedule(sct, school.getId(), curDayString, periodDomainMap);
                scheduleList.add(schedule);

                CourseRollCall courseRollCall = courseRollCallService.get(schedule.getCourseId(), schedule.getTeacherId());// 老师课程设置规则
                ScheduleRollCall scheduleRollCall = new ScheduleRollCall();// 老师课堂设置规则

                scheduleRollCallService.initRuler(scheduleRollCall, schedule, courseRollCall);

                scheduleRollCallList.add(scheduleRollCall);

                // 添加监控信息
                pushMonitor.pushMonitor(schedule, (System.currentTimeMillis() - begin), Boolean.TRUE, null);
            } catch (Exception e) {
                LOG.warn("学校({})初始当天课表及课堂规则失败。({})", school.getName(), sct.getCourseId());
                LOG.warn("学校({})初始当天课表及课堂规则失败。({})", school.getName(), e);
            }
        }
        // List<ScheduleRollCall> scheduleRollCalls =
        saveScheduleAndRuler(scheduleList, scheduleRollCallList);
        myCacheService.initSchoolScheduleAndRuler(school.getId(), scheduleRollCallList);
        LOG.info("学校({})初始当天课表及课堂规则完成，成功初始化({})条数据", school.getName(), scheduleList.size());
    }

    /**
     * 学校当天课表初始化
     */
    public void schoolDayPreprocessTask() {
        List<IdNameDomain> schools = orgManagerService.findAllOrg();
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        if (null != schools && schools.size() > 0) {
            Set<Long> orgIds = new HashSet<>();
            for (IdNameDomain school : schools) {
                try {
                    LOG.info("开始初始化学校：（{}）,（{}）的数据", school.getName(), school.getId());
                    // 如果需要支持手动初始化，这儿进行当天该学校课表及相关数据的删除操作
                    SemesterDomain schoolSemester = orgManagerService.getorgsemester(school.getId(), curDayString);
                    if (null == schoolSemester || null == schoolSemester.getId() || schoolSemester.getId() <= 0) {
                        LOG.warn("学校：（{}），没有对应的学期数据，不能进行初始化操作", school.getName());
                        continue;
                    }
                    List<DianDianSchoolTimeDomain> ddList = orgManagerService.findSchoolTimeDay(school.getId(), schoolSemester.getId(), curDayString);
                    if (null == ddList || ddList.isEmpty()) {
                        LOG.warn("学校：（{}），这个时间没有课表数据", school.getName());
                        continue;
                    }
                    List<PeriodDomain> periodDomainList = periodService.listPeriod(school.getId());
                    if (null == periodDomainList || periodDomainList.isEmpty()) {
                        LOG.warn("学校：（{}），这个时间没有课程节数据", school.getName());
                        continue;
                    }
                    Map<Integer, PeriodDomain> periodDomainMap = new HashMap<>();
                    for (PeriodDomain p : periodDomainList) {
                        periodDomainMap.put(p.getNo(), p);
                    }
                    // 学校课表数据初始化
                    initSchoolDay(school, ddList, periodDomainMap, curDayString);
                    orgIds.add(school.getId());
                } catch (Exception e) {
                    LOG.warn("初始化学校数据异常({})", school.getId());
                    LOG.warn("初始化学校数据异常:{}", e);
                }
            }
            redisDataService.cacheCurrentdayOrgs(orgIds);
        }
    }

    /**
     * 给定学校从现有数据库课表和前端规则表中初始化Redis缓存
     * 
     * @param orgIds
     */
    @Transactional(readOnly = true)
    public void initRedisCurentDay(Set<Long> orgIds) {
        if (null != orgIds && !orgIds.isEmpty()) {
            redisDataService.cacheCurrentdayOrgs(orgIds);

            Date current = new Date();
            String curDayString = DateUtil.format(current);
            for (Long orgId : orgIds) {
                List<ScheduleRollCall> scheduleRollCallList = scheduleRollCallService.findTeachDateAndOrgId(curDayString, orgId);
                LOG.info("开始初始化天粒度学校：（{}）,当天从数据库总查询的（{}）条数据", orgId, scheduleRollCallList.size());
                try {
                    myCacheService.initSchoolScheduleAndRuler(orgId, scheduleRollCallList);
                } catch (Exception e) {
                    LOG.warn("初始化天粒度失败:orgId({})：{}", orgId, e);
                }
            }
        }
    }

    /**
     * 初始化当天所有课表数据的学校课表到Redis缓存
     */
    @Transactional(readOnly = true)
    public void initRedisCurentDayAllOrg() {
        Date current = new Date();
        String curDayString = DateUtil.format(current);
        List<Long> orgIds = scheduleService.getDayOrgIds(curDayString);
        if (null != orgIds && !orgIds.isEmpty()) {
            Set<Long> ods = new HashSet<>();
            ods.addAll(orgIds);
            initRedisCurentDay(ods);
        }
    }
}
