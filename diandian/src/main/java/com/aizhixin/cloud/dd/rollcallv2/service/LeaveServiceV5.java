package com.aizhixin.cloud.dd.rollcallv2.service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.messege.dto.AudienceDTO;
import com.aizhixin.cloud.dd.messege.service.MessageService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;
import com.aizhixin.cloud.dd.messege.service.PushService;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.DianDianDaySchoolTimeTableDomain;
import com.aizhixin.cloud.dd.rollcall.entity.Leave;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.*;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * @author LIMH
 * @date 2018/1/18
 */
@Service
@Transactional
public class LeaveServiceV5 {
    private final Logger log = LoggerFactory.getLogger(LeaveServiceV5.class);

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @Autowired
    private PushService pushService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private RollCallService rollCallService;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private RollCallRemoteClient rollCallRemoteClient;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private MessageService messageService;

    /**
     * 批准学生请假
     *
     * @param leaveId
     * @param account
     * @param accessToken
     * @return
     */
    public Object passLeaveRequest(Long leaveId, AccountDTO account, String accessToken) throws Exception {

        try {

            Leave leave = leaveRepository.findOne(leaveId);
            if (leave == null) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "leave request not exist!");
                return res;
            }
            if (!leave.getHeadTeacherId().equals(account.getId())) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "error creator!");
                return res;
            }
            if (!leave.getStatus().equals(LeaveConstants.STATUS_REQUEST)) {
                Map<String, Object> res = new HashMap<>();
                res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                res.put(ApiReturnConstants.MESSAGE, "this request is " + leave.getStatus());
                return res;
            }

            List<Leave> leavList = leaveRepository.findAllByStudentIdAndStartDateAndEndDateAndStartPeriodIdAndEndPeriodIdAndRequestContentAndRequestType(leave.getStudentId(),
                    leave.getStartDate(), leave.getEndDate(), leave.getStartPeriodId(), leave.getEndPeriodId(), leave.getRequestContent(), leave.getRequestType());
            if (leavList != null && leavList.size() > 0) {
                for (Leave leave1 : leavList) {
                    leave1.setStatus(LeaveConstants.STATUS_PASS);
                    if (leave1.getHeadTeacherId().longValue() != account.getId().longValue()) {
                        leave1.setDeleteFlag(DataValidity.INVALID.getState());
                    }
                }
            } else {
                leave.setStatus(LeaveConstants.STATUS_PASS);
                leaveRepository.save(leave);
            }
            /**
             * 处理学生排课请假信息
             */
            leaveRepository.save(leavList);

            List<Long> ids = new ArrayList<Long>();
            if (leave.getRequestType().equals(LeaveConstants.TYPE_DAY)) {

                String beginTeachTime = DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT);
                String endTeachTime = DateFormatUtil.format(leave.getEndDate(), DateFormatUtil.FORMAT_SHORT);
                List<Date> manyDate = DateFormatUtil.getMonthBetweenDate(leave.getStartDate(), leave.getEndDate());

                for (Date schoolDay : manyDate) {
                    if (!DateFormatUtil.compareDate(schoolDay, DateFormatUtil.parse((DateFormatUtil.formatShort(new Date()) + " 23:59:59"), DateFormatUtil.FORMAT_LONG))) {
                        // 从平台获取排课
                        // 根据日期从平台获取排课数据
                        List<DianDianDaySchoolTimeTableDomain> ddt
                                = orgManagerRemoteService.getStudentDaySchoolTimeTable(leave.getStudentId(), DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT));
                        for (DianDianDaySchoolTimeTableDomain dto : ddt) {
                            Long tempTeacherId = leaveService.send(leave.getStudentId(), InitScheduleService.parseTeacher(dto.getTeachers()).getId(), dto.getCourseId(),
                                    dto.getTeachDate(), dto.getPeriodId(), null, leave, account.getId());
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    } else {

                        // 直接修改本地数据库排课信息
                        Set<Long> teachingclassIds
                                = orgManagerRemoteService.getStudentDayTeachingClassId(leave.getStudentId(), DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT));
                        // 获取当天该学生的排课信息
                        List<Schedule> scheduleList = scheduleRepository.findByTeachDateAndDeleteFlagAndTeachingclassIdIn(
                                DateFormatUtil.format(schoolDay, DateFormatUtil.FORMAT_SHORT), DataValidity.VALID.getState(), teachingclassIds);

                        // 获取该学生的某一天的排课，修改其签到记录。 对于未点名的课程，需要实时获取请假列表查看。
                        Set<Long> scheduleRollCallIds = new HashSet();
                        for (Schedule schedule : scheduleList) {
                            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule.getId());
                            if (null != scheduleRollCall && scheduleRollCall.getIsOpenRollcall()) {
                                scheduleRollCallIds.add(scheduleRollCall.getId());
                                boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate())
                                        && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
                                if (inClass) {
                                    // 点点签到重构优化
//                                    rollCallRemoteClient.setStudentLeave(account.getOrganId(), schedule.getId(), leave.getStudentId());
                                    RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), leave.getStudentId());
                                    if (null != rollCall) {
                                        rollCall.setLastType(rollCall.getType());
                                        rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                                    }
                                }
                            }

                            Long tempTeacherId = null;
                            try {
                                tempTeacherId = leaveService.send(leave.getStudentId(), schedule.getTeacherId(), schedule.getCourseId(),
                                        DateFormatUtil.parse(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT), schedule.getPeriodId(), schedule, leave, account.getId());
                            } catch (ParseException e) {
                                log.warn("Exception", e);
                            }
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                        // 修改学生的签到状态
                        if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
                            rollCallService.updateRollCallByStudentIdAndScheduleRollCall(RollCallConstants.TYPE_ASK_FOR_LEAVE, leave.getStudentId(), scheduleRollCallIds);
                        }
                    }
                }
            } else if (leave.getRequestType().equals(LeaveConstants.TYPE_PERIOD)) {
                Map map = leaveService.getBetweenStartAndEndPeriodId(account.getOrganId(), leave.getStartPeriodId(), leave.getEndPeriodId());

                // 当天以后的请假
                if (!DateFormatUtil.compareDate(leave.getStartDate(), new Date())) {
                    // 根据日期从平台获取排课数据
                    List<DianDianDaySchoolTimeTableDomain> ddt
                            = orgManagerRemoteService.getStudentDaySchoolTimeTable(leave.getStudentId(), DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT));
                    for (DianDianDaySchoolTimeTableDomain dto : ddt) {
                        if (map.containsKey(dto.getPeriodId())) {
                            Long tempTeacherId = leaveService.send(leave.getStudentId(), InitScheduleService.parseTeacher(dto.getTeachers()).getId(), dto.getCourseId(),
                                    dto.getTeachDate(), dto.getPeriodId(), null, leave, account.getId());
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    }
                } else {
                    // 直接修改本地数据库排课信息
                    Set<Long> teachingclassIds
                            = orgManagerRemoteService.getStudentDayTeachingClassId(leave.getStudentId(), DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT));
                    // 获取当天该学生的排课信息
                    List<Schedule> scheduleList = scheduleRepository.findByTeachDateAndDeleteFlagAndTeachingclassIdIn(
                            DateFormatUtil.format(leave.getStartDate(), DateFormatUtil.FORMAT_SHORT), DataValidity.VALID.getState(), teachingclassIds);

                    // 获取该学生的某一天的排课，修改其签到记录。 对于未点名的课程，需要实时获取请假列表查看。
                    Set<Long> scheduleRollCallIds = new HashSet();
                    for (Schedule schedule : scheduleList) {
                        if (map.containsKey(schedule.getPeriodId())) {
                            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
                            if (null != scheduleRollCall && scheduleRollCall.getIsOpenRollcall()) {
                                scheduleRollCallIds.add(scheduleRollCall.getId());

                                boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate())
                                        && CourseUtils.classBeginTime(schedule.getScheduleStartTime()) && CourseUtils.classEndTime(schedule.getScheduleEndTime()));
                                if (inClass) {
//                                    rollCallRemoteClient.setStudentLeave(account.getOrganId(), schedule.getId(), leave.getStudentId());
                                    RollCall rollCall = (RollCall) redisTemplate.opsForHash().get(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), leave.getStudentId());
                                    if (null != rollCall) {
                                        rollCall.setLastType(rollCall.getType());
                                        rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                                        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                                    }
                                }
                            }
                            Long tempTeacherId = null;
                            try {
                                tempTeacherId = leaveService.send(leave.getStudentId(), schedule.getTeacherId(), schedule.getCourseId(),
                                        DateFormatUtil.parse(schedule.getTeachDate(), DateFormatUtil.FORMAT_SHORT), schedule.getPeriodId(), schedule, leave, account.getId());
                            } catch (ParseException e) {
                                log.warn("Exception", e);
                            }
                            if (null != tempTeacherId) {
                                ids.add(tempTeacherId);
                            }
                        }
                    }
                    // 修改学生的签到状态
                    if (null != scheduleRollCallIds && scheduleRollCallIds.size() > 0) {
                        rollCallService.updateRollCallByStudentIdAndScheduleRollCall(RollCallConstants.TYPE_ASK_FOR_LEAVE, leave.getStudentId(), scheduleRollCallIds);
                    }
                }
            }
            pushMessageService.createPushMessage("请假审批结果通知", "请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, PushMessageConstants.MODULE_LEAVE, "请假审批结果通知", leave.getStudentId());
            ids.add(leave.getStudentId());
            pushService.listPush(accessToken, "您有一条未读的请假审批结果通知", "请假审批", "请假审批", ids);
            //----新消息服务----start
            List<AudienceDTO> audiences = new ArrayList<>();
            AudienceDTO dto = new AudienceDTO();
            dto.setUserId(leave.getStudentId());
            dto.setData(leave);
            audiences.add(dto);
            messageService.push("请假审批结果通知", "请假审批结果通知", PushMessageConstants.FUNCTION_STUDENT_NOTICE, audiences);
            //----新消息服务----end
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        Map<String, Object> res = new HashMap<>();
        res.put(ApiReturnConstants.MESSAGE, ApiReturnConstants.SUCCESS);
        res.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return res;
    }
}
