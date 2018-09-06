package com.aizhixin.cloud.dd.appeal.controller;

import com.aizhixin.cloud.dd.appeal.domain.AppealDomain;
import com.aizhixin.cloud.dd.appeal.dto.AppealDTO;
import com.aizhixin.cloud.dd.appeal.service.AppealService;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/phone/v2/appeal")
@Api(description = "申诉")
public class AppealController {

    @Autowired
    private AppealService appealService;
    @Autowired
    private DDUserService ddUserService;

    @RequestMapping(value = "/student/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "学生创建申诉", response = Void.class, notes = "学生创建申诉<br>@author hsh")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String accessToken,
                                    @ApiParam(value = "申诉信息", required = true) @RequestBody AppealDTO appealDTO) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = appealService.create(accessToken, account, appealDTO);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/getlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生查询申诉列表", response = Void.class, notes = "学生查询申诉列表<br>@author hsh")
    public ResponseEntity<?> getListByStudent(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "10:待审核 20:通过，30:不通过") @RequestParam(value = "appealStatus", required = false) Integer appealStatus,
                                              @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "id");
        PageData<AppealDomain> result = appealService.getListByStudent(account.getId(), pageable, appealStatus);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getlist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师查询申诉列表", response = Void.class, notes = "教师查询申诉列表<br>@author hsh")
    public ResponseEntity<?> getListByTeacher(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "10:待审核 20:通过，30:不通过") @RequestParam(value = "appealStatus", required = false) Integer appealStatus,
                                              @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "id");
        PageData<AppealDomain> result = appealService.getListByTeacher(account.getId(), pageable, appealStatus);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/approval", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "教师审批申诉", response = Void.class, notes = "教师审批申诉<br>@author xiagen")
    public ResponseEntity<?> approvalByTeacher(@RequestHeader("Authorization") String accessToken,
                                               @ApiParam(value = "申诉id", required = true) @RequestParam(value = "appealId", required = true) Long appealId,
                                               @ApiParam(value = "20:通过，30:不通过", required = true) @RequestParam(value = "appealStatus", required = true) Integer appealStatus) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        appealService.approvalByTeacher(accessToken, appealId, appealStatus);
        Map<String, Object> result = new HashMap<>();
        result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
