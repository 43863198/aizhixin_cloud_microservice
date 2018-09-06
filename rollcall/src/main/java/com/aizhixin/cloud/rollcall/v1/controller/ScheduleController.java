package com.aizhixin.cloud.rollcall.v1.controller;


import com.aizhixin.cloud.rollcall.service.MyCacheService;
import com.aizhixin.cloud.rollcall.service.ScheduleRollCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/schedule")
@Api(description = "当天课程操作API")
public class ScheduleController {
    private MyCacheService myCacheService;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    @Autowired
    public ScheduleController(MyCacheService myCacheService) {
        this.myCacheService = myCacheService;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "停止课堂", response = Void.class, notes = "停止课堂<br><br><b>@author zhen.pan</b>")
    public void stop(
            @ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
            @ApiParam(value = "课堂ID") @RequestParam("scheduleId") Long scheduleId) {
        myCacheService.stopSchedule(scheduleId, orgId);
    }

    @RequestMapping(value = "/add", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "添加课堂", response = Void.class, notes = "添加课堂<br><br><b>@author zhen.pan</b>")
    public void add(
            @ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
            @ApiParam(value = "课堂ID") @RequestParam("scheduleId") Long scheduleId) {
        myCacheService.addSchedule(scheduleId);
    }

    @RequestMapping(value = "/declarecourse", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "老师认领课程", response = Void.class, notes = "老师认领课程<br><br><b>@author zhen.pan</b>")
    public void declareCourse(
            @ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
            @ApiParam(value = "排课ID") @RequestParam("scheduleId") Long scheduleId,
            @ApiParam(value = "源老师ID") @RequestParam("srcTeacherId") Long srcTeacherId,
            @ApiParam(value = "目标老师ID") @RequestParam("destTeacherId") Long destTeacherId,
            @ApiParam(value = "目标老师姓名") @RequestParam("destTeacherName") String destTeacherName) {
        myCacheService.updateCacheCourseTeacher(orgId, scheduleId, srcTeacherId, destTeacherId, destTeacherName);
    }

    @RequestMapping(value = "/teacher/getScheduleRollCallAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取当节课程的到课率", response = Void.class, notes = "获取当节课程的到课率<br><br><b>@author meihua</b>")
    public String currentDayTeacherCourseList(
            @ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
            @ApiParam(value = "课堂规则ID") @RequestParam("ruleId") Long ruleId) {
        return scheduleRollCallService.getCurrentScheduleRollCallAttendance(orgId, ruleId);
    }

//    @RequestMapping(value = "/student/currentDayCourseList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "GET", value = "学生当天课程列表", response = Void.class, notes = "学生当天课程列表<br><br><b>@author meihua</b>")
//    public List <ScheduleRedisDomain> currentDayStudentCourseList(
//            @ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
//            @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
//        return scheduleRollCallService.currentDayStudentCourseList(orgId, studentId);
//    }
}
