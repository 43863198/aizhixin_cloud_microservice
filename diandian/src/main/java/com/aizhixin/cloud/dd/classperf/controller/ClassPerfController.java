package com.aizhixin.cloud.dd.classperf.controller;

import com.aizhixin.cloud.dd.classperf.dto.ClassPerfBatchDTO;
import com.aizhixin.cloud.dd.classperf.dto.ClassPerfDTO;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLog;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfStudent;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfTeacher;
import com.aizhixin.cloud.dd.classperf.service.ClassPerfService;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phone/v1/classperf")
@Api(description = "日常打分手机")
public class ClassPerfController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private ClassPerfService classPerfService;

    @RequestMapping(value = "/teacher/batchRateStudent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "批量打分", response = Void.class, notes = "批量打分<br>@author hsh")
    public ResponseEntity<?> batchRateStudent(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "打分信息", required = true) @RequestBody ClassPerfBatchDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = classPerfService.batchRateStudent(dto, account.getOrganId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/rateStudent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "打分", response = Void.class, notes = "打分<br>@author hsh")
    public ResponseEntity<?> rateStudent(@RequestHeader("Authorization") String accessToken,
                                         @ApiParam(value = "打分信息", required = true) @RequestBody ClassPerfDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = classPerfService.rateStudent(dto);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getTeacherInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师剩余分", response = Void.class, notes = "教师剩余分<br>@author hsh")
    public ResponseEntity<?> getTeacherInfo(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        ClassPerfTeacher result = classPerfService.getTeacherInfo(account.getOrganId(), account.getId());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getStudentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分列表", response = Void.class, notes = "学生累计得分列表<br>@author hsh")
    public ResponseEntity<?> getStudentList(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "name") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "totalScore");
        PageData<ClassPerfStudent> result = classPerfService.getStudentList(pageable, account.getOrganId(), name);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getStudent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分", response = Void.class, notes = "学生累计得分<br>@author hsh")
    public ResponseEntity<?> getStudent(@RequestHeader("Authorization") String accessToken,
                                        @ApiParam(value = "stuId") @RequestParam(value = "stuId", required = false) Long stuId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        ClassPerfStudent result = classPerfService.getStudent(account.getOrganId(), stuId);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getStudentLogList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分记录", response = Void.class, notes = "学生累计得分记录<br>@author hsh")
    public ResponseEntity<?> getStudentLogList(@RequestHeader("Authorization") String accessToken,
                                               @ApiParam(value = "classPerfId") @RequestParam(value = "classPerfId", required = false) String classPerfId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<ClassPerfLog> result = classPerfService.getStudentLogList(classPerfId);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
