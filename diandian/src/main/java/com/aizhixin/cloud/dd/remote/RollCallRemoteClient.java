package com.aizhixin.cloud.dd.remote;

import com.aizhixin.cloud.dd.rollcall.domain.CurrentScheduleRulerDomain;
import com.aizhixin.cloud.dd.rollcall.domain.ScheduleInClassRedisDomain;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.entity.RollCall;
import com.aizhixin.cloud.dd.rollcallv2.domain.RollcallRedisDomain;
import com.aizhixin.cloud.dd.rollcallv2.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.dd.rollcallv2.domain.ScheduleRollCallRedisDomain;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LIMH on 2017/11/10.
 */
//, url="http://gateway.aizhixintest.com:80/rollcall"
//@FeignClient(name = "rollcall")
public interface RollCallRemoteClient {

    /**
     * 获取学生当前正在课堂中的课堂ID和课堂规则IDs
     */
    @RequestMapping(value = "/v1/rollcall/ruler", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<CurrentScheduleRulerDomain> getSignCourseRuler(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "studentId") Long studentId);

    /**
     * 获取学生当前正在课堂中的课堂ID和课堂规则IDs
     */
    @RequestMapping(value = "/v1/rollcall/schedules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<ScheduleInClassRedisDomain> getSignCourse(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "studentId") Long studentId);

    /**
     * 获取学生当课堂签到状态
     */
    @RequestMapping(value = "/v1/rollcall/student/listRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<RollCall> listRollCall(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "scheduleId") Long scheduleId);

    /**
     * 停止课程
     */
    @RequestMapping(value = "/v1/schedule/stop", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void stopSchedule(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @RequestParam("scheduleId") Long scheduleId);

    /**
     * 新增课程
     */
    @RequestMapping(value = "/v1/schedule/add", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void addSchedule(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @RequestParam("scheduleId") Long scheduleId);

    /**
     * 学生签到
     *
     * @param orgId
     * @param studentId
     * @return
     */
    @RequestMapping(value = "/v1/rollcall/student/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    Map signin(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId, SignInDTO signInDomain);

    /**
     * 当天课程列表
     *
     * @param orgId
     * @param studentId
     * @return
     */
    @RequestMapping(value = "/v1/rollcall/student/currentDayStudentCourseList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<ScheduleRedisDomain> currentDayStudentCourseList(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId);

    @RequestMapping(value = "/v1/rollcall/student/getScheduleRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ScheduleRollCallRedisDomain getScheduleRollCall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "课程ID") @RequestParam("scheduleId") Long scheduleId);

    /**
     * 打开随堂点
     *
     * @param orgId
     * @param rulerId
     * @param rollCallType
     */
    @RequestMapping(value = "/v1/rollcall/open", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void open(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "规则ID") @RequestParam("scheduleId") Long scheduleId,
        @ApiParam(value = "签到规则ID") @RequestParam("rulerId") Long rulerId,
        @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填") @RequestParam("rollCallType") String rollCallType, @RequestParam("authCode") String authCode);

    /**
     * 关闭随堂点
     *
     * @param orgId
     * @param rulerId
     */
    @RequestMapping(value = "/v1/rollcall/close", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void close(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "签到规则ID") @RequestParam("rulerId") Long rulerId);

    /**
     * 修改签到规则
     *
     * @param orgId
     * @param teacherId
     * @param courseId
     * @param rollCallType
     * @param lateTime
     * @param isOpen
     */
    @RequestMapping(value = "/v1/rollcall/updateruler", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void upadteRuler(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "老师ID") @RequestParam("teacherId") Long teacherId,
        @ApiParam(value = "课程ID") @RequestParam("courseId") Long courseId,
        @ApiParam(value = "rollCallType 点名类型[automatic,digital] 必填") @RequestParam(value = "rollCallType") String rollCallType,
        @ApiParam(value = "lateTime 迟到时间 必填") @RequestParam(value = "lateTime") int lateTime,
        @ApiParam(value = "isOpen 是否开启[disable:关闭,enable:开启] 必填") @RequestParam(value = "isOpen") String isOpen, @RequestParam(value = "authCode") String authCode);

    /**
     * 上课期间学生请假，修改缓存考勤数据
     *
     * @param orgId
     * @param scheduleId
     * @param studentId
     */
    @RequestMapping(value = "/v1/rollcall/studentleave", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void setStudentLeave(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂ID") @RequestParam("scheduleId") Long scheduleId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId);

    /**
     * 老师认领课程
     *
     * @param orgId
     * @param scheduleId
     * @param srcTeacherId
     * @param destTeacherId
     * @param destTeacherName
     */
    @RequestMapping(value = "/v1/schedule/declarecourse", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void declareCourse(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "排课ID") @RequestParam("scheduleId") Long scheduleId,
        @ApiParam(value = "源老师ID") @RequestParam("srcTeacherId") Long srcTeacherId, @ApiParam(value = "目标老师ID") @RequestParam("destTeacherId") Long destTeacherId,
        @ApiParam(value = "目标老师姓名") @RequestParam("destTeacherName") String destTeacherName);

    /**
     * 批量修改考勤
     *
     * @param orgId
     * @param ruleId
     * @param studentIds
     * @param type
     */
    @RequestMapping(value = "/v1/rollcall/updateRollcall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    void updateRollcall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂规则ID") @RequestParam("ruleId") Long ruleId,
        @ApiParam(value = "学生ID") @RequestParam("studentIds") Set<String> studentIds, @ApiParam(value = "考勤状态") @RequestParam("type") String type);

    /**
     * 获取学生当天签到状态
     * 
     * @param orgId
     * @param studentId
     * @return
     */
    @RequestMapping(value = "/v1/rollcall/student/currentDayStudentSignType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生当天签到状态", response = Void.class, notes = "获取学生当天签到状态<br><br><b>@author meihua</b>")
    Map<String, RollcallRedisDomain> currentDayStudentSignType(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId);

    /**
     * 获取当节课程的到课率
     *
     * @param orgId
     * @param ruleId
     * @return
     */
    @RequestMapping(value = "/v1/schedule/teacher/getScheduleRollCallAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    String currentDayTeacherCourseList(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂规则ID") @RequestParam("ruleId") Long ruleId);
}
