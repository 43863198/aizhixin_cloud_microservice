package com.aizhixin.cloud.dd.rollcall.v1.controller;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aizhixin.cloud.dd.common.domain.IdNameDomain;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.service.*;
import com.aizhixin.cloud.dd.rollcall.serviceV2.RollCallServiceV2;
import com.aizhixin.cloud.dd.rollcall.serviceV3.RollCallServiceV3;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.LeaveConstants;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "手机学生端API", description = "针对手机端的相关API")
public class StudentPhoneResource {

    private final Logger log = LoggerFactory.getLogger(StudentPhoneResource.class);

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CourseRollCallService courseRollCallService;
    @Autowired
    private ScheduleRollCallService scheduleRollCallService;
    @Autowired
    private ScheduleService scheduleService;
    @Lazy
    @Autowired
    private RollCallService rollCallService;
    @Autowired
    private RollCallServiceV2 rollCallServiceV2;
    @Autowired
    private RollCallServiceV3 rollCallServiceV3;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private AssessService assessService;
    @Autowired
    private UserService userService;
    @Autowired
    private SemesterService semesterService;
    @Autowired
    private StudentService studentService;

    /**
     * 查询学生当天课程列表
     *
     * @param accessToken
     * @param teachTime
     * @param offset
     * @param limit
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/courselist/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生课程列表", response = Void.class, notes = "查询学生课程列表<br>@author meihua.li")
    public ResponseEntity<?> getStudentCourseList(@ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "teachTime 时间") @RequestParam(value = "teachTime", required = false) String teachTime,
                                                  @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
                                                  @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageInfo page = null;
        try {
            page = scheduleRollCallService.getStudentCourseListDay(PageUtil.createNoErrorPageRequest(offset, limit), account, teachTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/students/signCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "签到课程", response = Void.class, notes = "签到课程<br>@author meihua.li")
    public ResponseEntity<?> getStudentSignCourse(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        List<StudentScheduleDTO> studentSignCourse = scheduleRollCallService.getStudentSignCourse(account.getId());
        if (null == studentSignCourse) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(studentSignCourse, HttpStatus.OK);
    }

    @RequestMapping(value = "/students/signCourseV2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "签到课程 优化", response = Void.class, notes = "签到课程 优化<br>@author meihua.li")
    public ResponseEntity<?> getStudentSignCourseV2(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        List<StudentScheduleDTO> studentSignCourse = scheduleRollCallService.getStudentSignCourseV2(account.getOrganId(), account.getId());
        if (null == studentSignCourse) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(studentSignCourse, HttpStatus.OK);
    }

    /**
     * 获取课程详情
     *
     * @param schedule_id
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @RequestMapping(value = "/student/coursedetail/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生课程详情信息查询", response = Void.class, notes = "查询学生课程详情信息<br>@author meihua.li")
    public ResponseEntity<?> getCourseInfo(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<StudentScheduleDTO>(scheduleRollCallService.getStudentCourseInfo(account, schedule_id), HttpStatus.OK);
    }

    /**
     * 签到
     *
     * @param scheduleId
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/student/report", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "签到", response = Void.class, notes = "签到<br>@author meihua.li")
    public ResponseEntity<?> report(
            @ApiParam(value = "scheduleId 排课表id(必填)") @RequestParam(value = "scheduleId", required = true) Long scheduleId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
//        Object resBody = rollCallService.excuteReport(scheduleId, account);

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("message", "success!");
        resBody.put("success", Boolean.TRUE);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/signIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "签到优化", response = Void.class, notes = "签到<br>@author meihua.li")
    public ResponseEntity<?> signIn(@RequestHeader("Authorization") String accessToken,
                                    @ApiParam(value = "点名信息") @RequestBody SignInDTO signInDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        String rollCallType = signInDTO.getRollCallType();
        if (!ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollCallType) && !ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(rollCallType)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "rollcalltype is error !");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        // 验证 验证码和经纬度是否合法
        Object resBody = rollCallServiceV2.excuteSignIn(account, signInDTO);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }


    @RequestMapping(value = "/student/signInThree", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "签到优化3", response = Void.class, notes = "签到<br>@author meihua.li")
    public ResponseEntity<?> ignIns3(
            @ApiParam(value = "点名信息") @RequestBody SignInDTO signInDTO,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        String rollCallType = signInDTO.getRollCallType();
        if (!ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollCallType)
                && !ScheduleConstants.TYPE_ROLL_CALL_DIGITAL
                .equals(rollCallType)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "rollcalltype is error !");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        // 验证 验证码和经纬度是否合法
        Map<String, Object> resBody = new HashMap<>();
//        Object resBody = rollCallServiceV2.excuteSignIn(account, signInDTO);
        rollCallServiceV3.excuteSignIn(account, signInDTO);
        resBody.put(ApiReturnConstants.MESSAGE, "签到结果确认中!");
        resBody.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/signInNull", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "签到优化", response = Void.class, notes = "签到<br>@author meihua.li")
    public ResponseEntity<?> signInNull(
            @ApiParam(value = "点名信息") @RequestBody SignInDTO signInDTO,
            @RequestHeader("Authorization") String accessToken) {
        return new ResponseEntity<Object>(System.currentTimeMillis(), HttpStatus.OK);
    }

    /**
     * 学生某学周课程表查询
     *
     * @param accessToken
     * @param weekId
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/courseweek/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生某学周课程表查询", response = Void.class, notes = "查询学生某学周课程表<br>@author 潘震")
    public ResponseEntity<?> getCourseWeek(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "weekId 学周id") @RequestParam(value = "weekId", required = false) Long weekId) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (weekId == null || weekId < 1) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "weekId 学周id 必填");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List>(scheduleService.getStudentScheduleCourseWeek(account, weekId), HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/requestleave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "学生请假申请", response = Void.class, notes = "学生请假申请<br>@author 杨立强")
    public ResponseEntity<?> requestLeave(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "isLeaveSchoole 是否离校") @RequestParam(value = "isLeaveSchoole", required = true) Boolean isLeaveSchoole,
            @ApiParam(value = "requestType 请假类型(period(课程节)/day(多天))") @RequestParam(value = "requestType", required = false) String requestType,
            @ApiParam(value = "startPeriodId 请假开始课程节（type为period时必填）") @RequestParam(value = "startPeriodId", required = false) Long startPeriodId,
            @ApiParam(value = "endPeriodId 请假结束课程节（type为period时必填）") @RequestParam(value = "endPeriodId", required = false) Long endPeriodId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "startDate 请假开始天(格式:yyyy-MM-dd)") @RequestParam(value = "startDate", required = true) Date startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endDate 请假结束天(type为day时;必填格式:yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) Date endDate,
            @ApiParam(value = "content 请假理由") @RequestParam(value = "content", required = false) String content) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> resBody = new HashMap<>();
        if (requestType.equals(LeaveConstants.TYPE_DAY) && endDate == null) {
            resBody.put(ApiReturnConstants.MESSAGE, "if requestType was 'day' then endDate must be required");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (requestType.equals(LeaveConstants.TYPE_PERIOD)
                && (startPeriodId == null || endPeriodId == null)) {
            resBody.put(ApiReturnConstants.MESSAGE, "if requestType was 'period' then endPeriodId and startPeriodId must be required");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }

        // 取消请假发送班主任;
        Map<String, Object> teacherMap = userService.getClassTeacherByStudentId(account.getId());
        Long headTeacherId = null;
        String headTeacherName = "";
        if (null != teacherMap && teacherMap.size() > 0) {
            Integer temp = (Integer) teacherMap.get("id");
            if (null != temp) {
                headTeacherId = temp.longValue();
                headTeacherName = String.valueOf(teacherMap.get("name"));
            }
        }
        Object res = leaveService.requestLeave(account, isLeaveSchoole, requestType, startPeriodId, endPeriodId, startDate, endDate, content, headTeacherId, headTeacherName, accessToken, null);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 学生请假申请，包含上传照片
     *
     * @param accessToken
     * @param isLeaveSchoole
     * @param requestType
     * @param startPeriodId
     * @param endPeriodId
     * @param startDate
     * @param endDate
     * @param content
     * @param files
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/requestLeaveAddPic", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "学生请假申请，上传照片", response = Void.class, notes = "学生请假申请，上传照片<br>@author 李美华")
    public ResponseEntity<?> requestLeaveAddPic(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "isLeaveSchoole 是否离校") @RequestParam(value = "isLeaveSchoole", required = true) Boolean isLeaveSchoole,
            @ApiParam(value = "requestType 请假类型(period(课程节)/day(多天))") @RequestParam(value = "requestType", required = false) String requestType,
            @ApiParam(value = "startPeriodId 请假开始课程节（type为period时必填）") @RequestParam(value = "startPeriodId", required = false) Long startPeriodId,
            @ApiParam(value = "endPeriodId 请假结束课程节（type为period时必填）") @RequestParam(value = "endPeriodId", required = false) Long endPeriodId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "startDate 请假开始天(格式:yyyy-MM-dd)") @RequestParam(value = "startDate", required = true) Date startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endDate 请假结束天(type为day时;必填格式:yyyy-MM-dd)") @RequestParam(value = "endDate", required = false) Date endDate,
            @ApiParam(value = "content 请假理由") @RequestParam(value = "content", required = false) String content,
            @ApiParam(value = "files 上传照片") @RequestParam(value = "file", required = false) MultipartFile[] files) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> resBody = new HashMap<>();
        if (requestType.equals(LeaveConstants.TYPE_DAY) && endDate == null) {
            resBody.put(ApiReturnConstants.MESSAGE, "if requestType was 'day' then endDate must be required");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (requestType.equals(LeaveConstants.TYPE_PERIOD) && (startPeriodId == null || endPeriodId == null)) {
            resBody.put(ApiReturnConstants.MESSAGE, "if requestType was 'period' then endPeriodId and startPeriodId must be required");
            resBody.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        // TODO: 要支持多个辅导员
        Map<String, Object> teacherMap = userService.getClassTeacherByStudentId(account.getId());
        Long headTeacherId = null;
        String headTeacherName = "";
        if (null != teacherMap && teacherMap.size() > 0) {
            Integer temp = (Integer) teacherMap.get("id");
            if (null != temp) {
                headTeacherId = temp.longValue();
                headTeacherName = String.valueOf(teacherMap.get("name"));
            }
        }
        Object res = leaveService.requestLeave(account, isLeaveSchoole, requestType, startPeriodId, endPeriodId, startDate, endDate, content, headTeacherId, headTeacherName, accessToken, files);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 学生取消请假
     *
     * @param leaveId
     * @param accessToken
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @RequestMapping(value = "/student/cancleleaverequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生取消请假", response = Void.class, notes = "学生取消请假<br>@author 杨立强")
    public ResponseEntity<?> cancleLeaveRequest(
            @ApiParam(value = "leaveId 请假申请id") @RequestParam(value = "leaveId", required = true) Long leaveId,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Object resBody = leaveService.cancleLeaveRequest(leaveId, account.getId());
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

    /**
     * 学生根据状态获取请假列表
     */
    @RequestMapping(value = "/student/getleaverequest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生根据状态获取请假列表", response = Void.class, notes = "学生根据状态获取请假列表<br>@author 杨立强")
    public ResponseEntity<?> getLeaveRequest(
            @ApiParam(value = "status 请假状态（申请：request；已处理：processed；撤销：cancle；）") @RequestParam(value = "status", required = true) String status,
            @ApiParam(value = "orderByKey 请假状态（最后修改时间：lastModifiedTime；创建时间：createdTime；）") @RequestParam(value = "orderByKey", required = true) String orderByKey,
            @ApiParam(value = "orderBy 排序（顺序：asc；撤销：desc）") @RequestParam(value = "orderBy", required = true) String orderBy,
            @RequestHeader("Authorization") String accessToken) {

        if (!orderByKey.equals("lastModifiedTime") && !orderByKey.equals("createdTime")) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "orderByKey faild");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        if (!orderBy.equals("asc") && !orderBy.equals("desc")) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put("message", "orderBy faild");
            return new ResponseEntity<Object>(resBody, HttpStatus.UNAUTHORIZED);
        }
        orderByKey = orderByKey.equals("lastModifiedTime") ? "last_modified_date" : "created_date";

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Object resBody = leaveService.getLeaveRequsetByStudentAndStatus(account.getId(), status, account.getOrganId(), orderByKey, orderBy);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }


    /**
     * 查询学生特定一趟课的评价信息
     *
     * @param accessToken
     * @param schedule_id
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/course/assess/getmy", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生课程表课程详情评教查询", response = Void.class, notes = "查询学生特定课程评教信息<br>@author 潘震")
    public ResponseEntity<?> getStudentCourseAssess(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        PageInfo page = assessService.getMyAssess(schedule_id, account.getId());
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    /**
     * 学生未评教查询
     *
     * @param accessToken
     * @param offset
     * @param limit
     * @return
     * @throws URISyntaxException
     * @throws DlEduException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/notassess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生未评教查询", response = Void.class, notes = "学生未评教查询 无用的接口<br>@author 杨立强")
    public ResponseEntity<?> notAssess(@RequestHeader("Authorization") String accessToken,
                                       @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
                                       @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        PageInfo page = assessService.getNotAssessList(limit, offset, account.getId());
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * 学生端评教结果保存
     *
     * @param accessToken
     * @param schedule_id
     * @param score
     * @param content
     * @return
     */
    @RequestMapping(value = "/assess/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "评教结果保存", httpMethod = "POST", response = Void.class, notes = "评教结果保存<br>@author 潘震")
    public ResponseEntity<?> update(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "schedule_id 排课表id(必填)") @RequestParam(value = "schedule_id", required = true) Long schedule_id,
            @ApiParam(value = "score 评教分数(0到5分，表示0到5颗星星),必填") @RequestParam(value = "score", required = true) Integer score,
            @ApiParam(value = "content 评教内容,选填") @RequestParam(value = "content", required = false) String content) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        if (null == schedule_id || 0l == schedule_id) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            resBody.put(ApiReturnConstants.CAUSE, "排课id不能为空");
            return new ResponseEntity<Object>(resBody,
                    HttpStatus.EXPECTATION_FAILED);
        }
        AssessDTO assesslDTO = new AssessDTO();
        assesslDTO.setScheduleId(schedule_id);
        assesslDTO.setScore(score);
        assesslDTO.setContent(content);
        assesslDTO.setStudentId(account.getId());

        assesslDTO.setClassesId(studentService.getClassByStudentId(account.getId()).getId());
        return assessService.save(assesslDTO);
    }


    /**
     * 学生按照教学日获取课程节
     *
     * @param teachDay
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/period/getbyteachday", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生按照教学日获取课程节", response = Void.class, notes = "学生按照教学日获取课程节<br>@author meihua.li")
    public ResponseEntity<?> getPeriodByDay(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "teachDay 查询天(格式:yyyy-MM-dd)") @RequestParam(value = "startDay", required = true) Date teachDay,
            @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        List<PeriodDTO> list = periodService.findAllByOrganIdAndStatus(account.getId(), account.getOrganId(), teachDay);
        return new ResponseEntity(list, HttpStatus.OK);
    }


    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/Schedule/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "考勤记录查询", response = Void.class, notes = "学生的考勤记录<br>@author 段伟")
    public ResponseEntity<?> getSchedule(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) String semesterId,
            @ApiParam(value = "状态类型 1-已到 2-旷到 3-迟到 4-请假 5-早退") @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(value = "课程名字") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }

        if (StringUtils.isBlank(semesterId)) {
            IdNameDomain currentSemester = semesterService.getCurrentSemester(account.getOrganId());
            if (currentSemester == null) {
                semesterId = String.valueOf(0);
            } else {
                semesterId = currentSemester.getId() + "";
            }
        }

        PageData<AttendanceAllDTO> listPage = rollCallService.getSchedule(offset, null == limit ? 10 : limit, account.getOrganId(),
                account.getId(), Long.valueOf(semesterId), typeId, courseName, null);

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
        page.setPageCount(listPage.getPage().getTotalPages());
        page.setTotalCount(listPage.getPage().getTotalElements());
        page.setOffset(offset);
        page.setLimit(null == limit ? 100 : limit);
        page.setData(toList);
        return new ResponseEntity<Object>(page, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/attendance/getCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "考勤记录比例", response = Void.class, notes = "学生各项考勤分别统计<br>@author 段伟")
    public ResponseEntity<?> getCount(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = false) Long semesterId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        List<AttendanceCountDTO> list = rollCallService.getAttendance(account.getOrganId(), account.getId(),
                semesterId);
        PageInfo page = new PageInfo();
        page.setData(list);
        return new ResponseEntity<Object>(page, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/student/attendance/getcourseCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "考勤按课程查询", response = Void.class, notes = "学生的课程考勤记录总数<br>@author 段伟")
    public ResponseEntity<?> getcourseCount(
            @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "学期Id") @RequestParam(value = "semesterId", required = true) String semesterId,
            @ApiParam(value = "状态类型 1-已到 2-旷到 3-迟到 4-请假 5-早退") @RequestParam(value = "typeId", required = false) String typeId,
            @ApiParam(value = "课程名") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "课程名") @RequestParam(value = "courseName", required = false) Long courseId,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(),
                    HttpStatus.UNAUTHORIZED);
        }
        List<AttendanceCountDTO> list = new ArrayList<>();
        list = rollCallService.getcourseCount(offset, limit, account.getOrganId(),
                account.getId(), typeId, Long.valueOf(semesterId), courseName, courseId);
        if (null != list && list.size() > 0) {
            for (AttendanceCountDTO data : list) {
                data.setNormal(data.getArrvied() + data.getBeg());
                data.setAbnormal(data.getCrunk() + data.getEarly() + data.getLate());
            }
        }
        PageInfo page = new PageInfo();
        page.setPageCount(1);
        page.setTotalCount(Long.valueOf(list.size()));
        page.setLimit(null == limit ? 10 : limit);
        page.setOffset(offset);
        page.setData(list);
        return new ResponseEntity<Object>(page, HttpStatus.OK);
    }

}
