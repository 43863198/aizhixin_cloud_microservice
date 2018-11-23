package com.aizhixin.cloud.dd.rollcallv2.service;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.RollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallClassDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.repository.RollCallRepository;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcall.utils.RedisUtil;
import com.aizhixin.cloud.dd.rollcallv2.domain.ScheduleRollCallRedisDomain;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author LIMH
 * @date 2017/12/26
 */
@Service
public class RollcallServiceV5 {
    private final static Logger log = LoggerFactory.getLogger(RollcallServiceV5.class);

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
//    @Autowired
//    private RollCallRemoteClient rollCallRemoteClient;

    @Autowired
    private RollCallRepository rollCallRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ModifyAttendanceLogService modifyAttendanceLogService;

    @Autowired
    private StudentLeaveScheduleService studentLeaveScheduleService;

    @Autowired
    private RollCallService rollCallService;

    /**
     * 获取老师的点名信息
     *
     * @param scheduleId
     * @param type
     * @param name
     * @param isSchoolTime
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<RollCallClassDTO> getRollCall(Long orgId, Long scheduleId, String type, String name, boolean isSchoolTime) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        if (null == schedule) {
            return null;
        }

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);

        if (null == scheduleRollCall) {
            return null;
        }
        boolean inClass = (DateFormatUtil.getNow(DateFormatUtil.FORMAT_SHORT).equals(schedule.getTeachDate()) && CourseUtils.classBeginTime(schedule.getScheduleStartTime())
                && CourseUtils.classEndTime(schedule.getScheduleEndTime()));

        if (inClass) {
            ScheduleRollCallRedisDomain scheduleRollCallRedisDomain = null;//rollCallRemoteClient.getScheduleRollCall(orgId, scheduleId);
            if (scheduleRollCall != null) {
                scheduleRollCall.setRollCallType(scheduleRollCallRedisDomain.getRollCallType());
                scheduleRollCall.setLocaltion(scheduleRollCallRedisDomain.getLocaltion());
                scheduleRollCall.setAttendance(scheduleRollCallRedisDomain.getAttendance());
                scheduleRollCall.setCourseLaterTime(scheduleRollCallRedisDomain.getCourseLaterTime());
            }
        }

        String authCode = "";
        if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(scheduleRollCall.getRollCallType()) && StringUtils.isBlank(scheduleRollCall.getLocaltion())) {
            authCode = String.valueOf(RollCallService.getRandomAuthCode());
            scheduleRollCall.setLocaltion(authCode);
            scheduleRollCallService.save(scheduleRollCall, scheduleId);
//            rollCallRemoteClient.upadteRuler(schedule.getOrganId(), schedule.getTeacherId(), schedule.getCourseId(), scheduleRollCall.getRollCallType(), scheduleRollCall.getCourseLaterTime(), CourseRollCallConstants.OPEN_ROLLCALL, authCode);

        } else {
            authCode = scheduleRollCall.getLocaltion() == null ? "" : scheduleRollCall.getLocaltion();
        }

        List<RollCall> rollCallList = null;
        // 课堂内的签到数据在redis库中查询
        if (inClass) {
//            rollCallList = rollCallRemoteClient.listRollCall(orgId, scheduleId);
        } else {
            rollCallList = rollCallRepository.findByScheduleRollcallId(scheduleRollCall.getId());
        }

        if (null == rollCallList) {
            return null;
        }
        if (StringUtils.isNotBlank(type)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return rollCall.getType().equals(type) ? true : false;
            }));
        }

        if (StringUtils.isNotBlank(name)) {
            rollCallList = Lists.newArrayList(Collections2.filter(rollCallList, (rollCall) -> {
                return null == rollCall.getStudentName() ? false : rollCall.getStudentName().contains(name) ? true : false;
            }));
        }

        if (null != rollCallList && rollCallList.size() > 0) {

            Collections.sort(rollCallList, new Comparator<RollCall>() {
                @Override
                public int compare(RollCall o1, RollCall o2) {
                    if (o1.getStudentNum() == null) {
                        o1.setStudentNum("");
                    }
                    return o1.getStudentNum().compareTo(o2.getStudentName());
                    // return o1.getStudentId() - o2.getStudentId() > 0 ? 1 : -1;
                }
            });
        }

        int clasR = (null == scheduleRollCall.getClassRoomRollCall() ? 10 : scheduleRollCall.getClassRoomRollCall());
        boolean isClassroomRollCall = false;
        if (ScheduleConstants.TYPE_OPEN_CLASSROOMROLLCALL == clasR) {
            isClassroomRollCall = true;
        }

        String ids = "";
        Map<Long, RollCallClassDTO> map = new TreeMap<Long, RollCallClassDTO>();

        List<Long> slss = studentLeaveScheduleService.findStudentIdByScheduleId(schedule);
        for (RollCall rollCall : rollCallList) {
            if (!RollCallConstants.TYPE_CANCEL_ROLLCALL.equals(rollCall.getType()) && slss != null && slss.contains(rollCall.getStudentId())) {
                String tempType = rollCall.getType();
                rollCall.setLastType(tempType);
                rollCall.setType(RollCallConstants.TYPE_ASK_FOR_LEAVE);
                if (!RollCallConstants.TYPE_ASK_FOR_LEAVE.equals(tempType)) {
                    if (inClass) {
                        redisTemplate.opsForHash().put(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCall.getStudentId(), rollCall);
                    } else {
                        rollCallRepository.save(rollCall);
                    }
                }
            }

            RollCallDTO rollCallDTO = new RollCallDTO();
            // 组装数据
            rollCallDTO.setId(rollCall.getId());
            rollCallDTO.setScheduleId(rollCall.getScheduleRollcallId());
            rollCallDTO.setClassId(rollCall.getClassId());
            rollCallDTO.setUserId(rollCall.getStudentId());
            rollCallDTO.setType(rollCall.getType());
            rollCallDTO.setDistance(null == rollCall.getDistance() ? "" : rollCall.getDistance());
            rollCallDTO.setSignTime(DateFormatUtil.format(rollCall.getSignTime(), DateFormatUtil.FORMAT_MINUTE));
            rollCallDTO.setUserName(rollCall.getStudentName());
            String className = rollCall.getClassName(); // "23";
            rollCallDTO.setClassName(className);
            rollCallDTO.setRollCall(scheduleRollCall.getIsOpenRollcall());
            rollCallDTO.setTeacherId(schedule.getTeacherId());
            rollCallDTO.setCourseId(schedule.getCourseId());

            if (null != map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId())) {
                RollCallClassDTO classDTO = (RollCallClassDTO) map.get(rollCall.getClassId() == null ? 0 : rollCall.getClassId());
                ArrayList<RollCallDTO> ls = classDTO.getRollCallList();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setClassSize(classDTO.getClassSize() + 1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
            } else {
                RollCallClassDTO classDTO = new RollCallClassDTO();
                classDTO.setClassName(className);
                ArrayList<RollCallDTO> ls = new ArrayList<RollCallDTO>();
                ls.add(rollCallDTO);
                if (StringUtils.isNotBlank(ids)) {
                    ids += "," + rollCallDTO.getUserId().toString();
                } else {
                    ids += rollCallDTO.getUserId().toString();
                }
                classDTO.setRollCallList(ls);
                classDTO.setClassSize(1);
                classDTO.setAuthCode(authCode);
                classDTO.setClassroomRollcall(isClassroomRollCall);
                map.put(rollCallDTO.getClassId() == null ? 0 : rollCallDTO.getClassId(), classDTO);
            }
        }

        if (StringUtils.isNotBlank(ids)) {
            List<RollCallClassDTO> rsList = new ArrayList<RollCallClassDTO>();
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                RollCallClassDTO classDTO = (RollCallClassDTO) entry.getValue();
                rsList.add(classDTO);
            }
            return rsList;
        }
        return new ArrayList();
    }

    public Map openClassrommRollcall(Long orgId, Long scheduleId, String rollcallType) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1.修改排课表，修改其标志为 开启随堂点
            Schedule schedule = scheduleService.findOne(scheduleId);
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
            if (null == scheduleRollCall) {
                scheduleRollCall = new ScheduleRollCall();
                scheduleRollCall.setSchedule(schedule);
            }
            if (null == scheduleRollCall) {
                throw new NullPointerException();
            }
            String authCode = "";
            if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(rollcallType)) {
                authCode = String.valueOf(RollCallService.getRandomAuthCode());
            }
            scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.OPEN_CLASSROOMROLLCALL);
            scheduleRollCall.setRollCallType(rollcallType);
            scheduleRollCall.setLocaltion(authCode);
            scheduleRollCall.setCourseLaterTime(0);
            scheduleRollCall.setAbsenteeismTime(0);
            scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);

            scheduleRollCallService.save(scheduleRollCall, scheduleId);

//            rollCallRemoteClient.open(orgId, scheduleId, scheduleRollCall.getId(), rollcallType, authCode);

        } catch (Exception e) {
            log.warn("Exception", e);
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "开启随堂点失败!");
        }
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return result;

    }

    public boolean closeClassrommRollcall(Long orgId, Long scheduleId) {
        try {
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
            if (null == scheduleRollCall) {
                return false;
            }
            // 1.修改排课表，修改其标志为 关闭随堂点
            scheduleRollCall.setClassRoomRollCall(CourseRollCallConstants.CLOSED_CLASSROOMROLLCALL);
            scheduleRollCallService.save(scheduleRollCall, scheduleId);

            // 通知rollcall服务
//            rollCallRemoteClient.close(orgId, scheduleRollCall.getId());
            List<RollCall> rollCallList = listRollCallBySRCIdInRedis(scheduleRollCall.getId());

            // 2.将未提交的学生状态改为旷课。
            Map<Long, RollCall> rollCallMap = new HashMap();
            for (RollCall rollCall : rollCallList) {
                String type = rollCall.getType();
                rollCall.setLastType(type);
                if (RollCallConstants.TYPE_UNCOMMITTED.equals(type)) {
                    rollCall.setType(RollCallConstants.TYPE_TRUANCY);
                } else if (RollCallConstants.TYPE_COMMITTED.equals(type)) {
                    rollCall.setType(RollCallConstants.TYPE_NORMA);
                }
                rollCall.setCanRollCall(Boolean.FALSE);
                rollCallMap.put(rollCall.getStudentId(), rollCall);
            }
            redisTemplate.opsForHash().putAll(RedisUtil.getScheduleRollCallKey(scheduleRollCall.getId()), rollCallMap);
        } catch (Exception e) {
            log.warn("Exception", e);
            return false;
        }
        return true;
    }

    public List<RollCall> listRollCallBySRCIdInRedis(Long scheduleRollCallId) {
        return redisTemplate.opsForHash().values(RedisUtil.getScheduleRollCallKey(scheduleRollCallId));
    }

    /**
     * 学生点名结果修改
     *
     * @param rollcallDTO
     */
    public void updateRollCallResult(Long orgId, RollCallDTO rollcallDTO) {

        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findOne(rollcallDTO.getScheduleId());
        Schedule schedule = scheduleService.findOne(scheduleRollCall.getSchedule().getId());
        modifyAttendanceLogService.modifyAttendance(rollcallDTO.getId(), rollcallDTO.getType(), schedule.getTeacherNname(), schedule.getTeacherId());
        RollCall rollCall = null;

        if (DateFormatUtil.formatShort(new Date()).equals(schedule.getTeachDate())) {
//            rollCallRemoteClient.updateRollcall(orgId, scheduleRollCall.getId(), Sets.newHashSet(rollcallDTO.getUserId().toString()), rollcallDTO.getType());
            if (scheduleRollCall.getIsInClassroom()) {
                return;
            }
        }
        // 课堂外修改
        rollCall = rollCallRepository.findOne(rollcallDTO.getId());
        rollCall.setType(rollcallDTO.getType());
        rollCall.setCanRollCall(Boolean.FALSE);
        rollCallRepository.save(rollCall);

    }

    /**
     * 学生状态批量修改
     *
     * @param orgId
     * @param userId
     * @param studentIds
     * @param type
     */
    public void updateRollCallResult(Long orgId, Long userId, Long scheduleId, Set<Long> studentIds, String type) {
        Schedule schedule = scheduleService.findOne(scheduleId);
        // ScheduleRollCall scheduleRollCall = scheduleRollCallService.findOne(schedule);
        ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
        long scheduleRollcallId = scheduleRollCall.getId();
        if (DateFormatUtil.formatShort(new Date()).equals(schedule.getTeachDate())) {
            Set<String> stuIds = new HashSet<>();
            for (Long studentId : studentIds) {
                stuIds.add(String.valueOf(studentId));
            }
//            rollCallRemoteClient.updateRollcall(orgId, scheduleRollcallId, stuIds, type);
            if (scheduleRollCall.getIsInClassroom()) {
                return;
            }
        }
        // 课堂外修改
        List<RollCall> rollCalls = rollCallRepository.findAllByScheduleRollcallIdAndStudentIdIn(scheduleRollcallId, studentIds);
        if (rollCalls == null || rollCalls.isEmpty()) {
            return;
        }
        for (RollCall rollCall : rollCalls) {
            rollCall.setLastType(rollCall.getType());
            rollCall.setType(type);
        }
        rollCallRepository.save(rollCalls);
    }

}
