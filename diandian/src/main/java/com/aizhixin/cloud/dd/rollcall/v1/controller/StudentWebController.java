package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.JdbcTemplate.*;
import com.aizhixin.cloud.dd.rollcall.dto.*;
import com.aizhixin.cloud.dd.rollcall.service.CourseService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.RollCallService;
import com.aizhixin.cloud.dd.rollcall.service.SemesterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by meihua.li on 2017/7/18.
 */
@RestController
@RequestMapping("/api/web/v1/")
@Api(value = "学生API", description = "针对学生的相关API")
public class StudentWebController {

    private final Logger log = LoggerFactory.getLogger(StudentWebController.class);

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private CourseService courseService;

    @Lazy
    @Autowired
    private RollCallService rollCallService;

    @Autowired
    private StudentAttemdanceDetailQueryJdbcTemplate studentAttemdanceDetailQueryJdbcTemplate;

    @Autowired
    private StudentAttendanceGatherQueryPaginationSQL studentAttendanceGatherQueryPaginationSQL;

    @Autowired
    private EvaluationDetailQueryJdbcTemplate evaluationDetailQueryJdbcTemplate;

    @Autowired
    private EvaluationCountQueryJdbcTemplate evaluationCountQueryJdbcTemplate;


    @RequestMapping(value = "/student/getCoursePullDownList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生课程列表", response = Void.class, notes = "获取学生课程列表<br>@author LIMH")
    public ResponseEntity<?> getCoursePullDownList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Long semesterId = semesterService.getSemesterId(account.getOrganId());
        if (semesterId == null) {
            return new ResponseEntity(null, HttpStatus.OK);
        }
        return new ResponseEntity(courseService.getCourseByStudentId(account.getId(), semesterId), HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getstudentForPersonal", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "个人中心学生考勤查询", response = Void.class, notes = "个人中心学生考勤查询<br>@author LIMH")
    public ResponseEntity<?> getStudentAttendanceForPersonal(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Long semesterId = semesterService.getSemesterId(account.getOrganId());
        PersonalAttendanceDTO personalAttendance = null;
        if (semesterId != null) {
            personalAttendance = rollCallService.getPersonalAttendanceForStudent(account.getId(), semesterId);
        }
        if (null == personalAttendance) {
            personalAttendance = new PersonalAttendanceDTO();
        }
        return new ResponseEntity(personalAttendance, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/attendanceDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查看考勤明细", response = Void.class, notes = "学生查看考勤明细<br>@author LIMH")
    public ResponseEntity<?> exportAttendancDetail(
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = true) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = true) Integer limit,
            @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = true) Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = true) Date endTime,
            @ApiParam(value = "type 状态") @RequestParam(value = "type", required = false) String type,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        Set<Long> courseIds = new HashSet();
        if (null == courseId) {
            Long semesterId = semesterService.getSemesterId(account.getOrganId());
            if (null == semesterId) {
                return new ResponseEntity(null, HttpStatus.OK);
            }
            List<CoursePullDownListDTO> list = courseService.getCourseByStudentId(account.getId(), semesterId);
            if (null == list) {
                return new ResponseEntity(null, HttpStatus.OK);
            }
            for (CoursePullDownListDTO dto : list) {
                courseIds.add(dto.getCourseId());
            }
        } else {
            courseIds.add(courseId);
        }
        PageInfo page = null;
        try {
            page = studentAttemdanceDetailQueryJdbcTemplate.getPageInfo(
                    limit,
                    offset,
                    StudentAttemdanceDetailQueryJdbcTemplate.organMapper,
                    null,
                    new StudentAttendanceDetailQueryPaginationSQL(courseIds, beginTime == null ? null : sdf
                            .format(beginTime), endTime == null ? null : edf.format(endTime), type, account.getId()));
        } catch (Exception e) {
            log.warn("Exception", e);
            log.warn("导出考勤", e);
        }


        return new ResponseEntity(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/attendanceGather", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查看考勤汇总", response = Void.class, notes = "学生查看考勤汇总<br>@author LIMH")
    public ResponseEntity<?> exportAttendancGather(
            @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = true) Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = true) Date endTime,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

        Set<Long> courseIds = new HashSet();
        if (null == courseId) {
            Long semesterId = semesterService.getSemesterId(account.getOrganId());
            if (null == semesterId) {
                return new ResponseEntity(null, HttpStatus.OK);
            }
            List<CoursePullDownListDTO> list = courseService.getCourseByStudentId(account.getId(), semesterId);
            if (null == list) {
                return new ResponseEntity(null, HttpStatus.OK);
            }
            for (CoursePullDownListDTO dto : list) {
                courseIds.add(dto.getCourseId());
            }
        } else {
            courseIds.add(courseId);
        }
        List<StudentAttendanceGatherDTO> list = studentAttendanceGatherQueryPaginationSQL.query(account.getId(),
                courseIds, beginTime == null ? null : sdf.format(beginTime),
                endTime == null ? null : edf.format(endTime));
        if (null == list) {
            for (StudentAttendanceGatherDTO studentAttendanceGatherDTO : list) {
                studentAttendanceGatherDTO.setPersonId(account.getPersonId());
                studentAttendanceGatherDTO.setStudentName(account.getName());
            }
        }
        return new ResponseEntity(list, HttpStatus.OK);
    }


    @RequestMapping(value = "/student/EvaluationDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查看评教详细", response = Void.class, notes = "学生查看评教详细<br>@author DuanWei")
    public ResponseEntity<?> EvaluationDetail(
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = true) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = true) Integer limit,
            @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = false) Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        PageInfo page = null;
        try {
            page = evaluationDetailQueryJdbcTemplate.EvaluationDetail(limit, offset,
                    EvaluationDetailQueryJdbcTemplate.rowMapper, null, new EvaluationDetailQueryPaginationSQL(
                            beginTime == null ? null : sdf.format(beginTime), endTime == null ? null : edf.format(endTime),
                            account.getId(), courseId));
        } catch (Exception e) {
            log.warn("Exception", e);
        }

        return new ResponseEntity(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/EvaluationCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查看评教统计", response = Void.class, notes = "学生查看评教统计<br>@author DuanWei")
    public ResponseEntity<?> EvaluationCount(
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = true) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = true) Integer limit,
            @ApiParam(value = "courseId 课程id") @RequestParam(value = "courseId", required = false) Long courseId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "beginTime 开始时间") @RequestParam(value = "beginTime", required = true) Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = true) Date endTime,
            @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        PageInfo page = null;
        try {
            page = evaluationCountQueryJdbcTemplate.getEvaluationCount(limit, offset,
                    EvaluationCountQueryJdbcTemplate.rowMapper, null, new EvaluationCountQueryPaginationSQL(
                            beginTime == null ? null : sdf.format(beginTime), endTime == null ? null : edf.format(endTime),
                            account.getId(), courseId));
        } catch (Exception e) {
            log.warn("Exception", e);
        }
        return new ResponseEntity(page, HttpStatus.OK);
    }
}
