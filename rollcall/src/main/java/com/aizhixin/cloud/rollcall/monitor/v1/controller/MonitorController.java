package com.aizhixin.cloud.rollcall.monitor.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.rollcall.monitor.service.*;

import io.swagger.annotations.ApiOperation;

/**
 * @author LIMHa
 * @date 2017/12/11
 */
@RestController
@RequestMapping("/api/phone/v1/monitor")
public class MonitorController {

    @Autowired
    private DaybreakService daybreakService;

    @Autowired
    private BeforeClassService beforeClassService;

    @Autowired
    private OutClassService outClassService;

//   @Autowired
//   private CourseRollcallMonitorService courseRollcallMonitorService;

//    @Autowired
//    private ContrastToolService contrastToolService;

    // @RequestMapping(value = "/listOrg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "查询学校排课信息", response = Void.class, notes = "查询学校排课信息<br>@author meihua")
    // public ResponseEntity<?> listOrg(@RequestParam(value = "orgName", required = false) String orgName, @RequestParam(value = "teachDate") String teachDate,
    // @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // return new ResponseEntity<Object>(daybreakService.listDayBreak(orgName, teachDate), HttpStatus.OK);
    // }

    // @RequestMapping(value = "/getOrg", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "根据状态查询学校排课信息", response = Void.class, notes = "根据状态查询学校排课信息<br>@author meihua")
    // public ResponseEntity<?> getOrg(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "flag", required = false) Boolean flag,
    // @RequestParam(value = "teachDate") String teachDate, @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // return new ResponseEntity<Object>(daybreakService.list(orgId, flag, teachDate), HttpStatus.OK);
    // }

    // @RequestMapping(value = "/listBeforeClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "查询学校排课信息", response = Void.class, notes = "查询学校排课信息<br>@author meihua")
    // public ResponseEntity<?> listBeforeClass(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "flag") Boolean flag,
    // @RequestParam(value = "teachDate") String teachDate, @ApiParam(value = "status 10:打卡机开启,20:打卡机关闭") @RequestParam(value = "status", required = false) String status,
    // @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // List<BeforeClass> beforeClassDTOList = beforeClassService.findAllByOrgIdAndTeachDate(orgId, teachDate, flag, status);
    // return new ResponseEntity<Object>(beforeClassDTOList, HttpStatus.OK);
    // }

    // @RequestMapping(value = "/listOutClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "查询学校排课信息", response = Void.class, notes = "查询学校排课信息<br>@author meihua")
    // public ResponseEntity<?> listOutClass(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "flag") Boolean flag,
    // @RequestParam(value = "teachDate") String teachDate, @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // List<OutClass> outClassDTOList = outClassService.findAllByOrgIdAndTeachDate(orgId, teachDate, flag);
    // return new ResponseEntity<Object>(outClassDTOList, HttpStatus.OK);
    // }

//    @RequestMapping(value = "/repairOutClass", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "PUT", value = "修复课后任务(当天有效)", response = Void.class, notes = "修复课后任务(当天有效)<br>@author meihua")
//    public ResponseEntity<?> repairOutClass(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "scheduleId") Long scheduleId,
//        @RequestHeader("Authorization") String accessToken) {
//
//        return new ResponseEntity<Object>(outClassService.repairOutClass(orgId, scheduleId), HttpStatus.OK);
//    }

//    @RequestMapping(value = "/repairBeforClass", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperation(httpMethod = "PUT", value = "修复课前任务(课前有效)", response = Void.class, notes = "修复课前任务(课前有效)<br>@author meihua")
//    public ResponseEntity<?> repairBeforClass(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "scheduleId") Long scheduleId,
//        @RequestHeader("Authorization") String accessToken) {
//
//        return new ResponseEntity<Object>(beforeClassService.repairBeforeClass(orgId, scheduleId), HttpStatus.OK);
//    }

    @RequestMapping(value = "/repairDayBreak", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修复凌晨任务(修复凌晨)", response = Void.class, notes = "重复执行，会清除当天已进行考勤的排课数据<br>@author meihua")
    public ResponseEntity<?> repairDayBreak(@RequestParam(value = "orgId") Long orgId, @RequestParam(value = "orgName") String orgName,
        @RequestHeader("Authorization") String accessToken) {

        return new ResponseEntity<Object>(daybreakService.repairDayBreak(orgId, orgName), HttpStatus.OK);
    }

    // /**
    // * 根据教师id获取课程列表
    // */
    // @RequestMapping(value = "/teacher/getCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "根据教师id获取当前学期的课程列表", response = Void.class, notes = "根据教师id获取课程列表<br>@author meihua")
    // public ResponseEntity<?> getCourseList(@RequestParam(value = "teacherId") Long teacherId, @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // return new ResponseEntity<Object>(courseRollcallMonitorService.getCourseRollcall(teacherId), HttpStatus.OK);
    // }

    // /**
    // * 查询所有学校
    // */
    // @RequestMapping(value = "/listAllOrgInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "查询所有学校", response = Void.class, notes = "查询所有学校<br>@author meihua")
    // public ResponseEntity<?> listOrgInfo(@RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // return new ResponseEntity<Object>(courseRollcallMonitorService.getAllOrg(), HttpStatus.OK);
    // }

    // /**
    // * 查询所有学校
    // */
    // @RequestMapping(value = "/teacher/listTeacher", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "查询某学校老师", response = Void.class, notes = "查询某学校老师<br>@author meihua")
    // public ResponseEntity<?> listTeacherInfo(@RequestParam(value = "orgId") Long orgId,
    // @ApiParam(value = "老师名称或工号模糊查询") @RequestParam(value = "kewWord", required = false) String kewWord, @RequestParam(value = "pageSize", required = false) Integer pageSize,
    // @RequestParam(value = "pageNum", required = false) Integer pageNum, @RequestHeader(value = "Authorization", required = false) String accessToken) {
    //
    // return new ResponseEntity<Object>(courseRollcallMonitorService.listTeacherInfo(orgId, kewWord, pageSize, pageNum), HttpStatus.OK);
    // }

    // @RequestMapping(value = "/system/contrastTool", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    // @ApiOperation(httpMethod = "GET", value = "对比检测", response = Void.class, notes = "对比检测<br>@author meihua")
    // public ResponseEntity<?> contrastTool() {
    // contrastToolService.contrastTask();
    // return new ResponseEntity<Object>(null, HttpStatus.OK);
    // }
}
