package com.aizhixin.cloud.dd.classperf.controller;

import com.aizhixin.cloud.dd.classperf.domain.ClassPerfTeacherDomain;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfLog;
import com.aizhixin.cloud.dd.classperf.entity.ClassPerfStudent;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/web/v1/classperf")
@Api(description = "日常打分web")
public class ClassPerfWebController {
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private ClassPerfService classPerfService;

    @RequestMapping(value = "/teacher/updateLimitScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改教师限制分", response = Void.class, notes = "修改教师限制分<br>@author hsh")
    public ResponseEntity<?> updateLimitScore(@RequestHeader("Authorization") String accessToken,
                                              @ApiParam(value = "teacherId") @RequestParam(value = "teacherId", required = false) Long teacherId,
                                              @ApiParam(value = "sorce") @RequestParam(value = "sorce", required = false) Integer sorce) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = classPerfService.updateLimitScore(account.getOrganId(), teacherId, sorce);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/updateAllTeacherLimitScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "修改所有教师限制分", response = Void.class, notes = "修改所有教师限制分<br>@author hsh")
    public ResponseEntity<?> updateAllTeacherLimitScore(@RequestHeader("Authorization") String accessToken,
                                                        @ApiParam(value = "sorce") @RequestParam(value = "sorce", required = false) Integer sorce) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = classPerfService.updateAllTeacherLimitScore(account.getOrganId(), sorce);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getTeacherList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "所有教师列表", response = Void.class, notes = "所有教师列表<br>@author hsh")
    public ResponseEntity<?> getTeacherList(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "collegeId") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                            @ApiParam(value = "name") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "id");
        PageData<ClassPerfTeacherDomain> result = classPerfService.getTeacherList(pageable, account.getOrganId(), collegeId, name);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getStudentList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分", response = Void.class, notes = "学生累计得分<br>@author hsh")
    public ResponseEntity<?> getStudentList(@RequestHeader("Authorization") String accessToken,
                                            @ApiParam(value = "collegeId") @RequestParam(value = "collegeId", required = false) Long collegeId,
                                            @ApiParam(value = "profId") @RequestParam(value = "profId", required = false) Long profId,
                                            @ApiParam(value = "classesId") @RequestParam(value = "classesId", required = false) Long classesId,
                                            @ApiParam(value = "name") @RequestParam(value = "name", required = false) String name,
                                            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "totalScore");
        PageData<ClassPerfStudent> result = classPerfService.getStudentList(pageable, account.getOrganId(), collegeId, profId, classesId, name);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getStudentLogList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分记录", response = Void.class, notes = "学生累计得分记录<br>@author hsh")
    public ResponseEntity<?> getStudentLogList(@RequestHeader("Authorization") String accessToken,
                                               @ApiParam(value = "classPerfId") @RequestParam(value = "classPerfId", required = false) String classPerfId,
                                               @ApiParam(value = "teacherName") @RequestParam(value = "teacherName", required = false) String teacherName,
                                               @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                               @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "DESC", "createdDate");
        PageData<ClassPerfLog> result = classPerfService.getStudentLogList(pageable, classPerfId, teacherName);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/initAllTeacherLimitScore", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生累计得分记录", response = Void.class, notes = "学生累计得分记录<br>@author hsh")
    public ResponseEntity<?> getStudentLogList(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        classPerfService.initAllTeacherLimitScore();
        Map<String, Object> result = new HashMap<>();
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
