package com.aizhixin.cloud.dd.counsellorollcall.v2.controller;

import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTO;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTOV2;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallRuleDTO;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.TempGroupService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallStudentService;
import com.aizhixin.cloud.dd.counsellorollcall.v2.service.CounselorRollcallTeacherService;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/api/phone/v2/counsellor")
@RestController
@Api(value = "辅导员点名相关API", description = "辅导员点名相关API")
public class CounselorRollcallPhoneControllerV2 {

    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CounselorRollcallTeacherService teacherService;
    @Autowired
    private CounselorRollcallStudentService studentService;
    @Autowired
    private TempGroupService tempGroupService;

    /**
     * 导员点名组查询
     */
    @RequestMapping(value = "/student/listCounsellorGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "导员点名组查询", response = Void.class, notes = "导员点名组查询<br>@author hsh")
    public ResponseEntity<?> listCounsellorGroup(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.listCounsellorGroupByStu(account.getOrganId(), account.getId()), HttpStatus.OK);
    }

    /**
     * 添加导员点名组
     */
    @RequestMapping(value = "/student/addCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加导员点名组", response = Void.class, notes = "添加导员点名组<br>@author hsh")
    public ResponseEntity<?> addCounsellorGroup(@ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTOV2 counRollcallGroupDTO,
                                                @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.saveTempGroupByStu(account.getOrganId(), account.getId(), counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 修改导员点名组
     */
    @RequestMapping(value = "/student/updateCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改导员点名组", response = Void.class, notes = "修改导员点名组<br>@author hsh")
    public ResponseEntity<?> updateCounsellorGroup(@ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTOV2 counRollcallGroupDTO,
                                                   @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.updateTempGroupByStu(account.getId(), counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 学生查询导员点名列表
     */
    @RequestMapping(value = "/student/getRollCallGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询导员点名列表", response = Void.class, notes = "学生查询导员点名列表<br>@author hsh<br>10:未提交 20:已到 40:请假 50:迟到")
    public ResponseEntity<?> getRollcallList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(studentService.getRollcallGroupList(account), HttpStatus.OK);
    }

    /**
     * 学生按天查询导员点名记录
     */
    @RequestMapping(value = "/student/getRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生按天查询导员点名记录", response = Void.class, notes = "学生按天查询导员点名记录<br>@author hsh")
    public ResponseEntity<?> getRollcall(@RequestHeader("Authorization") String accessToken,
                                         @ApiParam(value = "点名组id", required = true) @RequestParam(value = "groupId", required = true) Long groupId,
                                         @ApiParam(value = "点名日期 yyyy-MM-dd 空为当天", required = true) @RequestParam(value = "date", required = true) String date) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(studentService.getRollcall(account.getId(), groupId, date), HttpStatus.OK);
    }

    /**
     * 查询导员点名组日统计
     */
    @RequestMapping(value = "/teacher/getDailyStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组日统计", response = Void.class, notes = "查询导员点名组日统计<br>@author hsh")
    public ResponseEntity<?> getDailyStatistics(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "点名组id", required = true) @RequestParam(value = "groupId", required = true) Long groupId,
                                                @ApiParam(value = "点名状态 10:迟到 20:开始缺卡 30:结束缺卡 40:请假 50:已到 60:两次都缺卡", required = true) @RequestParam(value = "status", required = true) Integer status,
                                                @ApiParam(value = "点名日期 yyyy-MM-dd 空为当天", required = true) @RequestParam(value = "date", required = true) String date) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getDailyStatistics(groupId, status, date), HttpStatus.OK);
    }

    /**
     * 查询导员点名组学生签到记录
     */
    @RequestMapping(value = "/teacher/getStuRollCall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组学生签到记录", response = Void.class, notes = "查询导员点名组学生签到记录<br>@author hsh")
    public ResponseEntity<?> getStuRollCall(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "学生id", required = true) @RequestParam(value = "stuId", required = true) Long stuId,
                                            @ApiParam(value = "点名组id", required = true) @RequestParam(value = "groupId", required = true) Long groupId,
                                            @ApiParam(value = "点名日期 yyyy-MM-dd 空为当天", required = true) @RequestParam(value = "date", required = true) String date) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getStuRollCall(stuId, groupId, date), HttpStatus.OK);
    }

    /**
     * 导员修改点名结果
     */
    @RequestMapping(value = "/teacher/updateRollcallStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "导员修改点名结果", response = Void.class, notes = "导员修改点名结果<br>@author hsh")
    public ResponseEntity<?> updateRollcallStatus(@RequestHeader("Authorization") String accessToken,
                                                  @ApiParam(value = "签到id", required = true) @RequestParam(value = "id", required = true) Long id,
                                                  @ApiParam(value = "状态 10:迟到 20:开始缺卡 30:结束缺卡 40:开始请假 50:结束请假 60:开始已到 70:结束已到", required = true) @RequestParam(value = "status", required = true) Integer status) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.updateRollcallStatus(id, status), HttpStatus.OK);
    }

    /**
     * 导员批量修改点名结果
     */
    @RequestMapping(value = "/teacher/updateRollcallStatusBatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "导员批量修改点名结果", response = Void.class, notes = "导员批量修改点名结果<br>@author hsh")
    public ResponseEntity<?> updateRollcallStatusBatch(@RequestHeader("Authorization") String accessToken,
                                                       @ApiParam(value = "签到id", required = true) @RequestParam(value = "ids", required = true) Set<Long> ids,
                                                       @ApiParam(value = "状态 10:已到 20:请假 30:缺卡", required = true) @RequestParam(value = "status", required = true) Integer status) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.updateRollcallStatusBatch(ids, status), HttpStatus.OK);
    }

    /**
     * 查询导员点名组月统计
     */
    @RequestMapping(value = "/teacher/getMonthlyStatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组月统计", response = Void.class, notes = "查询导员点名组月统计<br>@author hsh")
    public ResponseEntity<?> getCounsellorRollCallList(@RequestHeader("Authorization") String accessToken,
                                                       @ApiParam(value = "点名组id", required = true) @RequestParam(value = "groupId", required = true) Long groupId,
                                                       @ApiParam(value = "点名状态 10:迟到 20:开始缺卡 30:结束缺卡 40:请假", required = true) @RequestParam(value = "status", required = true) Integer status,
                                                       @ApiParam(value = "点名日期 yyyy-MM 空为当月", required = true) @RequestParam(value = "date", required = true) String date) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getMonthlyStatistics(groupId, status, date), HttpStatus.OK);
    }

    /**
     * 查询导员点名组月统计学生详情
     */
    @RequestMapping(value = "/teacher/getMonthlyStatisticsStudentDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组月统计学生详情", response = Void.class, notes = "查询导员点名组月统计学生详情<br>@author hsh")
    public ResponseEntity<?> getMonthlyStatisticsStudentDetails(@RequestHeader("Authorization") String accessToken,
                                                                @ApiParam(value = "点名组id", required = true) @RequestParam(value = "groupId", required = true) Long groupId,
                                                                @ApiParam(value = "学生id", required = true) @RequestParam(value = "stuId", required = true) Long stuId,
                                                                @ApiParam(value = "点名状态 10:迟到 20:开始缺卡 30:结束缺卡 40:请假", required = true) @RequestParam(value = "status", required = true) Integer status,
                                                                @ApiParam(value = "点名日期 yyyy-MM 空为当月", required = true) @RequestParam(value = "date", required = true) String date) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getMonthlyStatisticsStudentDetails(groupId, stuId, status, date), HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getCounsellorGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名组", response = Void.class, notes = "查询导员点名组<br>@author hsh")
    public ResponseEntity<?> getCounsellorGroup(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getTempGroup(account), HttpStatus.OK);
    }

    /**
     * 添加导员点名组
     */
    @RequestMapping(value = "/teacher/addCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加导员点名组", response = Void.class, notes = "添加导员点名组<br>@author hsh")
    public ResponseEntity<?> addCounsellorGroup(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTOV2 counRollcallGroupDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.saveTempGroupByTeacher(account, counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 修改导员点名组
     */
    @RequestMapping(value = "/teacher/updateCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改导员点名组", response = Void.class, notes = "修改导员点名组<br>@author hsh")
    public ResponseEntity<?> updateCounsellorGroup(@RequestHeader("Authorization") String accessToken,
                                                   @ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTOV2 counRollcallGroupDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.updateTempGroup(account, counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 删除导员点名组
     */
    @RequestMapping(value = "/teacher/delCounsellorGroup", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除导员点名组", response = Void.class, notes = "删除导员点名组<br>@author hsh")
    public ResponseEntity<?> delCounsellorGroup(@RequestHeader("Authorization") String accessToken,
                                                @ApiParam(value = "点名组id", required = true) @RequestParam(value = "tempGroupIds", required = true) Set<Long> tempGroupIds) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.delTempGroup(account.getId(), tempGroupIds), HttpStatus.OK);
    }

    /**
     * 查询点名规则
     */
    @RequestMapping(value = "/teacher/getRuleList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询点名规则", response = Void.class, notes = "查询点名规则<br>@author hsh")
    public ResponseEntity<?> getRuleList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.getRuleList(account.getId()), HttpStatus.OK);
    }

    /**
     * 添加点名规则
     */
    @RequestMapping(value = "/teacher/addRule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加点名规则", response = Void.class, notes = "添加点名规则<br>@author hsh")
    public ResponseEntity<?> addRule(@RequestHeader("Authorization") String accessToken,
                                     @ApiParam(value = "ruleDTO 点名规则") @RequestBody CounRollcallRuleDTO ruleDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.addRule(account.getId(), ruleDTO), HttpStatus.OK);
    }

    /**
     * 修改点名规则
     */
    @RequestMapping(value = "/teacher/updateRule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加点名规则", response = Void.class, notes = "添加点名规则<br>@author hsh")
    public ResponseEntity<?> updateRule(@RequestHeader("Authorization") String accessToken,
                                        @ApiParam(value = "ruleDTO 点名规则") @RequestBody CounRollcallRuleDTO ruleDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.updateRule(account.getId(), ruleDTO), HttpStatus.OK);
    }

    /**
     * 删除点名规则
     */
    @RequestMapping(value = "/teacher/delRule", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除点名规则", response = Void.class, notes = "删除点名规则<br>@author hsh")
    public ResponseEntity<?> delRule(@RequestHeader("Authorization") String accessToken,
                                     @ApiParam(value = "ruleIds", required = true) @RequestParam(value = "ruleIds", required = true) Set<Long> ruleIds) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(teacherService.delRule(account.getId(), ruleIds), HttpStatus.OK);
    }
}
