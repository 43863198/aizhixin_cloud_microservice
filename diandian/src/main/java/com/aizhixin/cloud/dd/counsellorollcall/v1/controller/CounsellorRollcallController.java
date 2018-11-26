package com.aizhixin.cloud.dd.counsellorollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.communication.dto.ReportDTO;
import com.aizhixin.cloud.dd.communication.service.RollCallEverService;
import com.aizhixin.cloud.dd.communication.utils.HttpSimpleUtils;
import com.aizhixin.cloud.dd.counsellorollcall.dto.CounRollcallGroupDTO;
import com.aizhixin.cloud.dd.counsellorollcall.v1.service.*;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequestMapping("/api/phone/v1/counsellor")
@RestController
@Api(value = "辅导员点名相关API", description = "辅导员点名相关API")
public class CounsellorRollcallController {

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private CounsellorRollcallService counsellorRollcallService;

    @Lazy
    @Autowired
    private StudentSignInService studentSignInService;

    @Lazy
    @Autowired
    private TempGroupService tempGroupService;

    @Lazy
    @Autowired
    private StudentSubGroupService studentSubGroupService;
    @Autowired
    private HttpSimpleUtils httpSimpleUtils;

    @Autowired
    private AlarmClockService alarmClockService;

    @Autowired
    private RollCallEverService rollCallEverService;

    /**
     * 导员点名组查询
     */
    @RequestMapping(value = "/teacher/listCounsellorGroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "导员点名组查询", response = Void.class, notes = "导员点名组查询<br>@author meihua")
    public ResponseEntity<?> listCounsellorGroup(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.listCounsellorGroup(account.getOrganId(), account.getId(), account.getName()), HttpStatus.OK);
    }

    /**
     * 删除导员点名组
     */
    @RequestMapping(value = "/teacher/deleteCounsellorGroup", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除导员点名组", response = Void.class, notes = "删除导员点名组<br>@author meihua")
    public ResponseEntity<?> deleteCounsellorGroup(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Set<Long> tempGroupId,
                                                   @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.deleteTempGroup(tempGroupId), HttpStatus.OK);
    }

    /**
     * 导员点名闹钟开关
     */
    @RequestMapping(value = "/teacher/alarmOnOff", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "导员点名闹钟开关", response = Void.class, notes = "导员点名闹钟开关<br>@author meihua")
    public ResponseEntity<?> alarmOnOff(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId") Long tempGroupId,
                                        @ApiParam(value = "alarmOnOff") @RequestParam(value = "alarmOnOff") Boolean alarmOnOff, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.updateTempGroupAlarm(tempGroupId, alarmOnOff), HttpStatus.OK);
    }

    /**
     * 添加导员点名组
     */
    @RequestMapping(value = "/teacher/addCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "添加导员点名组", response = Void.class, notes = "添加导员点名组<br>@author meihua")
    public ResponseEntity<?> addCounsellorGroup(@ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTO counRollcallGroupDTO,
                                                @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.saveTempGroupByTeacher(account.getOrganId(), account.getId(), account.getName(), counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 修改导员点名组
     */
    @RequestMapping(value = "/teacher/updateCounsellorGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改导员点名组", response = Void.class, notes = "修改导员点名组<br>@author meihua")
    public ResponseEntity<?> updateCounsellorGroup(@ApiParam(value = "counRollcallGroupDTO 导员点名") @RequestBody CounRollcallGroupDTO counRollcallGroupDTO,
                                                   @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.updateTempGroup(account.getOrganId(), counRollcallGroupDTO), HttpStatus.OK);
    }

    /**
     * 导员点名组开启点名
     */
    @RequestMapping(value = "/teacher/openCounsellorGroup", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "导员点名组开启点名", response = Void.class, notes = "导员点名组开启点名<br>@author meihua")
    public ResponseEntity<?> openCounsellorGroup(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId,
                                                 @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.openTempGroup(accessToken, tempGroupId), HttpStatus.OK);
    }

    /**
     * 导员点名组关闭点名
     */
    @RequestMapping(value = "/teacher/closeCounsellorGroup", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "导员点名组关闭点名", response = Void.class, notes = "导员点名组关闭点名br>@author meihua")
    public ResponseEntity<?> closeCounsellorGroup(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId,
                                                  @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(tempGroupService.closeTempGroup(tempGroupId), HttpStatus.OK);
    }

    /**
     * 查询导员点名列表
     */
    @RequestMapping(value = "/teacher/listCounsellorRollcall", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名列表", response = Void.class, notes = "查询导员点名列表<br>@author meihua")
    public ResponseEntity<?> listCounsellorRollcall(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId,
                                                    @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(counsellorRollcallService.findConRollCallList(tempGroupId), HttpStatus.OK);
    }

    /**
     * 删除导员点名
     */
    @RequestMapping(value = "/teacher/deleteCounsellorRollcall", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "DELETE", value = "删除导员点名", response = Void.class, notes = "删除导员点名<br>@author meihua")
    public ResponseEntity<?> deleteCounsellorRollcall(@RequestParam(value = "counsellorIds") Set<Long> counsellorIds, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(counsellorRollcallService.deleteCounsellor(counsellorIds), HttpStatus.OK);
    }

    /**
     * 查询组内学生
     */
    @RequestMapping(value = "/teacher/listCounsellorRollcallStudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询组内学生", response = Void.class, notes = "查询组内学生<br>@author meihua")
    public ResponseEntity<?> listCounsellorRollcallStudent(@ApiParam(value = "tempGroupId") @RequestParam(value = "tempGroupId", required = true) Long tempGroupId,
                                                           @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(studentSubGroupService.listStudentTempGroup(tempGroupId), HttpStatus.OK);
    }

    /**
     * 查询导员点名明细
     */
    @RequestMapping(value = "/teacher/queryDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询导员点名明细", response = Void.class, notes = "查询导员点名明细<br>@author meihua")
    public ResponseEntity<?> queryDetail(@ApiParam(value = "id (必填)") @RequestParam(value = "rollCallEverId") Long rollCallEverId,
                                         @ApiParam(value = "状态 UnCommit(\"未提交\", \"10\"), HavaTo(\"已到\", \"20\"), NonArrival(\"未到\", \"30\"), AskForLeave(\"请假\", \"40\")") @RequestParam(value = "status",
                                                 required = false) String status,
                                         @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(studentSignInService.findCouRollcallDetail(rollCallEverId, status), HttpStatus.OK);
    }

    /**
     * 根据学生id查询电话
     */
    @RequestMapping(value = "/teacher/queryPhoneById", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据id查询电话", response = Void.class, notes = "查询<br>@author meihua")
    public ResponseEntity<?> queryPhoneById(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "id", required = true) @RequestParam(value = "id") Long id) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> resBody = new HashMap<>(200);
        httpSimpleUtils.fromZhixinAvater(id + "", resBody);
        return new ResponseEntity(resBody, HttpStatus.OK);
    }

    /**
     * 修改学生状态
     */
    @RequestMapping(value = "/teacher/updateStudents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "修改学生状态", response = Void.class, notes = "修改学生状态<br>@author meihua")
    public ResponseEntity<?> updateStudents(@ApiParam(value = "ids") @RequestParam(value = "ids", required = false) Set<Long> ids,
                                            @ApiParam(value = "status") @RequestParam(value = "status", required = true) String status, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        studentSignInService.updateStudetnStatus(ids, status);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 根据班级查询学生
     */
    @RequestMapping(value = "/teacher/getStudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据班级查询学生", response = Void.class, notes = "根据班级查询学生<br>@author meihua")
    public ResponseEntity<?> getStudent(@ApiParam(value = "组ID") @RequestParam(value = "tempGroupId", required = false) Long tempGroupId,
                                        @ApiParam(value = "班级ID") @RequestParam(value = "classId", required = true) Long classId,
                                        @ApiParam(value = "班级名称（不做查询条件，回传数据）") @RequestParam(value = "className", required = true) String className, @RequestHeader("Authorization") String accessToken)
            throws IOException {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(studentSubGroupService.getStudentInfo(tempGroupId, classId, className), HttpStatus.OK);
    }

    @RequestMapping(value = "/students/getRollCallEver", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询辅导员点名信息", response = Void.class, notes = "学生查询辅导员点名信息<br>@author meihua")
    public ResponseEntity<?> getRollCallEver(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(studentSignInService.listStudentSignInCache(account.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/student/reportEver", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "辅导员点名签到", response = Void.class, notes = "辅导员点名签到<br>@author meihua")
    public ResponseEntity<?> reportEver(@ApiParam(value = "点名信息") @RequestBody ReportDTO reportDTO, @RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        log.info("导员点名签到:{} {}", account.getId(), reportDTO);
        /** ------------------------------------潘震2017-12-29修改----------------------- */
//        Long a=System.currentTimeMillis();
        Map<String, Object> res = studentSignInService.signIn(account, reportDTO);
        Boolean r = (Boolean) res.get(ApiReturnConstants.SUCCESS);
        if (null != r && !r.booleanValue()) {
            return new ResponseEntity<Object>(res, HttpStatus.BAD_REQUEST);
        } /** ------------------------------------潘震2017-12-29修改----------------------- */
//        Long b=System.currentTimeMillis();
//        System.out.println((b-a));
        return new ResponseEntity<Object>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/system/doAlarmClock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "定时任务", response = Void.class, notes = "定时任务<br>@author meihua")
    public ResponseEntity<?> doAlarmClock(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(alarmClockService.needAlarmClockTempGroup(), HttpStatus.OK);
    }

    @RequestMapping(value = "/system/closeAllAlarmClock", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "凌晨定时任务", response = Void.class, notes = "凌晨定时任务<br>@author meihua")
    public ResponseEntity<?> closeAllAlarmClock(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        tempGroupService.closeAllTempGroup();
        rollCallEverService.scheduleCloseRollCallEver();
        return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
}
