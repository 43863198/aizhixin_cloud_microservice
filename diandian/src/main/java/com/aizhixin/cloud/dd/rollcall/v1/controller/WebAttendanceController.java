package com.aizhixin.cloud.dd.rollcall.v1.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.exception.DlEduException;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.AttendanceRecordDTO;
import com.aizhixin.cloud.dd.rollcall.dto.Statistics.RollcallStatisticsDTO;
import com.aizhixin.cloud.dd.rollcall.service.AttendanceRecordService;
import com.aizhixin.cloud.dd.rollcall.service.AttendanceStatisticsService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;

/**
 * Created by LIMH on 2017/9/18.
 */
@RestController
@RequestMapping("/api/web/v1")
@Api(value = "Web端API", description = "针对Web端的相关API")
public class WebAttendanceController {

    private final Logger log = LoggerFactory.getLogger(WebAttendanceController.class);

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private AttendanceStatisticsService attendanceStatisticsService;
    @Autowired
    private AttendanceRecordService attendanceRecordService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;

    @RequestMapping(value = "/attendance/allTeacherAttendanceByOrgId", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员到课率", response = Void.class, notes = "辅导员到课率<br>@author hsh")
    public ResponseEntity<?> allTeacherAttendanceByOrgId(@RequestHeader("Authorization") String accessToken,
                                                         @ApiParam(value = "院系id") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                         @ApiParam(value = "辅导员姓名工号") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间", required = true) @RequestParam(value = "beginDate", required = true) String beginDate,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间", required = true) @RequestParam(value = "endDate", required = true) String endDate,
                                                         @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                         @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(attendanceStatisticsService.allTeacherAttendanceByOrgId(account.getOrganId(), collegeId, teacherName, pageNumber, pageSize, beginDate, endDate), HttpStatus.OK);
    }

    /**
     * 教学班考勤 按老师
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/attendance/teachingclassByTeacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教学班考勤 按老师", response = Void.class, notes = "教学班考勤 按老师<br>@author meihua.li")
    public ResponseEntity<?> teachingclassByTeacher(@ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                    @ApiParam(value = "老师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = true) String beginDate,
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = true) String endDate,
                                                    @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(attendanceStatisticsService.attendanceByTeacher(pageNumber, pageSize, account.getOrganId(), null, collegeId, teacherName, beginDate, endDate),
                HttpStatus.OK);

    }

    @RequestMapping(value = "/attendance/exportTeachingclassByTeacher", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出教学班考勤 按老师", response = Void.class, notes = "导出教学班考勤 按老师 <b>@author meihua.li</b>")
    public ResponseEntity<?> exportAttendanceByPeriod(@ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                      @ApiParam(value = "老师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = false) String endDate,
                                                      @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(attendanceStatisticsService.exportClassAttendanceByTeacher(account.getOrganId(), null, collegeId, teacherName, beginDate, endDate),
                HttpStatus.OK);
    }

    /**
     * 教学班考勤 按课程节
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/attendance/attendanceByPeriod", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教学班考勤 按课程节", response = Void.class, notes = "教学班考勤 按课程节<br>@author meihua.li")
    public ResponseEntity<?> attendanceByPeriod(@ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                @ApiParam(value = "老师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = true) String beginDate,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = true) String endDate,
                                                @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(attendanceStatisticsService.attendanceByPeriod(pageNumber, pageSize, account.getOrganId(), teacherName, courseName, beginDate, endDate),
                HttpStatus.OK);

    }

    @RequestMapping(value = "/attendance/exportClassAttendanceByPeriod", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出教学班考勤 按课程节", response = Void.class, notes = "导出教学班考勤 按课程节 <b>@author meihua.li</b>")
    public ResponseEntity<?> exportAttendanceByPeriod(@ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,
                                                      @ApiParam(value = "老师名称") @RequestParam(value = "teacherName", required = false) String teacherName,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = false) String endDate,
                                                      @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(attendanceStatisticsService.exportClassAttendanceByPeriod(account.getOrganId(), teacherName, courseName, beginDate, endDate),
                HttpStatus.OK);
    }

    /**
     * 行政班考勤 按班级
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/attendance/classAttendanceGroupByclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "行政班考勤 按班级", response = Void.class, notes = "行政班考勤 按班级<br>@author meihua.li")
    public ResponseEntity<?> classAttendanceGroupByclass(@ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                         @ApiParam(value = "专业ID") @RequestParam(value = "proId", required = false) Long proId,
                                                         @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = false) Long classId,
                                                         @ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = true) String beginDate,
                                                         @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = true) String endDate,
                                                         @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                         @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<String> userRoles = orgManagerRemoteService.getUserRoles(account.getId());
        if (isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(account.getId());
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                collegeId = user.getLong("collegeId");
            }
        }
        return new ResponseEntity<>(
                attendanceStatisticsService.classAttendanceGroupByclass(pageNumber, pageSize, account.getOrganId(), grade, collegeId, proId, classId, beginDate, endDate),
                HttpStatus.OK);

    }

    @RequestMapping(value = "/attendance/exportClassAttendanceGroupByclass", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出政班考勤 按班级", response = Void.class, notes = "导出政班考勤 按班级 <b>@author meihua.li</b>")
    public ResponseEntity<?> exportClassAttendanceGroupByCollege(@ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                                 @ApiParam(value = "专业ID") @RequestParam(value = "proId", required = false) Long proId,
                                                                 @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = false) Long classId,
                                                                 @ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = false) String endDate,
                                                                 @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(attendanceStatisticsService.exportClassAttendanceGroupByclass(account.getOrganId(), grade, collegeId, proId, classId, beginDate, endDate),
                HttpStatus.OK);
    }

    /**
     * 行政班考勤 按专业
     */
    @RequestMapping(value = "/attendance/classAttendanceGroupByPro", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "行政班考勤 按专业", response = Void.class, notes = "行政班考勤 按专业<br>@author meihua.li")
    public ResponseEntity<?> classAttendanceGroupByPro(@ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                       @ApiParam(value = "专业ID") @RequestParam(value = "majorId", required = false) Long majorId,
                                                       @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId") Long managerId,
                                                       @ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = true) String beginDate,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = true) String endDate,
                                                       @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                       @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<String> userRoles = orgManagerRemoteService.getUserRoles(account.getId());
        if (isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(account.getId());
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                collegeId = user.getLong("collegeId");
            }
        }
        return new ResponseEntity<>(
                attendanceStatisticsService.classAttendanceGroupByPro(pageNumber, pageSize, account.getOrganId(), grade, collegeId, majorId, beginDate, endDate), HttpStatus.OK);

    }

    @RequestMapping(value = "/attendance/exportClassAttendanceGroupByPro", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出考勤  专业", response = Void.class, notes = "导出考勤  专业 <b>@author meihua.li</b>")
    public ResponseEntity<?> exportClassAttendanceGroupByCollege(@ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                                 @ApiParam(value = "学院ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                                 @ApiParam(value = "专业ID") @RequestParam(value = "proId", required = false) Long proId,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = false) String endDate,
                                                                 @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(attendanceStatisticsService.exportClassAttendanceGroupByPro(account.getOrganId(), grade, collegeId, proId, beginDate, endDate),
                HttpStatus.OK);
    }

    /**
     * 教学班考勤 按学院
     *
     * @throws DlEduException
     */
    @RequestMapping(value = "/attendance/classAttendanceGroupByCollege", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教学班考勤 按学院", response = Void.class, notes = "教学班考勤 按学院<br>@author meihua.li")
    public ResponseEntity<?> classAttendanceGroupByCollege(@ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                           @ApiParam(value = "年级") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = true) String beginDate,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = true) String endDate,
                                                           @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                           @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<String> userRoles = orgManagerRemoteService.getUserRoles(account.getId());
        if (isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(account.getId());
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                collegeId = user.getLong("collegeId");
            }
        }
        return new ResponseEntity<>(attendanceStatisticsService.classAttendanceGroupByCollege(pageNumber, pageSize, account.getOrganId(), grade, collegeId, beginDate, endDate),
                HttpStatus.OK);

    }

    @RequestMapping(value = "/attendance/exportClassAttendanceGroupByCollege", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "导出学院考勤", response = Void.class, notes = "导出学院考勤 <b>@author meihua.li</b>")
    public ResponseEntity<?> exportClassAttendanceGroupByCollege(@ApiParam(value = "年级") @RequestParam(value = "grade", required = false) String grade,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "起始时间") @RequestParam(value = "beginDate", required = false) String beginDate,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "截至时间") @RequestParam(value = "endDate", required = false) String endDate,
                                                                 @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(attendanceStatisticsService.exportClassAttendanceGroupByCollege(account.getOrganId(), grade, null, beginDate, endDate), HttpStatus.OK);
    }

    /**
     * 按条件搜索学生考勤
     *
     * @param orgId
     * @param criteria
     * @param startTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/attendancerecor/searchbywhere", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "按条件搜索学生考勤。", httpMethod = "GET", response = Void.class, notes = "按条件搜索学生考勤。<br><br>@author jianwei.wu</b>")
    public PageData<AttendanceRecordDTO> searchForStudentAttendance(
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
            @ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "criteria 姓名/学号") @RequestParam(value = "criteria", required = false) String criteria,
            @ApiParam(value = "startTime 开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = false) String endTime,

            @ApiParam(value = "教学班名称") @RequestParam(value = "teachingClassName", required = false) String teachingClassName,
            @ApiParam(value = "教师姓名") @RequestParam(value = "teacherName", required = false) String teacherName,
            @ApiParam(value = "课程名称") @RequestParam(value = "courseName", required = false) String courseName,

            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                if (null != user) {
                    collegeId = user.getLong("collegeId");
                } else {
                    return null;
                }
            }
        }
        return attendanceRecordService.searchAttendance(orgId, collegeId, criteria, startTime, endTime, teachingClassName, teacherName, courseName, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    /**
     * 考勤的修改
     *
     * @param rollcallId
     * @return
     */
    @PutMapping(value = "/attendancerecor/modifyattendance", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改考勤。", httpMethod = "PUT", response = Void.class, notes = "修改考勤。<br><br>@author jianwei.wu</b>")
    public Map<String, Object> modifyAttendance(@ApiParam(value = "rollcallId 签到id", required = true) @RequestParam(value = "rollcallId") Long rollcallId,
                                                @ApiParam(value = "type 考勤状态(1:已到；2:旷课；3:迟到；4:请假；5:早退)", required = true) @RequestParam(value = "type") String type,
                                                @ApiParam(value = "operator 操作员", required = true) @RequestParam(value = "operator") String operator,
                                                @ApiParam(value = "operatorId 操作员id", required = true) @RequestParam(value = "operatorId") Long operatorId) {
        return attendanceRecordService.modifyAttendance(rollcallId, type, operator, operatorId);
    }

    /**
     * 批量考勤的修改
     *
     * @param rollcallIds
     * @return
     */
    @PutMapping(value = "/attendancerecor/modifyattendanceBatch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量考勤的修改。", httpMethod = "PUT", response = Void.class, notes = "批量考勤的修改。<br><br>@author hsh</b>")
    public Map<String, Object> modifyattendanceBatch(@ApiParam(value = "rollcallId 签到id", required = true) @RequestParam(value = "rollcallIds") Set<Long> rollcallIds,
                                                     @ApiParam(value = "type 考勤状态(1:已到；2:旷课；3:迟到；4:请假；5:早退)", required = true) @RequestParam(value = "type") String type,
                                                     @ApiParam(value = "operator 操作员", required = true) @RequestParam(value = "operator") String operator,
                                                     @ApiParam(value = "operatorId 操作员id", required = true) @RequestParam(value = "operatorId") Long operatorId) {
        return attendanceRecordService.modifyattendanceBatch(rollcallIds, type, operator, operatorId);
    }

    /**
     * 查看考勤修改记录
     *
     * @param rollcallId
     * @return
     */
    @GetMapping(value = "/attendancerecor/viewlog", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查看考勤修改记录。", httpMethod = "GET", response = Void.class, notes = "查看考勤修改记录。<br><br>@author jianwei.wu</b>")
    public Map<String, Object> viewLog(@ApiParam(value = "rollcallId 签到id", required = true) @RequestParam(value = "rollcallId") Long rollcallId) {
        return attendanceRecordService.viewLog(rollcallId);
    }

    /**
     * 辅导员点名记录
     *
     * @param orgId
     * @param collegeId
     * @param nj
     * @param status
     * @param startDate
     * @param endDate
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/instructor/rollcall", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "辅导员点名记录", httpMethod = "GET", response = Void.class, notes = "辅导员点名记录 <br><br>@author jianwei.wu</b>")
    public PageData<RollcallStatisticsDTO> getRollcallStatistics(@ApiParam(value = "orgId 学校id", required = true) @RequestParam(value = "orgId") Long orgId,
                                                                 @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
                                                                 @ApiParam(value = "collegeId 学院id") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                                                 @ApiParam(value = "nj 姓名/工号") @RequestParam(value = "nj", required = false) String nj,
                                                                 @ApiParam(value = "status 状态：1：已点名；0：未点名", required = true) @RequestParam(value = "status") String status,
                                                                 @ApiParam(value = "startDate 开始日期", required = true) @RequestParam(value = "startDate") String startDate,
                                                                 @ApiParam(value = "endDate 结束日期", required = true) @RequestParam(value = "endDate") String endDate,
                                                                 @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                 @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = orgManagerRemoteService.getUserRoles(managerId);
        if (isCollegeManager(userRoles)) {
            String userInfo = orgManagerRemoteService.getUserInfo(managerId);
            if (null != userInfo) {
                JSONObject user = JSONObject.fromObject(userInfo);
                collegeId = user.getLong("collegeId");
            }
        }
        return attendanceRecordService.getRollcallStatistics(PageUtil.createNoErrorPageRequest(pageNumber, pageSize), nj, orgId, collegeId, status, startDate, endDate);
    }

    @GetMapping(value = "/rollcall/classdetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "辅导员点名班级详情", httpMethod = "GET", response = Void.class, notes = "辅导员点名班级详情 <br><br>@author jianwei.wu</b>")
    public Map<String, Object> getClassdetailst(@ApiParam(value = "rid 辅导员发起的点名id", required = true) @RequestParam(value = "rid") Long rid,
                                                @ApiParam(value = "classId 行政班id", required = true) @RequestParam(value = "classId") Long classId) {
        return attendanceRecordService.getClassdetailst(classId, rid);
    }

    // 判断是否是院级管理 （权限数筛选时使用）
    public static boolean isCollegeManager(List<String> roles) {
        if (roles.contains("ROLE_COLLEGE_ADMIN")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_DATAVIEW")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_EDUCATIONALMANAGER")) {
            return true;
        } else {
            return false;
        }
    }

}
