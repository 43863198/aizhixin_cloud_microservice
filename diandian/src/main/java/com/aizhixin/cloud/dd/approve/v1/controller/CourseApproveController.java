package com.aizhixin.cloud.dd.approve.v1.controller;

import com.aizhixin.cloud.dd.approve.domain.CourseApproveDomain;
import com.aizhixin.cloud.dd.approve.domain.ApproveStateDomain;
import com.aizhixin.cloud.dd.approve.domain.CourseApproveDomainV2;
import com.aizhixin.cloud.dd.approve.domain.TeachingClassDomain;
import com.aizhixin.cloud.dd.approve.entity.CourseApprove;
import com.aizhixin.cloud.dd.approve.services.AdjustCourseScheduleRecordService;
import com.aizhixin.cloud.dd.approve.services.CourseApproveService;
import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/phone/v1")
@Api(description = "调停课审批操作API")
public class CourseApproveController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private CourseApproveService courseApproveService;
    @Autowired
    private AdjustCourseScheduleRecordService adjustCourseScheduleRecordService;

    @RequestMapping(value = "/approve/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "调停课审批保存", response = Void.class, httpMethod = "POST", notes = "调停课审批保存<br>author xiagen</br>")
    public ResponseEntity<Map<String, Object>> save(@RequestHeader("Authorization") String accessToken, @RequestBody CourseApproveDomain courseApproveDomain) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (StringUtils.isEmpty(courseApproveDomain.getApproveType())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "申请类型不能为空");
        }
        if (StringUtils.isEmpty(courseApproveDomain.getContext())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "申请详情不能为空");
        }
        if (0L == courseApproveDomain.getApproveUserId()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "审批人不能为空");
        }
        CourseApprove courseApprove = courseApproveService.save(courseApproveDomain, accountDTO);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, courseApprove.getId());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/updateState", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "调停课审批状态修改（通过、撤销、拒绝）", response = Void.class, httpMethod = "PUT", notes = "调停课审批状态修改<br>author xiagen</br>")
    public ResponseEntity<Map<String, Object>> save(@RequestHeader("Authorization") String accessToken, @RequestBody ApproveStateDomain approveStateDomain) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (approveStateDomain.getCourseApproveId() == 0L) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "审批id不能为空");
        }
        CourseApprove courseApprove = courseApproveService.findByOne(approveStateDomain.getCourseApproveId());
        if (null == courseApprove) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "审批信息不存在");
        }
        courseApproveService.updateApproveState(approveStateDomain, accountDTO);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/mySend", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "用户发送的申请", response = Void.class, httpMethod = "GET", notes = "用户发送的申请<br>@author xiagen</br>")
    public ResponseEntity<Map<String, Object>> findSendApprove(@ApiParam(value = "accessToken 授权token", required = true) @RequestHeader(value = "Authorization") String accessToken,
                                                               @ApiParam(value = "approveState 审批状态，10：审批中，20：审批拒绝，30：审批通过，40：审批撤销") @RequestParam(value = "approveState", required = false) Integer approveState,
                                                               @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                               @ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = 10;
        }
        result = courseApproveService.findMySendApprove(pageNumber, pageSize, result, accountDTO, approveState);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/receive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "用户收到的申请", response = Void.class, httpMethod = "GET", notes = "用户收到的申请<br>@author xiagen</br>")
    public ResponseEntity<Map<String, Object>> findReceiveApprove(@ApiParam(value = "accessToken 授权token", required = true) @RequestHeader(value = "Authorization") String accessToken,
                                                                  @ApiParam(value = "isApprove true:已审批，false:未审批") @RequestParam(value = "isApprove") boolean isApprove,
                                                                  @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                  @ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = 10;
        }
        result = courseApproveService.findReceiveApprove(result, accountDTO, pageNumber, pageSize, isApprove);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "审批详情", response = Void.class, httpMethod = "GET", notes = "审批详情<br>@author xiagen</br>")
    public ResponseEntity<Map<String, Object>> findApprove(@ApiParam(value = "accessToken 授权token", required = true) @RequestHeader(value = "Authorization") String accessToken,
                                                           @ApiParam(value = "id 审批信息id", required = true) @RequestParam(value = "id", required = true) Long id) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == id || 0L == id) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "id不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        CourseApproveDomainV2 courseApproveDomainV2 = courseApproveService.findByCourseApproveInfo(id);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, courseApproveDomainV2);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/adjustMsg/post", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "调停课通知", response = Void.class, httpMethod = "POST", notes = "调停课通知 暂时取消<br>@author xiagen</br>")
    public ResponseEntity<Map<String, Object>> adjustCoursePost(@ApiParam(value = "accessToken 授权token", required = true) @RequestHeader(value = "Authorization") String accessToken, @RequestBody TeachingClassDomain teachingClassDomain) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == teachingClassDomain.getId() || 0L == teachingClassDomain.getId()) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "教学班id不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        adjustCourseScheduleRecordService.adjustCourseMsg(teachingClassDomain.getId(), accessToken, accountDTO.getName());
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        result.put(ApiReturnConstants.DATA, "调停课通知发送成功！");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/adjustRecord/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "调停课记录列表", response = Void.class, httpMethod = "GET", notes = "调停课记录列表<br>@author xiagen</br>")
    public ResponseEntity<Map<String, Object>> findAdjustCourseList(@ApiParam(value = "accessToken 授权token", required = true) @RequestHeader(value = "Authorization") String accessToken,
                                                                    @ApiParam(value = "pageNumber 起始页", required = false) @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                                    @ApiParam(value = "pageSize 分页大小", required = false) @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        AccountDTO accountDTO = ddUserService.getUserInfoWithLogin(accessToken);
        if (null == accountDTO) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "无权限");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = 1;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = 10;
        }
        result = adjustCourseScheduleRecordService.findByUserIdAdjustCourseList(pageNumber, pageSize, accountDTO.getId(), result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
