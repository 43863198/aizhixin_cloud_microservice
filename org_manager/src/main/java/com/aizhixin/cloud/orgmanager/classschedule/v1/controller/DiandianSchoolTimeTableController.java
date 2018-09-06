package com.aizhixin.cloud.orgmanager.classschedule.v1.controller;

import com.aizhixin.cloud.orgmanager.classschedule.domain.*;
import com.aizhixin.cloud.orgmanager.classschedule.service.DiandianSchoolTimeTableService;
import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.company.domain.StudentDomain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by zhen.pan on 2017/5/10.
 */
@RestController
@RequestMapping("/v1/ddschooltimetable")
@Api(description = "点点课表相关API")
public class DiandianSchoolTimeTableController {
    private DiandianSchoolTimeTableService diandianSchoolTimeTableService;

    @Autowired
    public DiandianSchoolTimeTableController(DiandianSchoolTimeTableService diandianSchoolTimeTableService) {
        this.diandianSchoolTimeTableService = diandianSchoolTimeTableService;
    }

    @RequestMapping(value = "/schoolday", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取特定学校特定学期某一天的排课信息", response = Void.class, notes = "获取特定学校特定学期某一天的排课信息<br><br><b>@author zhen.pan</b>")
    public List<DianDianSchoolTimeDomain> getSchoolTime(
            @ApiParam(value = "orgId 学校ID") @RequestParam(value = "orgId", required = false) Long orgId,
            @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
            @ApiParam(value = "teachDate 日期(yyyy-MM-dd)，不填写当天") @RequestParam(value = "teachDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date teachDate) {
        return diandianSchoolTimeTableService.findSchoolTimeDay(orgId, semesterId, teachDate);
    }

    @RequestMapping(value = "/studentweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取特定学生、学周的课表信息", response = Void.class, notes = "获取特定学生、学周的课表信息<br><br><b>@author zhen.pan</b>")
    public List<DianDianWeekSchoolTimeTableDomain> getStudentWeekSchoolTimeTable(
            @ApiParam(value = "weekId 学周ID", required = true) @RequestParam(value = "weekId") Long weekId,
            @ApiParam(value = "studentId 学生ID", required = true) @RequestParam(value = "studentId") Long studentId) {
        return diandianSchoolTimeTableService.findStudentWeekSchoolTimeTable(weekId, studentId);
    }

    @RequestMapping(value = "/teacherweek", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取特定老师、学周的课表信息", response = Void.class, notes = "获取特定老师、学周的课表信息<br><br><b>@author zhen.pan</b>")
    public List<DianDianWeekSchoolTimeTableDomain> getTeacherWeekSchoolTimeTable(
            @ApiParam(value = "weekId 学周ID", required = true) @RequestParam(value = "weekId") Long weekId,
            @ApiParam(value = "teacherId 老师ID", required = true) @RequestParam(value = "teacherId") Long teacherId) {
        return diandianSchoolTimeTableService.findTeacherWeekSchoolTimeTable(weekId, teacherId);
    }

    @RequestMapping(value = "/studentday", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取特定学生某一天的排课信息", response = Void.class, notes = "获取特定学生某一天的排课信息<br><br><b>@author zhen.pan</b>")
    public List<DianDianDaySchoolTimeTableDomain> getStudentDaySchoolTimeTable(
            @ApiParam(value = "studentId 学生ID", required = true) @RequestParam(value = "studentId") Long studentId,
            @ApiParam(value = "date 日期(yyyy-MM-dd)，不填写是当天") @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return diandianSchoolTimeTableService.findStudentDaySchoolTimeTable(studentId, date);
    }

    @RequestMapping(value = "/dayteachingclassid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取特定学生某一天的教学班ID列表", response = Void.class, notes = "获取特定学生某一天的教学班ID列表<br><br><b>@author zhen.pan</b>")
    public Set<Long> getStudentDayTeachingClassId(
            @ApiParam(value = "studentId 学生ID", required = true) @RequestParam(value = "studentId") Long studentId,
            @ApiParam(value = "date 日期(yyyy-MM-dd)，不填写是当天") @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return diandianSchoolTimeTableService.findStudentDayTeachingClassId(studentId, date);
    }

    @RequestMapping(value = "/getstudentsbyteacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取老师所代课及所带班级的学生", response = Void.class, notes = "获取老师所代课及所带班级的学生<br><br><b>@author zhen.pan</b>")
    public List<StudentDomain> getStudentByTeacher(
            @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
            @ApiParam(value = "teacherId 老师ID", required = true) @RequestParam(value = "teacherId") Long teacherId,
            @ApiParam(value = "name 学生姓名", required = true) @RequestParam(value = "name") String name) {
        return diandianSchoolTimeTableService.findAllStudentByTeacher(teacherId, semesterId, name);
    }

    @RequestMapping(value = "/addtempcourseschedule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "增加临时调课，可以增加、停止、调停", response = Void.class, notes = "增加临时调课，可以增加、停止、调停<br><br><b>@author zhen.pan</b>")
    public Long addTempAdjustCourseSchedule(@ApiParam(value = "临时调课基本信息") @RequestBody TempAdjustCourseFullDomain tempAdjustCourseFullDomain) {
        return diandianSchoolTimeTableService.addTempAdjustCourseSchedule(tempAdjustCourseFullDomain);
    }

    @RequestMapping(value = "/canceltempcourseschedule", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "取消之前临时增加、停止、调停的课程", response = Void.class, notes = "取消之前临时增加、停止、调停的课程<br><br><b>@author zhen.pan</b>")
    public void cancelTempAdjustCourseSchedule(@ApiParam(value = "adjustCourseId 临时调停课ID") @RequestParam(value = "adjustCourseId") Long adjustCourseId) {
        diandianSchoolTimeTableService.cancelTempAdjustCourseSchedule(adjustCourseId);
    }

    @RequestMapping(value = "/listtempadjustcourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "调停课历史记录查询列表", response = Void.class, notes = "调停课历史记录查询列表<br><br><b>@author zhen.pan</b>")
    public PageData<TempAdjustCourseListDomain> listTempAdjustCourse(
            @ApiParam(value = "orgId 学期ID") @RequestParam(value = "orgId", required = false) Long orgId,
            @ApiParam(value = "semesterId 学期ID") @RequestParam(value = "semesterId", required = false) Long semesterId,
            @ApiParam(value = "teacher 老师") @RequestParam(value = "teacher", required = false) String teacher,
            @ApiParam(value = "adjustType 调课类型:10增加,20停止,30调整") @RequestParam(value = "adjustType", required = false) Integer adjustType,
            @ApiParam(value = "startDate 开始日期") @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @ApiParam(value = "endDate 结束日期") @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return diandianSchoolTimeTableService.listTempAdjustCourse(orgId, semesterId, teacher, adjustType, startDate, endDate, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }
}
