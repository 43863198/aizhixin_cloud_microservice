package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.remote.OrgManagerRemoteClient;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PeriodDTO;
import com.aizhixin.cloud.dd.rollcall.service.CounsellorService;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.PeriodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LIMH on 2017/10/13.
 */
@RestController
@RequestMapping("/api/phone/v1/counsellor")
@Api(value = "手机辅导员API", description = "针对手机辅导员相关API")
public class CounsellorPhoneController {

    private final Logger log = LoggerFactory.getLogger(CounsellorPhoneController.class);
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CounsellorService counsellorService;
    @Autowired
    private OrgManagerRemoteClient orgManagerRemoteService;
    @Autowired
    private PeriodService periodService;

    @RequestMapping(value = "/timelyAttendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员及时到课率查询", response = Void.class, notes = "辅导员及时到课率查询<br>@author meihua.li")
    public ResponseEntity<?> timelyAttendance(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(counsellorService.timelyAttendance(account.getId(), account.getOrganId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/timelyAttendanceByPeriod", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "辅导员及时到课率查询", response = Void.class, notes = "辅导员及时到课率查询<br>@author hsh")
    public ResponseEntity<?> timelyAttendance(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "课节id", required = false) @RequestParam(value = "periodId", required = false) Long periodId) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (periodId == null || periodId < 1) {
            List<PeriodDTO> periodDTOS = periodService.listPeriod(account.getOrganId());
            if (null != periodDTOS) {
                periodId = counsellorService.getCurrentPeriodId(periodDTOS, new Date());
            }
        }
        return new ResponseEntity(counsellorService.timelyAttendance(account.getId(), account.getOrganId(), periodId), HttpStatus.OK);
    }

    @RequestMapping(value = "/getPeriodList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询课节", response = Void.class, notes = "查询课节<br>@author hsh")
    public ResponseEntity<?> getPeriodList(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity(counsellorService.getPeriodList(account.getOrganId()), HttpStatus.OK);
    }

    @RequestMapping(value = "/queryClasses", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询辅导员带的行政班", response = Void.class, notes = "查询辅导员带的行政班<br>@author meihua.li")
    public ResponseEntity<?> queryClasses(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();
        result.put(ApiReturnConstants.DATA, counsellorService.queryClasses(account.getId()));
        result.put(ApiReturnConstants.MESSAGE, "");
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/cansel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "暂停学生考勤", response = Void.class, notes = "暂停学生考勤<br>@author meihua.li")
    public ResponseEntity<?> cansel(@RequestParam(value = "studentId") Long studentId, @RequestParam(value = "msg", required = false) String msg,
                                    @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();
        try {
            orgManagerRemoteService.cansel(studentId, msg, account.getId());
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "修改失败,联系管理员!");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/cansel/batch", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "暂停学生考勤批量", response = Void.class, notes = "暂停学生考勤批量<br>@author meihua.li")
    public ResponseEntity<?> canselBatch(@RequestParam(value = "studentIds") Set<Long> studentIds, @RequestParam(value = "msg", required = false) String msg,
                                         @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();
        try {
            if (null != studentIds && !studentIds.isEmpty()) {
                for (Long studentId : studentIds) {
                    orgManagerRemoteService.cansel(studentId, msg, account.getId());
                }
            }
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "修改失败,联系管理员!");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/recover", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "恢复学生考勤", response = Void.class, notes = "恢复学生考勤<br>@author meihua.li")
    public ResponseEntity<?> recover(@RequestParam(value = "studentId") Long studentId, @RequestParam(value = "msg", required = false) String msg,
                                     @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();

        try {
            orgManagerRemoteService.recover(studentId, msg, account.getId());
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "修改失败,联系管理员!");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/recover/batch", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "PUT", value = "恢复学生考勤批量", response = Void.class, notes = "恢复学生考勤批量<br>@author meihua.li")
    public ResponseEntity<?> recoverBatch(@RequestParam(value = "studentIds") Set<Long> studentIds, @RequestParam(value = "msg", required = false) String msg,
                                          @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashedMap();

        try {

            if (null != studentIds && !studentIds.isEmpty()) {
                for (Long studentId : studentIds) {
                    orgManagerRemoteService.recover(studentId, msg, account.getId());
                }
            }

            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "");
            result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        } catch (Exception e) {
            result.put(ApiReturnConstants.DATA, "");
            result.put(ApiReturnConstants.MESSAGE, "修改失败,联系管理员!");
            result.put(ApiReturnConstants.SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
