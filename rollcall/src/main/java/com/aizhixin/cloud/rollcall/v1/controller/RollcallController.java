package com.aizhixin.cloud.rollcall.v1.controller;

import com.aizhixin.cloud.rollcall.domain.CurrentScheduleRulerDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRollCallRedisDomain;
import com.aizhixin.cloud.rollcall.domain.SignInDomain;
import com.aizhixin.cloud.rollcall.domain.StudentInClassScheduleRedisDomain;
import com.aizhixin.cloud.rollcall.entity.RollCall;
import com.aizhixin.cloud.rollcall.service.MyCacheService;
import com.aizhixin.cloud.rollcall.service.RollCallService;
import com.aizhixin.cloud.rollcall.service.ScheduleRollCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/v1/rollcall")
@Api(description = "签到操作API")
public class RollcallController {
    private MyCacheService myCacheService;

    @Autowired
    private ScheduleRollCallService scheduleRollCallService;

    @Autowired
    private RollCallService rollCallService;

    @Autowired
    public RollcallController(MyCacheService myCacheService) {
        this.myCacheService = myCacheService;
    }

    @RequestMapping(value = "/schedules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生当前正在课堂中的课堂ID和课堂规则ID", response = Void.class, notes = "获取学生当前正在课堂中的课堂ID和课堂规则ID<br><br><b>@author zhen.pan</b>")
    public List<StudentInClassScheduleRedisDomain> getStudentScheduleAndRollcall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getStudentCurrentInClass(orgId, studentId);
    }

    @RequestMapping(value = "/ruler", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生当前正在课堂的签到规则列表", response = Void.class, notes = "获取学生当前正在课堂的签到规则列表<br><br><b>@author zhen.pan</b>")
    public List<CurrentScheduleRulerDomain> getStudentRuller(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getStudentCurrentRulerInClass(orgId, studentId);
    }

    @RequestMapping(value = "/open", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "打开随堂点", response = Void.class, notes = "打开随堂点<br><br><b>@author zhen.pan</b>")
    public void open(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "规则ID") @RequestParam("scheduleId") Long scheduleId,
        @ApiParam(value = "签到规则ID") @RequestParam("rulerId") Long rulerId,
        @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填") @RequestParam("rollCallType") String rollCallType, @RequestParam("authCode") String authCode) {
        myCacheService.open(orgId, scheduleId, rulerId, rollCallType, authCode);
    }

    @RequestMapping(value = "/close", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "关闭随堂点", response = Void.class, notes = "关闭随堂点<br><br><b>@author zhen.pan</b>")
    public void close(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "签到规则ID") @RequestParam("rulerId") Long rulerId) {
        myCacheService.close(orgId, rulerId);
    }

    @RequestMapping(value = "/updateruler", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改签到规则", response = Void.class, notes = "修改签到规则<br><br><b>@author zhen.pan</b>")
    public void upadteRuler(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "老师ID") @RequestParam("teacherId") Long teacherId,
        @ApiParam(value = "课程ID") @RequestParam("courseId") Long courseId,
        @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填") @RequestParam(value = "rollCallType") String rollCallType,
        @ApiParam(value = "lateTime 迟到时间 必填") @RequestParam(value = "lateTime") int lateTime,
        @ApiParam(value = "isOpen 是否开启[disable:关闭,enable:开启] 必填") @RequestParam(value = "isOpen") String isOpen, @RequestParam(value = "authCode") String authCode) {
        myCacheService.updateRuler(orgId, teacherId, courseId, rollCallType, lateTime, isOpen, authCode);
    }

    @RequestMapping(value = "/studentleave", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "上课期间学生请假，修改缓存考勤数据", response = Void.class, notes = "上课期间学生请假，修改缓存考勤数据<br><br><b>@author zhen.pan</b>")
    public void setStudentLeave(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂ID") @RequestParam("scheduleId") Long scheduleId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        myCacheService.setStudentLeave(orgId, scheduleId, studentId);
    }

    // 批量修改考勤
    @RequestMapping(value = "/updateRollcall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "批量修改考勤", response = Void.class, notes = "批量修改考勤<br><br><b>@author zhen.pan</b>")
    public void updateRollcall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂规则ID") @RequestParam("ruleId") Long ruleId,
        @ApiParam(value = "学生ID") @RequestParam("studentIds") Set<String> studentIds, @ApiParam(value = "考勤状态") @RequestParam("type") String type) {
        scheduleRollCallService.updateRollCall(orgId, ruleId, studentIds, type);
    }

    @RequestMapping(value = "/student/currentDayStudentSignType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生当天签到状态", response = Void.class, notes = "获取学生当天签到状态<br><br><b>@author meihua</b>")
    public Map currentDayStudentSignType(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getCurrentDayStudentSignType(orgId, studentId);
    }

    @RequestMapping(value = "/student/currentDayStudentCourseList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生当天课程列表", response = Void.class, notes = "获取学生当天课程列表<br><br><b>@author meihua</b>")
    public List currentDayStudentCourseList(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getCurrentDayStudentCourseList(orgId, studentId);
    }

    @RequestMapping(value = "/student/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "学生签到", response = Void.class, notes = "学生签到<br><br><b>@author meihua</b>")
    public Map signin(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId,
        @RequestBody SignInDomain signInDomain) {
        return rollCallService.signIn(orgId, studentId, signInDomain);
    }

    @RequestMapping(value = "/student/getScheduleRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "课堂规则信息", response = Void.class, notes = "课堂规则信息<br><br><b>@author meihua</b>")
    public ScheduleRollCallRedisDomain getScheduleRollCall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "scheduleId") @RequestParam("scheduleId") Long scheduleId) {
        return rollCallService.getScheduleRollcall(orgId, scheduleId);
    }

    @RequestMapping(value = "/student/listRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "课堂学生签到信息", response = Void.class, notes = "课堂学生签到信息<br><br><b>@author meihua</b>")
    public List<RollCall> listRollCall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "scheduleId") @RequestParam("scheduleId") Long scheduleId) {
        return rollCallService.listRollCall(orgId, scheduleId);
    }
}
