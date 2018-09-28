package com.aizhixin.cloud.orgmanager.company.v1.controller;

import com.aizhixin.cloud.orgmanager.common.PageData;
import com.aizhixin.cloud.orgmanager.common.core.ApiReturnConstants;
import com.aizhixin.cloud.orgmanager.common.core.PageUtil;
import com.aizhixin.cloud.orgmanager.company.domain.UserDomain;
import com.aizhixin.cloud.orgmanager.company.dto.StudentRollcallSetLogDTO;
import com.aizhixin.cloud.orgmanager.company.entity.StudentRollcallSet;
import com.aizhixin.cloud.orgmanager.company.service.PauseAttendanceOperationLogService;
import com.aizhixin.cloud.orgmanager.company.service.UserRoleService;
import com.aizhixin.cloud.orgmanager.company.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-10-18
 */
@RestController
@RequestMapping("/v1/pauselog")
@Api(description = "暂停考勤操作的记录日志API")
public class PauseAttendanceOperationLogController {
    @Autowired
    private PauseAttendanceOperationLogService pauseAttendanceOperationLogService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/initLogStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "初始化日志状态", response = Void.class, notes = "初始化日志状态 1qaz2wsx3edc <br><br><b>@hsh</b>")
    public ResponseEntity<Map<String, Object>> initLogStatus(@ApiParam(value = "password", required = true) @RequestParam(value = "password") String password) {
        if ("1qaz2wsx3edc".equals(password)) {
            pauseAttendanceOperationLogService.initLogStatus();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 根据条件查询暂停考勤操作记录日志
     *
     * @param orgId
     * @param criteria
     * @param startTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/getbywhere", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "根据条件查询暂停考勤操作记录日志", response = Void.class, notes = "根据条件查询暂停考勤操作记录日志<br><br><b>@jianwei.wu</b>")
    public PageData<StudentRollcallSetLogDTO> getLogBywhere(
            @ApiParam(value = "orgId 学校ID", required = true) @RequestParam(value = "orgId") Long orgId,
            @ApiParam(value = "managerId 登录用户ID", required = true) @RequestParam(value = "managerId", required = true) Long managerId,
            @ApiParam(value = "collegeId 班级ID") @RequestParam(value = "collegeId", required = false) Long collegeId,
            @ApiParam(value = "criteria 姓名/学号") @RequestParam(value = "criteria", required = false) String criteria,
            @ApiParam(value = "opt 操作类型 10暂停考勤，20恢复考勤") @RequestParam(value = "opt", required = false) Integer opt,
            @ApiParam(value = "startTime 开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(value = "endTime 结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(value = "pageNumber 第几页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页数据的数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<String> userRoles = userRoleService.findByUser(managerId);
        if (isCollegeManager(userRoles)) {
            UserDomain userInfo = userService.getUser(managerId);
            if (null != userInfo) {
                collegeId = userInfo.getCollegeId();
            }
        }
        return pauseAttendanceOperationLogService.getPauseAttendanceLogBywhere(orgId, collegeId, opt, criteria, startTime, endTime, PageUtil.createNoErrorPageRequest(pageNumber, pageSize));
    }

    //判断是否是院级管理  （权限数筛选时使用）
    private boolean isCollegeManager(List<String> roles) {
        if (roles.contains("ROLE_COLLEGE_ADMIN")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_DATAVIEW")) {
            return true;
        } else if (roles.contains("ROLE_COLLEG_EDUCATIONALMANAGER")) {
            return true;
        } else {
            return false;
        }
    }


}
