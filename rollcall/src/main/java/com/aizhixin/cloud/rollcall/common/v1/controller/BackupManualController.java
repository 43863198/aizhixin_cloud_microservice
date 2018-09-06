package com.aizhixin.cloud.rollcall.common.v1.controller;

import com.aizhixin.cloud.rollcall.domain.RollcallRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRedisDomain;
import com.aizhixin.cloud.rollcall.domain.ScheduleRollCallRedisDomain;
import com.aizhixin.cloud.rollcall.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/manual")
@Api(description = "后台手动操作API")
public class BackupManualController {
    @Autowired
    private RollcallDayTaskPreprocessService rollcallDayTaskPreprocessService;
    @Autowired
    private RollcallClassInTaskPreprocessService rollcallClassInTaskPreprocessService;
    @Autowired
    private MyCacheService myCacheService;
    @Autowired
    private RollcallClassOutTaskPreprocessService rollcallClassOutTaskPreprocessService;
    @Autowired
    private ManualTriggerService manualTriggerService;

    @Autowired
    private RollCallMedianProcessService rollCallMedianProcessService;

    @RequestMapping(value = "/cache/init", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "从数据数据手动初始化当天课表相关数据(需先清除缓存)", response = Void.class, notes = "从数据数据手动初始化当天课表相关数据(需先清除缓存)<br><br><b>@author zhen.pan</b>")
    public void initCahce(@ApiParam(value = "学校ID列表") @RequestBody Set<Long> orgIds) {
        rollcallDayTaskPreprocessService.initRedisCurentDay(orgIds);
    }

    @RequestMapping(value = "/cache/initall", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "从数据数据手动初始化当天课表相关数据(所有学校，需先清除缓存)", response = Void.class, notes = "从数据数据手动初始化当天课表相关数据(所有学校，需先清除缓存)<br><br><b>@author zhen.pan</b>")
    public void initCahceAllSchool() {
        rollcallDayTaskPreprocessService.initRedisCurentDayAllOrg();
    }

    @RequestMapping(value = "/cache/systemDb", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "凌晨调度,插入数据", response = Void.class, notes = "凌晨调度,插入数据<br><br><b>@author meihua</b>")
    public void systemDb() {
        rollcallDayTaskPreprocessService.schoolDayPreprocessTask();
    }

    @RequestMapping(value = "/cache/clear", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清理所有的缓存数据", response = Void.class, notes = "清理所有的缓存数据<br><br><b>@author zhen.pan</b>")
    public void cacheClear() {
        myCacheService.clearAllCache();
    }

    @RequestMapping(value = "/cache/teacherschedulerollcall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取老师的课堂规则", response = Void.class, notes = "获取老师的课堂规则<br><br><b>@author zhen.pan</b>")
    public List<ScheduleRollCallRedisDomain> getTeacherScheduleRollcall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "老师ID") @RequestParam("teacherId") Long teacherId) {
        return myCacheService.getTeacherScheduleRollCall(orgId, teacherId);
    }

    @RequestMapping(value = "/cache/studentschedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取学生的课表", response = Void.class, notes = "获取学生的课表<br><br><b>@author zhen.pan</b>")
    public List<ScheduleRedisDomain> getStudentSchedule(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getStudentSchedule(orgId, studentId);
    }

    @RequestMapping(value = "/inclasstask", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "手动课前处理任务", response = Void.class, notes = "手动课前处理任务<br><br><b>@author zhen.pan</b>")
    public void startInClasses() {
        rollcallClassInTaskPreprocessService.processInClassStart();
    }

    @RequestMapping(value = "/clear/zk", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "清理zookeeper数据", response = Void.class, notes = "清理zookeeper数据<br><br><b>@author zhen.pan</b>")
    public void clearZookeeper() {
        myCacheService.clearZookeeperData();
    }

    @RequestMapping(value = "/get/ruler", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取课堂规则", response = Void.class, notes = "获取课堂规则<br><br><b>@author zhen.pan</b>")
    public ScheduleRollCallRedisDomain getRuler(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂规则ID") @RequestParam("rulerId") Long rulerId) {
        return myCacheService.getRuler(orgId, rulerId);
    }

    @RequestMapping(value = "/get/lock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取zookeeper锁", response = Void.class, notes = "获取zookeeper锁<br><br><b>@author zhen.pan</b>")
    public Boolean getLock() {
        return myCacheService.getLock();
    }

    @RequestMapping(value = "/outclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "执行课后任务", response = Void.class, notes = "执行课后任务<br><br><b>@author meihua</b>")
    public void outclass() {
        rollcallClassOutTaskPreprocessService.processOutClassStart();
    }

    @RequestMapping(value = "/get/studentrollcall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取单个学生的签到列表数据", response = Void.class, notes = "获取单个学生的签到列表数据<br><br><b>@author zhen.pan</b>")
    public RollcallRedisDomain getStudentRollcall(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId,
        @ApiParam(value = "课堂ID") @RequestParam("scheduleId") Long scheduleId, @ApiParam(value = "学生ID") @RequestParam("studentId") Long studentId) {
        return myCacheService.getStudentRollcall(orgId, scheduleId, studentId);
    }

    @RequestMapping(value = "/preClass/init", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "初始化课前数据", response = Void.class, notes = "初始化课前数据<br><br><b>@author xiagen</b>")
    public void initPreClass(@ApiParam(value = "学校id", required = true) @RequestParam(value = "orgId", required = true) Long orgId,
        @ApiParam(value = "课堂id集合", required = true) @RequestParam(value = "schedulesIds", required = true) Set<Long> schedulesIds) {
        manualTriggerService.dealPreClass(orgId, schedulesIds);
    }

    @RequestMapping(value = "/outClass/stop", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "手动结束课程", response = Void.class, notes = "手动结束课程<br><br><b>@author hsh</b>")
    public void stopClass(@ApiParam(value = "学校ID") @RequestParam("orgId") Long orgId, @ApiParam(value = "课堂ID集合") @RequestParam("scheduleIds") Set<String> scheduleIds) {
        manualTriggerService.stopClass(orgId, scheduleIds);
    }

    @RequestMapping(value = "/median", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "", response = Void.class, notes = "<br><br><b>@author hsh</b>")
    public void median() {
        rollCallMedianProcessService.processRollCallMedian();
    }
}
