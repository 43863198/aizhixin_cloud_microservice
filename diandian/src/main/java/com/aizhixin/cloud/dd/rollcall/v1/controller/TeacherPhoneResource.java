package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.DataValidity;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.IdNameCode;
import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.AssessConstants;
import com.aizhixin.cloud.dd.constant.CourseRollCallConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.entity.CourseRollCall;
import com.aizhixin.cloud.dd.rollcall.entity.Schedule;
import com.aizhixin.cloud.dd.rollcall.entity.ScheduleRollCall;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.serviceV2.RollCallServiceV2;
import com.aizhixin.cloud.dd.rollcall.utils.CourseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机教师端API", description = "针对手机教师端的相关API")

public class TeacherPhoneResource {

    private final Logger log = LoggerFactory.getLogger(TeacherPhoneResource.class);

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CourseRollCallService courseRollCallService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Lazy
    @Autowired
    private RollCallService rollCallService;
    @Autowired
    private RollCallServiceV2 rollCallServiceV2;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private AssessService assessService;
    @Autowired
    private InitScheduleService initScheduleService;
    @Autowired
    private CourseAssessService courseAssessService;
    @Autowired
    private SemesterService semesterService;
    @Lazy
    @Autowired
    private TempAdjustCourseScheduleService tempAdjustCourseScheduleService;
    @Autowired
    private TeachingClassesService teachingClassesService;

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/course/getList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师课程列表查询", response = Void.class, notes = "查询教师课程列表<br>@author meihua.li")
    public ResponseEntity<?> getCourseList(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
                                           @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
                                           @ApiParam(value = "teachTime 教学时间") @RequestParam(value = "teachTime", required = false) String teachTime) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        PageInfo page = scheduleRollCallService.getTeacherScheduleList(PageUtil.createNoErrorPageRequestAndSort(offset, limit, new Sort(new Sort.Order(Sort.Direction.ASC, "periodNo"))), account.getId(), account.getOrganId(), teachTime);
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    /**
     * 打卡机点名信息查询
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/rollcall/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师课程点名信息查询", response = Void.class, notes = "查询教师课程点名信息<br>@author 李美华")
    public ResponseEntity<?> getRollCall(
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @ApiParam(value = "name 名称 (模糊查询)") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "isSchoolTime 是否上课时间") @RequestParam(value = "isSchoolTime", required = true) boolean isSchoolTime,
            @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = false) String type,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<RollCallClassDTO> rcList = rollCallService.getRollCall(account.getId(), schedule_id, type, name);

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
     * 补录考勤
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/additionaleRollcall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "补录考勤", response = Void.class, notes = "补录考勤<br>@author 李美华")
    public ResponseEntity<?> getRollCallSchoolTime(
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = new HashMap<>();
        try {
            rollCallService.additionaleRollcall(schedule_id);
            result.put(ApiReturnConstants.MESSAGE, "补录考勤成功！");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.MESSAGE, "补录考勤失败！");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 随堂点点名信息查询
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/rollcall/getRollCallSchoolTime", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "随堂点点名信息查询", response = Void.class, notes = "随堂点点名信息查询<br>@author 李美华")
    public ResponseEntity<?> getRollCallSchoolTime(
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @ApiParam(value = "name 名称 (模糊查询)") @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "isSchoolTime 是否上课时间") @RequestParam(value = "isSchoolTime", required = true) boolean isSchoolTime,
            @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = false) String type,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<RollCallClassDTO> rcList = rollCallService.getRollCallNum(account.getId(), schedule_id, type, name);

        PageInfo page = new PageInfo();
        page.setData(rcList);
        page.setOffset(0);
        page.setLimit(Integer.MAX_VALUE);
        page.setTotalCount(1L);
        page.setPageCount(Integer.MAX_VALUE);

        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    /**
     * 获取课程详情
     *
     * @param schedule_id
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/teacher/course/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师课程详情信息查询", response = Void.class, notes = "查询教师课程详情信息<br>@author 郑宁")
    public ResponseEntity<?> getCourseInfo(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<CourseInforDTO>(scheduleRollCallService.getTeacherCourseInfo(schedule_id), HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/isCanUpdateRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "该课程当前时间是否有排课", httpMethod = "POST", response = Void.class, notes = "该课程当前时间是否有排课<br>@author 李美华")
    public ResponseEntity<?> isCanUpdateRollcall(@RequestHeader("Authorization") String accessToken,
                                                 @ApiParam(value = "courseId 课程ID 必填") @RequestParam(value = "courseId", required = true) Long courseId) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();

        boolean flag = scheduleRollCallService.isCanUpdateRollCall(account.getId(), courseId);
        String message = "";
        if (flag) {
            message = "当前课程有课";
        }
        result.put(ApiReturnConstants.MESSAGE, message);
        result.put(ApiReturnConstants.SUCCESS, flag);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 获取当前教师所带课程点名设置信息
     *
     * @param courseId
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/getCourseRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取当前教师所带课程点名设置信息", response = Void.class, notes = "获取当前教师所带课程点名设置信息<br> @author meihua.li")
    public ResponseEntity<?> getCourseRollCall(
            @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = true) Long courseId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = courseRollCallService.get(courseId, account.getId());
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 根据教师id获取课程列表
     */
    @RequestMapping(value = "/teacher/getCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据教师id获取当前学期的课程列表", response = Void.class, notes = "根据教师id获取课程列表<br>@author 李美华")
    public ResponseEntity<?> getCourseList(@RequestHeader("Authorization") String accessToken) {

        Map<String, Object> result = new HashMap<String, Object>();

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        // 获取课程列表信息
        List<IdNameCode> courseList = orgManagerRemoteService.getSemesterCourseSchedule(account.getId(), null);

        List<CourseRollCallDTO> crcList = new ArrayList();
        CourseRollCallDTO courseRollCallDTO = null;
        // 组装数据
        for (IdNameCode domain : courseList) {
            courseRollCallDTO = new CourseRollCallDTO();
            CourseRollCall courseRollCall = courseRollCallService.get(domain.getId(), account.getId());
            courseRollCallDTO.setId(domain.getId());
            courseRollCallDTO.setCourseId(domain.getId());
            courseRollCallDTO.setCourseName(domain.getName());
            courseRollCallDTO.setReuser(10);// 已废弃

            if (null != courseRollCall) {
                courseRollCallDTO.setIsOpen(courseRollCall.getIsOpen());
                courseRollCallDTO.setLateTime(courseRollCall.getLateTime());
                courseRollCallDTO.setAbsenteeismTime(courseRollCall.getAbsenteeismTime());
                courseRollCallDTO.setRollCallTypeOrigin(courseRollCall.getRollCallType());
            } else {
                courseRollCallDTO.setIsOpen(CourseRollCallConstants.CLOSE_ROLLCALL);
                courseRollCallDTO.setLateTime(15);// 默认15分钟
                courseRollCallDTO.setAbsenteeismTime(0);
                courseRollCallDTO.setRollCallTypeOrigin(ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
            }

            crcList.add(courseRollCallDTO);
        }
        return new ResponseEntity<Object>(crcList, HttpStatus.OK);
    }

    /**
     * 取消正在进行的课程的考勤
     *
     * @param accessToken
     * @param scheduleId
     * @return
     */
    @RequestMapping(value = "/teacher/rollcall/cancleCurrentRollCall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "取消正在进行的课程的考勤", httpMethod = "POST", response = Void.class, notes = "取消正在进行的课程的考勤<br>@author 李美华")
    public ResponseEntity<?> cancleCurrentRollCall(@RequestHeader("Authorization") String accessToken,
                                                   @ApiParam(value = "scheduleId 排课ID") @RequestParam Long scheduleId) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            rollCallService.cancleRollCall(scheduleId, account.getId());
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
    public ResponseEntity<?> updateRollCallResult(@RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "id 点名id 必填") @RequestBody RollCallDTO rollcallDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            rollCallService.updateRollCallResult(rollcallDTO);
            result.put("success", Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", Boolean.FALSE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 批量修改学生签到状态
     *
     * @param accessToken
     * @param rollCallIds
     * @param type
     * @return
     */
    @RequestMapping(value = "/teacher/rollcall/updateRollCall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改部分/全部 学生考勤", httpMethod = "POST", response = Void.class, notes = "修改部分/全部 学生考勤<br>@author 李美华")
    public ResponseEntity<?> updateRollCallResult(@RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "rollCallList 点名id") @RequestParam(value = "rollCallIds", required = true) List<Long> rollCallIds,
                                                  @ApiParam(value = "type 点名结果,1:已到；2：旷课；3：迟到；4：请假；5：早退(选填，不填则为全部)") @RequestParam(value = "type", required = true) String type) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            Set<Long> rollCallIdSet = new HashSet<Long>();
            rollCallIds.forEach(id -> {
                if (id != null) {
                    rollCallIdSet.add(id);
                }
            });
            rollCallService.updateRollCall(rollCallIdSet, type, account.getId());
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 关闭点名
     *
     * @param accessToken
     * @param courseId
     * @return
     */
    @RequestMapping(value = "/teacher/closeRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "关闭点名", httpMethod = "POST", response = Void.class, notes = "关闭点名<br>@author 李美华")
    public ResponseEntity<?> closeRollCall(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "courseId 课程ID 必填") @RequestParam(value = "courseId", required = true) Long courseId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        Map<String, Object> resBody = new HashMap<>();

        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        try {
            courseRollCallService.closeRollCall(courseId, account.getId());
        } catch (Exception e) {

            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            resBody.put(ApiReturnConstants.MESSAGE, "关闭异常");
            e.printStackTrace();
        }
        resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * @param accessToken
     * @param courseId
     * @param rollCallType
     * @param lateTime
     * @param reUser
     * @param isOpen
     * @return
     */
    @RequestMapping(value = "/teacher/updateRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改点名规则", httpMethod = "POST", response = Void.class, notes = "修改点名规则<br>@author 李美华")
    public ResponseEntity<?> updateRollcallRule(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "courseId 课程ID 必填", required = true) @RequestParam(value = "courseId", required = true) Long courseId,
                                                @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填", required = true) @RequestParam(value = "rollCallType", required = true) String rollCallType,
                                                @ApiParam(value = "lateTime 迟到时间 必填", required = true) @RequestParam(value = "lateTime", required = true) int lateTime,
                                                @ApiParam(value = "absenteeismTime 旷课时间", required = false) @RequestParam(value = "absenteeismTime", required = false, defaultValue = "0") Integer absenteeismTime,
                                                @ApiParam(value = "reUser 是否重复是使用 [10:不重复运用,20:重复运用] 必填", required = true) @RequestParam(value = "reUser", required = true) int reUser,
                                                @ApiParam(value = "isOpen 是否开启[disable:关闭,enable:开启] 必填", required = true) @RequestParam(value = "isOpen", required = true) String isOpen) {
        Map<String, Object> resBody = new HashMap<>();
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (absenteeismTime == null) {
            absenteeismTime = 0;
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
            // 修改点名规则，需要判断是否在课中修改，课中修改清除该节课的点名记录，学生需要重新签到。
            // 根据课程id，教师ID,教学时间
            if (CourseRollCallConstants.OPEN_ROLLCALL.equals(isOpen)) {
                List<Schedule> curSchedules = scheduleService.listScheduleByTeacherIdAndCourseIdAndTeachDate(account.getId(), courseId, DateFormatUtil.format(new Date(), DateFormatUtil.FORMAT_SHORT));
                System.out.println("修改打卡机...,获取...");
                for (Schedule schedule : curSchedules) {
                    if (CourseUtils.isSchoolTime(schedule.getScheduleStartTime(), schedule.getScheduleEndTime())) {
                        if (!initScheduleService.initScheduleRollCall(schedule, true, lateTime, absenteeismTime, rollCallType, isOpen)) {
                            result.put(ApiReturnConstants.SUCCESS, false);
                            result.put(ApiReturnConstants.MESSAGE, "修改点名设置异常,");
                            return new ResponseEntity<Object>(result, HttpStatus.OK);
                        }
                        break;
                    }
                }
            }
            result.put(ApiReturnConstants.SUCCESS, true);
        } catch (Exception e) {
            result.put(ApiReturnConstants.SUCCESS, false);
            result.put(ApiReturnConstants.MESSAGE, "修改点名设置异常," + e);
            e.printStackTrace();
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 开启随堂点
     *
     * @param scheduleId
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/rollcall/openClassrommRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "开启随堂点", httpMethod = "POST", response = Void.class, notes = "开启随堂点<br>@author 李美华")
    public ResponseEntity<?> openClassrommRollcall(@RequestHeader("Authorization") String accessToken,
                                                   @ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            // 1.修改排课表，修改其标志为 开启随堂点
            Schedule schedule = scheduleService.findOne(scheduleId);
            if (schedule == null) {
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                result.put(ApiReturnConstants.MESSAGE, "开启随堂点失败!");
            } else {
                rollCallService.openClassrommRollcall(schedule.getId(), ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "开启随堂点失败!");
        }
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
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
    public ResponseEntity<?> openClassrommRollcallV2(@RequestHeader("Authorization") String accessToken,
                                                     @ApiParam(value = "rollcallType 点名方式") @RequestParam(value = "rollcallType", required = true) String rollcallType,
                                                     @ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            // 1.修改排课表，修改其标志为 开启随堂点
            Schedule schedule = scheduleService.findOne(scheduleId);
            if (schedule == null) {
                result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
                result.put(ApiReturnConstants.MESSAGE, "开启随堂点失败!");
            } else {
                rollCallService.openClassrommRollcall(schedule.getId(), ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollcallType) ? ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC : ScheduleConstants.TYPE_ROLL_CALL_DIGITAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "开启随堂点失败!");
        }
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/rollcall/closeClassrommRollcall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "关闭随堂点", httpMethod = "POST", response = Void.class, notes = "关闭随堂点<br>@author 李美华")
    public ResponseEntity<?> closeClassrommRollcall(@RequestHeader("Authorization") String accessToken,
                                                    @ApiParam(value = "scheduleId 排课id") @RequestParam(value = "scheduleId", required = true) Long scheduleId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            ScheduleRollCall scheduleRollCall = scheduleRollCallService.findBySchedule(scheduleId);
            rollCallService.closeClassrommRollcall(scheduleId, scheduleRollCall);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            result.put(ApiReturnConstants.MESSAGE, "关闭随堂点失败!");
        }
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 教师某学周课表查询
     *
     * @param weekId
     * @param accessToken
     * @return
     * @throws URISyntaxException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/course/getweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师某学周课程表查询", response = Void.class, notes = "查询教师某学周课程表<br>@author 郑宁")
    public ResponseEntity<?> getCourseWeek(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "weekId 学周id") @RequestParam(value = "weekId", required = false) Long weekId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List>(scheduleService.getTeacherCourseWeek(account.getId(), weekId, account.getOrganId()), HttpStatus.OK);
    }

    /**
     * 认领课程
     */
    @RequestMapping(value = "/teacher/claim", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "认领", response = Void.class, notes = "认领<br>@author meihua.li")
    public ResponseEntity<?> claim(@RequestHeader("Authorization") String accessToken,
                                   @RequestParam(value = "teachingclassId", required = true) Long teachingclassId,
                                   @RequestParam(value = "teachDate", required = true) String teachDate,
                                   @RequestParam(value = "periodNo", required = true) Integer periodNo,
                                   @RequestParam(value = "periodNum", required = true) Integer periodNum) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(scheduleService.claim(account.getId(), account.getName(), teachingclassId, teachDate, periodNo, periodNum), HttpStatus.OK);
    }

    /**
     * 获取某一排课所上的班级
     *
     * @param scheduleId
     * @param accessToken
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/teacher/getclassbyschedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取某一排课的班级", response = Void.class, notes = "获取某一排课的班级<br>@author 杨立强")
    public ResponseEntity<?> getClassBySchedule(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "scheduleId 排课id（必填）") @RequestParam(value = "scheduleId", required = true) Long scheduleId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (scheduleId == null) {
            Map<String, String> resBody = new HashMap<String, String>();
            resBody.put(ApiReturnConstants.CODE, AssessConstants.EMPTY_SCHEDULE_ID);
            resBody.put(ApiReturnConstants.MESSAGE, "Schedule Id is required");
            return new ResponseEntity<Object>(resBody, HttpStatus.OK);
        }
        Object resBody = scheduleRollCallService.getClassBySchedule(scheduleId);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
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
    public ResponseEntity<?> passLeaveRequest(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "leaveId 请假申请id") @RequestParam(value = "leaveId", required = true) Long leaveId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = null;
        try {
            resBody = leaveService.passLeaveRequest(leaveId, account, accessToken);
        } catch (Exception e) {
            Map<String, Object> res = new HashMap<>();
            res.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            res.put(ApiReturnConstants.MESSAGE, "error");
            return new ResponseEntity<Object>(resBody, HttpStatus.OK);
        }
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 驳回请假
     *
     * @param leaveId
     * @param content
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/rejectleaverequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "驳回请假", response = Void.class, notes = "驳回请假<br>@author 杨立强")
    public ResponseEntity<?> rejectLeaveRequest(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "leaveId 请假申请id") @RequestParam(value = "leaveId", required = true) Long leaveId,
                                                @ApiParam(value = "content 驳回理由") @RequestParam(value = "content", required = false) String content) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = leaveService.rejectLeaveRequest(leaveId, content, account.getId(), accessToken);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 老师根据状态获取请假列表
     *
     * @param status
     * @param orderByKey
     * @param orderBy
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/getleaverequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "老师根据状态获取请假列表", response = Void.class, notes = "老师根据状态获取请假列表<br>@author 杨立强")
    public ResponseEntity<?> getLeaveRequest(@RequestHeader("Authorization") String accessToken,
                                             @ApiParam(value = "status 请假状态（申请：request；已处理：processed；撤销：cancle）") @RequestParam(value = "status", required = true) String status,
                                             @ApiParam(value = "orderByKey 请假状态（最后修改时间：lastModifiedTime；创建时间：createdTime；）") @RequestParam(value = "orderByKey", required = true) String orderByKey,
                                             @ApiParam(value = "orderBy 排序（顺序：asc；撤销：desc）") @RequestParam(value = "orderBy", required = true) String orderBy) {
        if (!orderByKey.equals("lastModifiedTime") && !orderByKey.equals("createdTime")) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "orderByKey faild");
            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
        }
        if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "orderBy faild");
            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
        }
        orderByKey = orderByKey.equals("lastModifiedTime") ? "last_modified_date" : "created_date";
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = leaveService.getLeaveRequsetByTeacherAndStatus(account.getId(), status, account.getOrganId(), orderByKey, orderBy);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 老师端评教结果统计
     *
     * @param accessToken
     * @param schedule_id
     * @param offset
     * @param limit
     * @return
     */
    @RequestMapping(value = "/assess/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "老师端评教结果统计查询", httpMethod = "GET", response = Void.class, notes = "评教结果统计查询<br>@author meihua.li")
    public ResponseEntity<?> query(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "schedule_id 排课id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        AssessPageInfo<Map<String, String>> page = assessService.getAssess(limit, offset, schedule_id);
        return new ResponseEntity<AssessPageInfo<Map<String, String>>>(page, HttpStatus.OK);
    }

    /**
     * 根据班级和排课获取评教内容
     *
     * @param scheduleId
     * @param classId
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/getassessbyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级和排课获取评教", response = Void.class, notes = "根据班级和排课获取评教<br>@author meihua.li")
    public ResponseEntity<?> getAssessByClass(
            @ApiParam(value = "scheduleId 排课id（必填）") @RequestParam(value = "scheduleId", required = true) Long scheduleId,
            @ApiParam(value = "classId 班级id（必填）") @RequestParam(value = "classId", required = true) Long classId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Object resBody = assessService.getAssessByClass(scheduleId, classId);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 教师中心获取评教 全部周
     *
     * @param weekId
     */
    @RequestMapping(value = "/teacher/getassessbyweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "个人中心获取评教信息", response = Void.class, notes = "个人中心获取评教信息<br>@author 杨立强")
    public ResponseEntity<?> getAssessByWeek(
            @ApiParam(value = "weekId 周id（必填）") @RequestParam(value = "weekId", required = true) Long weekId,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Object resBody = assessService.getAssessInfoByTeacher(weekId, account.getId());
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 教师中心获取出勤 单周
     *
     * @param weekId
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/teacher/getrollcallbyweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "个人中心获取出勤信息 单周", response = Void.class, notes = "个人中心获取出勤信息 单周<br>@author 杨立强")
    public ResponseEntity<?> getRollCallByWeek(
            @ApiParam(value = "weekId 周id（必填）") @RequestParam(value = "weekId", required = true) Long weekId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Object resBody = rollCallService.getRollCallInfoByTeacherAndWeek(weekId, account.getId());
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 老师根据状态获取请假列表
     *
     * @param orderByKey
     * @param orderBy
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/teacher/getleaverequestforteach", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "授课老师查看请假通知", response = Void.class, notes = "老师根据状态获取请假列表<br>@author 杨立强")
    public ResponseEntity<?> getLeaveRequestForTeach(
            @ApiParam(value = "orderByKey 请假状态（最后修改时间：lastModifiedTime；创建时间：createdTime；）") @RequestParam(value = "orderByKey", required = true) String orderByKey,
            @ApiParam(value = "orderBy 排序（顺序：asc；撤销：desc）") @RequestParam(value = "orderBy", required = true) String orderBy,
            @RequestHeader("Authorization") String accessToken) {

//        if (!orderByKey.equals("lastModifiedTime") && !orderByKey.equals("createdTime")) {
//            Map<String, Object> resBody = new HashMap<>();
//            resBody.put("message", "orderByKey faild");
//            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
//        }
//        if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
//            Map<String, Object> resBody = new HashMap<>();
//            resBody.put("message", "orderBy faild");
//            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
//        }
//        orderByKey = orderByKey.equals("lastModifiedTime") ? "last_modified_date" : "created_date";
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        Object resBody = leaveService.getLeaveRequsetForTeachByTeacherAndStatus(account, null,
                account.getOrganId(), orderByKey, orderBy);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 手机教师端课程评分信息
     *
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/queryCourseAssess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "课程评分", response = Void.class, notes = "课程评分<br>@author bly")
    public ResponseEntity<?> queryCourseAssess(
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageInfo<TeacherPhoneCourseAssessDTO> pageInfo = courseAssessService.queryTeacherPhoneCourseAssess(account.getId(), offset, limit);
        return new ResponseEntity<>(pageInfo, HttpStatus.OK);
    }

    /**
     * 手机教师端课程评分详情
     *
     * @param teachingClassId
     * @param offset
     * @param limit
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/queryCourseAssessDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "课程评分详情", response = Void.class, notes = "课程评分详情<br>@author bly")
    public ResponseEntity<?> queryCourseAssessDetails(
            @ApiParam(value = "教学班Id") @RequestParam(value = "teachingClassId", required = true) Long teachingClassId,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @RequestHeader("Authorization") String accessToken) throws DlEduException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageInfo<TeacherPhoneCourseAssessDetailsDTO> pageInfo =
                courseAssessService.queryTeacherPhoneCourseAssessDetails(teachingClassId, offset, limit, account);
        return new ResponseEntity<>(pageInfo, HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/courselorClassAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员班级到课率", response = Void.class, notes = "课程评分详情<br>@author bly")
    public ResponseEntity<?> courselorClassAttendance(@ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = true) Long semesterId, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        if (null == semesterId) {
            semesterId = semesterService.getSemesterId(account.getOrganId());
        }

        return new ResponseEntity<>(rollCallService.courselorClassAttendance(account.getId(), semesterId), HttpStatus.OK);
    }

    /**
     * 手机教师端临时调课
     *
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/tempAdjustCourseSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "临时调课", response = Void.class, notes = "临时调课<br>@author bly")
    public ResponseEntity<?> tempAdjustCourseSchedule(
            @RequestHeader("Authorization") String accessToken, @RequestBody TempAdjustCourseFullDomain dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(tempAdjustCourseScheduleService.tempAdjustCourseScheduleService(accessToken, account, dto), HttpStatus.OK);
    }

    /**
     * 手机教师端临时停课
     *
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/tempAdjustCourseScheduleStop", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "停课", response = Void.class, notes = "停课<br>@author bly")
    public ResponseEntity<?> tempAdjustCourseScheduleStop(
            @RequestHeader("Authorization") String accessToken, @RequestBody TempAdjustCourseFullDomain dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(tempAdjustCourseScheduleService.stopSchedule(accessToken, account, dto), HttpStatus.OK);
    }

    /**
     * 手机教师端临时加课
     *
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/tempAdjustCourseScheduleAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "加课", response = Void.class, notes = "加课<br>@author bly")
    public ResponseEntity<?> tempAdjustCourseScheduleAdd(
            @RequestHeader("Authorization") String accessToken, @RequestBody TempAdjustCourseFullDomain dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(tempAdjustCourseScheduleService.addSchedule(accessToken, account, dto), HttpStatus.OK);
    }

    /**
     * 手机教师端临时加课
     *
     * @param accessToken
     * @return
     * @throws DlEduException
     */
    @RequestMapping(value = "/teacher/getTeachingclassidByTeacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学班信息", response = Void.class, notes = "获取教学班信息<br>@author bly")
    public ResponseEntity<?> getTeachingclassidByTeacher(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (null == semesterId) {
            IdNameDomain currentSemester = semesterService.getCurrentSemester(account.getOrganId());
            semesterId = currentSemester.getId();
        }
        return new ResponseEntity<>(teachingClassesService.getTeachingClassIdByTeacher(account.getId(), semesterId), HttpStatus.OK);
    }


    @RequestMapping(value = "/teacher/courselorStudentAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员学生到课率", response = Void.class, notes = "辅导员学生到课率<br>@author bly")
    public ResponseEntity<?> courselorStudentAttendance(@ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = true) Long semesterId,
                                                        @ApiParam(value = "班级Id") @RequestParam(value = "classId", required = true) Long classId,
                                                        @ApiParam(value = "排序  normal(正常多到少排序)/exception(异常多到少排序)") @RequestParam(value = "sort", required = false) String sort,
                                                        @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<CounselorStudentAddendanceDTO> csd = rollCallService.courselorStudentAttendance(classId, semesterId, sort);
        return new ResponseEntity<>(csd, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/teacher/studentOneAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员查看学生考勤", response = Void.class, notes = "辅导员查看学生考勤<br>@author meihua.li")
    public ResponseEntity<?> studentOneAttendance(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "学生Id") @RequestParam(value = "studentId", required = false) Long studentId,
            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId,
            @ApiParam(value = "状态类型 1-已到 2-旷到 3-迟到 4-请假 5-早退") @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = true) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = true) Integer limit) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        PageData<AttendanceAllDTO> listPage = rollCallService.getSchedule(offset, null == limit ? 10 : limit, account.getOrganId(),
                studentId, semesterId, typeId, null, null);

        List<AttendanceAllDTO> list = listPage.getData();
        List<AttendanceAllDTO> tempdata = new ArrayList<>();
        List toList = new ArrayList();
        String date = "";
        Map map = null;
        for (AttendanceAllDTO o : list) {
            String year = DateFormatUtil.format(o.getTeach_time()).substring(0, 4);//遍历出来的时间
            String month = DateFormatUtil.format(o.getTeach_time()).substring(5, 7);//遍历出来的时间
            if (date.equals(month)) {
                tempdata.add(o);
            } else {
                map = new HashMap();
                map.put("year", year);
                map.put("month", month);
                tempdata = new ArrayList();
                tempdata.add(o);
                map.put("data", tempdata);
                toList.add(map);
            }
            date = month;
        }

        PageInfo page = new PageInfo();
        Long count = Long.valueOf(null == list ? 0 : list.size());
        limit = (null == limit ? 100 : limit);
        page.setPageCount(listPage.getPage().getTotalPages());
        page.setTotalCount(listPage.getPage().getTotalElements());
        page.setOffset(offset);
        page.setLimit(limit);
        page.setData(toList);
        return new ResponseEntity<Object>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/checkSchedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "计算中值", httpMethod = "POST", response = Void.class, notes = "计算中值<br>@author 李美华")
    public ResponseEntity<?> closeClassrommRollcall(
            @RequestHeader("Authorization") String accessToken) {
        rollCallServiceV2.checkRollCallTypeSchedule();
        return new ResponseEntity<Object>(Boolean.FALSE, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/initScheduleRollCallAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "初始化课堂规则的到课率", httpMethod = "GET", response = Void.class, notes = "初始化课堂规则的到课率<br>@author meihua")
    public ResponseEntity<?> initScheduleRollCallAttendance(
            @RequestParam(value = "organIds") Set organIds,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        if (null == organIds) {
            organIds = new HashSet();
            organIds.add(account.getOrganId());
        }
        rollCallService.initScheduleRollCallAttendance(organIds);
        return new ResponseEntity<Object>(Boolean.FALSE, HttpStatus.OK);
    }
}
