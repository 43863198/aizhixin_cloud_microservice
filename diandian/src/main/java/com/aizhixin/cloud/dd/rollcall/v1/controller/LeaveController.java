package com.aizhixin.cloud.dd.rollcall.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.rollcall.domain.LeaveDomain;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.LeaveDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcall.service.UserService;
import com.aizhixin.cloud.dd.rollcall.serviceV2.LeaveServiceV2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hsh
 */
@RequestMapping("/api/phone/v1/leave")
@RestController
@Api(value = "请假相关API", description = "请假相关API")
public class LeaveController {
    @Autowired
    private LeaveServiceV2 leaveService;
    @Autowired
    private DDUserService ddUserService;
    @Autowired
    private UserService userService;

    /**
     * 获取请假类型
     */
    @RequestMapping(value = "/student/getLeaveType", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取请假类型", response = Void.class, notes = "获取请假类型<br>@author hsh")
    public ResponseEntity<?> getLeaveType(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = leaveService.getLeaveType();
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/requestleave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "学生请假申请", response = Void.class, notes = "学生请假申请<br>@author hsh")
    public ResponseEntity<?> requestLeave(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "请假信息", required = true) @RequestBody LeaveDTO dto) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> teacherMap = userService.getClassTeacherByStudentId(account.getId());
        Long headTeacherId = null;
        String headTeacherName = "";
        if (null != teacherMap && teacherMap.size() > 0) {
            Integer temp = (Integer) teacherMap.get("id");
            if (null != temp) {
                headTeacherId = temp.longValue();
                headTeacherName = String.valueOf(teacherMap.get("name"));
            }
        }
        Date start = DateFormatUtil.parse2(dto.getStartDate(), "yyyy-MM-dd HH:mm");
        Date end = DateFormatUtil.parse2(dto.getEndDate(), "yyyy-MM-dd HH:mm");
        Object res = leaveService.requestLeave(account, dto.getLeaveType(), start, end, dto.getContent(), headTeacherId, headTeacherName, accessToken, dto.getAppealFiles());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @RequestMapping(value = "/teacher/getLeaveList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "教师查询请假审批记录", response = Void.class, notes = "教师查询请假审批记录<br>@author hsh")
    public ResponseEntity<?> getLeaveList(@RequestHeader("Authorization") String accessToken,
                                          @ApiParam(value = "stuName") @RequestParam(value = "stuName", required = false) String stuName,
                                          @ApiParam(value = "teacherName") @RequestParam(value = "teacherName", required = false) String teacherName,
                                          @ApiParam(value = "status 1:通过 0:不通过") @RequestParam(value = "status", required = false) Integer status,
                                          @ApiParam(value = "className") @RequestParam(value = "className", required = false) String className,
                                          @ApiParam(value = "leavePublic 1:公假 0:私假") @RequestParam(value = "leavePublic", required = false) Integer leavePublic,
                                          @ApiParam(value = "leaveType") @RequestParam(value = "leaveType", required = false) Integer leaveType,
                                          @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequestAndSortType(pageNumber, pageSize, "desc", "id");
        PageData<LeaveDomain> result = leaveService.getLeaveList(pageable, account.getOrganId(), stuName, teacherName, status, className, leavePublic, leaveType);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/init/leavedata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "补全旧请假数据", response = Void.class, notes = "补全旧请假数据<br>@author hsh")
    public ResponseEntity<?> initLeaveData(@RequestHeader("Authorization") String accessToken) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        leaveService.initLeaveData();
        Map<String, Object> result = new HashMap<>();
        result.put(ApiReturnConstants.SUCCESS, Boolean.TRUE);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
