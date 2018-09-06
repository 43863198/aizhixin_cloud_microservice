package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.RollCallStatsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/phone/v1/rollcall")
public class RollCallStatsController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private RollCallStatsService rollCallStatsService;

    @RequestMapping(value = "/student/getAllRollCallStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生考勤累计统计", response = Void.class, notes = "学生考勤累计统计<br>@author hsh")
    public ResponseEntity<?> getAllRollCallStats(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(rollCallStatsService.getStuStatsAll(account.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getTeachingClassRollCallStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生教学班考勤统计", response = Void.class, notes = "学生教学班考勤统计<br>@author hsh")
    public ResponseEntity<?> getCourseRollCallStats(@RequestHeader("Authorization") String accessToken,
                                                    @ApiParam(value = "teachingClassId", required = true) @RequestParam(value = "teachingClassId", required = true) Long teachingClassId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(rollCallStatsService.getStuTeachingClassStats(account.getId(), teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getTeachingClassRollCallStatsV2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生教学班考勤统计", response = Void.class, notes = "学生教学班考勤统计<br>@author hsh")
    public ResponseEntity<?> getTeachingClassRollCallStatsV2(@ApiParam(value = "stuId", required = true) @RequestParam(value = "stuId", required = true) Long stuId,
                                                             @ApiParam(value = "teachingClassId", required = true) @RequestParam(value = "teachingClassId", required = true) Long teachingClassId) {
        return new ResponseEntity(rollCallStatsService.getStuTeachingClassStats(stuId, teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getTeachingClassRollCallStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师教学班考勤统计", response = Void.class, notes = "教师教学班考勤统计<br>@author hsh")
    public ResponseEntity<?> getTeachingClassRollCallStats(@ApiParam(value = "teachingClassId", required = true) @RequestParam(value = "teachingClassId", required = true) Long teachingClassId) {
        return new ResponseEntity(rollCallStatsService.getTeachingClassStats(teachingClassId), HttpStatus.OK);
    }

    @RequestMapping(value = "/initStuRollCallStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "计算学生考勤统计", response = Void.class, notes = "计算学生考勤统计<br>@author hsh")
    public ResponseEntity<?> getCourseRollCallStats(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        rollCallStatsService.initStatsData();
        return new ResponseEntity(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/initStuTotalRollCallStatsByTeachingClass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "计算学生考勤统计", response = Void.class, notes = "计算学生考勤统计<br>@author hsh")
    public ResponseEntity<?> initStuTotalRollCallStatsByTeachingClass(@ApiParam(value = "orgId") @RequestParam(value = "orgId") Long orgId,
                                                                      @ApiParam(value = "teachingClassIds") @RequestParam(value = "teachingClassIds") Set<Long> teachingClassIds) {
        rollCallStatsService.initStuTotalRollCallStatsByTeachingClass(orgId, teachingClassIds);
        return new ResponseEntity(null, HttpStatus.OK);
    }
}
