package com.aizhixin.cloud.dd.rollcallv2.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.remote.RollCallRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallClassDTO;
import com.aizhixin.cloud.dd.rollcall.dto.RollCallDTO;
import com.aizhixin.cloud.dd.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.service.CourseRollCallService;
import com.aizhixin.cloud.dd.rollcall.service.ScheduleRollCallService;
import com.aizhixin.cloud.dd.rollcall.service.ScheduleService;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import com.aizhixin.cloud.dd.rollcallv2.service.LeaveServiceV5;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcallv2.service.RollcallServiceV5;

import io.swagger.annotations.Api;

import java.net.URISyntaxException;
import java.util.*;

import static com.aizhixin.cloud.dd.rollcall.service.RollCallService.getRandomAuthCode;

/**
 * @author LIMH
 * @date 2017/12/26
 */
@RestController
@RequestMapping("/api/phone/v1/new")
@Api(value = "手机教师端API", description = "针对手机教师端的相关API")

public class TeacherPhoneResourceV5 {
    private final Logger log = LoggerFactory.getLogger(TeacherPhoneResourceV5.class);

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private RollcallServiceV5 rollcallServiceV5;

    @Autowired
    private LeaveServiceV5 leaveServiceV5;

    @Autowired
    private CourseRollCallService courseRollCallService;

    @Autowired
    private RollCallRemoteClient rollCallRemoteClient;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    /**
     * 打卡机点名信息查询
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/rollcall/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师课程点名信息查询", response = Void.class, notes = "查询教师课程点名信息<br>@author 李美华")
    public ResponseEntity<?> getRollCall(@ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
                                         @ApiParam(value = "name 名称 (模糊查询)") @RequestParam(value = "name", required = false) String name,
                                         @ApiParam(value = "isSchoolTime 是否上课时间") @RequestParam(value = "isSchoolTime", required = true) boolean isSchoolTime,
                                         @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = false) String type,
                                         @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<RollCallClassDTO> rcList = rollcallServiceV5.getRollCall(account.getOrganId(), schedule_id, type, name, isSchoolTime);

        PageInfo page = new PageInfo();
        page.setData(rcList);
        page.setOffset(1);
        page.setLimit(Integer.MAX_VALUE);
        page.setTotalCount(null == rcList ? 0L : rcList.size());
        page.setPageCount(1);
        page.setLimit(0);

        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    /**
     * 开启随堂点
     *
     * @param scheduleId
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/rollcall/openClassrommRollcallV2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "开启随堂点", httpMethod = "POST", response = Void.class, notes = "开启随堂点<br>@author 李美华")
    public ResponseEntity<?> openClassrommRollcall(@ApiParam(value = "rollcallType 点名方式") @RequestParam(value = "rollcallType", required = true) String rollcallType,
                                                   @ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId, @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                rollcallServiceV5.openClassrommRollcall(account.getOrganId(), scheduleId,
                        ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollcallType) ? ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC : ScheduleConstants.TYPE_ROLL_CALL_DIGITAL),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/rollcall/closeClassrommRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "关闭随堂点", httpMethod = "POST", response = Void.class, notes = "关闭随堂点<br>@author 李美华")
    public ResponseEntity<?> closeClassrommRollcall(@ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId,
                                                    @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        try {
            rollcallServiceV5.closeClassrommRollcall(account.getOrganId(), scheduleId);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "关闭随堂点失败!");
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        }
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/updateRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改点名规则", httpMethod = "POST", response = Void.class, notes = "修改点名规则<br>@author 李美华")
    public ResponseEntity<?> updateRollcall(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "courseId 课程ID 必填", required = true) @RequestParam(value = "courseId", required = true) Long courseId,
                                            @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填", required = true) @RequestParam(value = "rollCallType", required = true) String rollCallType,
                                            @ApiParam(value = "lateTime 迟到时间 必填", required = true) @RequestParam(value = "lateTime", required = true) int lateTime,
                                            @ApiParam(value = "absenteeismTime 旷课时间") @RequestParam(value = "absenteeismTime", required = false) int absenteeismTime,
                                            @ApiParam(value = "reUser 是否重复是使用 [10:不重复运用,20:重复运用] 必填", required = true) @RequestParam(value = "reUser", required = true) int reUser,
                                            @ApiParam(value = "isOpen 是否开启[disable:关闭,enable:开启] 必填", required = true) @RequestParam(value = "isOpen", required = true) String isOpen) {

        Map<String, Object> resBody = new HashMap<>();

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        try {

            CourseRollCall rc = courseRollCallService.get(courseId, account.getId());

            // 第一次设置点名规则
            if (rc == null) {
                CourseRollCall courseRollCall = new CourseRollCall();
                courseRollCall.setCourseId(courseId);
                courseRollCall.setTeacherId(account.getId());
                courseRollCall.setDeleteFlag(DataValidity.VALID.getState());
                courseRollCall.setRollCallType(rollCallType);
                courseRollCall.setLateTime(lateTime);
                courseRollCall.setAbsenteeismTime(absenteeismTime);
                courseRollCall.setIsOpen(isOpen);
                courseRollCallService.saveRollCall(courseRollCall);
            } else {
                // 修改点名规则
                courseRollCallService.updateRollCall(courseId, account.getId(), rollCallType, lateTime, absenteeismTime, isOpen);
            }
            String authCode = "";
            // 修改点名规则，需要判断是否在课中修改，课中修改清除该节课的点名记录，学生需要重新签到。
            // 根据课程id，教师ID,教学时间
            Date startDate = null;
            long time = System.currentTimeMillis();
            if (CourseRollCallConstants.OPEN_ROLLCALL.equals(isOpen)) {
                List<Schedule> curSchedules
                        = scheduleService.listScheduleByTeacherIdAndCourseIdAndTeachDate(account.getId(), courseId, DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT));
                System.out.println("修改打卡机...,获取...");
                for (Schedule schedule : curSchedules) {
                    ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(schedule);
                    if (null == scheduleRollCall) {
                        continue;
                    }
                    scheduleRollCall.setCourseLaterTime(lateTime);
                    scheduleRollCall.setAbsenteeismTime(absenteeismTime);
                    if (CourseUtils.isSchoolTime(schedule.getScheduleStartTime(), schedule.getScheduleEndTime())) {
                        if (ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(rollCallType)) {
                            authCode = String.valueOf(getRandomAuthCode());
                        }
                        scheduleRollCall.setLocaltion(authCode);
                        scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);
                        scheduleRollCallService.save(scheduleRollCall, schedule.getId());
                    }

                    try {
                        startDate = DateFormatUtil.parse(schedule.getTeachDate() + " " + schedule.getScheduleStartTime(), DateFormatUtil.FORMAT_MINUTE);
                        if (time < startDate.getTime()) {
                            scheduleRollCall.setIsOpenRollcall(Boolean.TRUE);
                            scheduleRollCallService.save(scheduleRollCall, schedule.getId());
                        }
                    } catch (Exception e) {

                    }
                }
            }
            rollCallRemoteClient.upadteRuler(account.getOrganId(), account.getId(), courseId, rollCallType, lateTime, isOpen, authCode);
            result.put(ApiReturnConstants.SUCCESS, true);
        } catch (Exception e) {
            result.put(ApiReturnConstants.SUCCESS, false);
            result.put(ApiReturnConstants.MESSAGE, "修改点名设置异常," + e);
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 通过请假
     *
     * @param leaveId
     * @param accessToken
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/teacher/passleaverequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "通过请假", response = Void.class, notes = "通过请假<br>@author 杨立强")
    public ResponseEntity<?> passLeaveRequest(@ApiParam(value = "leaveId 请假申请id") @RequestParam(value = "leaveId", required = true) Long leaveId,
                                              @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = null;
        try {
            resBody = leaveServiceV5.passLeaveRequest(leaveId, account, accessToken);
        } catch (Exception e) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "error");
            return new ResponseEntity<Object>(resBody, HttpStatus.OK);
        }
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 批量修改学生签到状态
     *
     * @return
     */
    @RequestMapping(value = "/teacher/rollcall/updateRollCallMore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改部分/全部 学生考勤", httpMethod = "POST", response = Void.class, notes = "修改部分/全部 学生考勤<br>@author 李美华")
    public ResponseEntity<?> updateRollCallMore(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "课堂id") @RequestParam(value = "scheduleRollcallId", required = true) Long scheduleRollcallId,
                                                @ApiParam(value = "学生id") @RequestParam(value = "studentIds", required = true) Set<Long> studentIds,
                                                @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = true) String type) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();

        try {

            rollcallServiceV5.updateRollCallResult(account.getOrganId(), account.getId(), scheduleRollcallId, studentIds, type);
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 提交点名结果
     */
    @RequestMapping(value = "/teacher/rollcall/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "点名结果修改", httpMethod = "POST", response = Void.class, notes = "修改点名结果<br>@author 李美华")
    public ResponseEntity<?> updateRollCallResult(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "id 点名id 必填") @RequestBody RollCallDTO rollcallDTO) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();

        try {
            rollcallServiceV5.updateRollCallResult(account.getOrganId(), rollcallDTO);
            result.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", Boolean.FALSE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
}
