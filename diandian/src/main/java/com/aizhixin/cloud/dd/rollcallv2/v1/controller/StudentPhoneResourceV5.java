package com.aizhixin.cloud.dd.rollcallv2.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ScheduleConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PageInfo;
import com.aizhixin.cloud.dd.rollcall.dto.SignInDTO;
import com.aizhixin.cloud.dd.rollcall.dto.StudentScheduleDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.rollcallv2.service.ScheduleRollCallServiceV5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LIMH
 * 切换去掉new，注释掉以前旧接口
 * @date 2017/12/26
 */
@RestController
@RequestMapping("/api/phone/v1/new")
@Api(value = "手机学生端API", description = "针对手机端的相关API")
public class StudentPhoneResourceV5 {

    private final Logger log = LoggerFactory.getLogger(StudentPhoneResourceV5.class);

    @Autowired
    private DDUserService ddUserService;

    @Lazy
    @Autowired
    private ScheduleRollCallServiceV5 scheduleRollCallServiceV5;

    /**
     * 查询学生当天课程列表
     *
     * @param accessToken
     * @param teachTime
     * @param offset
     * @param limit
     * @return
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/courselist/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生课程列表", response = Void.class, notes = "查询学生课程列表<br>@author meihua.li")
    public ResponseEntity<?> getStudentCourseList(
        @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
        @ApiParam(value = "teachTime 时间") @RequestParam(value = "teachTime", required = false) String teachTime,
        @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
        @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        PageInfo page = null;
        try {
            page = scheduleRollCallServiceV5.getStudentCourseListDay(PageUtil.createNoErrorPageRequest(offset, limit), account, teachTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/students/courselist/getV2", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "查询学生课程列表", response = Void.class, notes = "查询学生课程列表<br>@author meihua.li")
    public ResponseEntity<?> getStudentCourseListV2(
        @ApiParam(value = "token信息:</b><br/>需要在http header添加登录过程中获取到的token值,必填<br/>示例：bearer xxxxx") @RequestHeader("Authorization") String accessToken,
        @ApiParam(value = "teachTime 时间") @RequestParam(value = "teachTime", required = false) String teachTime,
        @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
        @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }

        PageInfo page = null;
        try {
            page = scheduleRollCallServiceV5.getStudentCourseListDay_VS2(PageUtil.createNoErrorPageRequest(offset, limit), account, teachTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<PageInfo>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/students/signCourse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "签到课程 优化", response = Void.class, notes = "签到课程 优化<br>@author meihua.li")
    public ResponseEntity<?> getStudentSignCourseV2(@RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<StudentScheduleDTO> studentSignCourse = scheduleRollCallServiceV5.getStudentSignCourse(account.getOrganId(), account.getId());
        if (null == studentSignCourse) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(studentSignCourse, HttpStatus.OK);
    }

    @RequestMapping(value = "/student/signIn", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "签到优化", response = Void.class, notes = "签到<br>@author meihua.li")
    public ResponseEntity<?> signIn(@ApiParam(value = "点名信息") @RequestBody SignInDTO signInDTO, @RequestHeader("Authorization") String accessToken) {

        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);

        }
        String rollCallType = signInDTO.getRollCallType();
        if (!ScheduleConstants.TYPE_ROLL_CALL_AUTOMATIC.equals(rollCallType) && !ScheduleConstants.TYPE_ROLL_CALL_DIGITAL.equals(rollCallType)) {
            Map<String, Object> resBody = new HashMap<>();
            resBody.put(ApiReturnConstants.MESSAGE, "rollcalltype is error !");
            return new ResponseEntity<Object>(resBody, HttpStatus.BAD_REQUEST);
        }
        // 验证 验证码和经纬度是否合法

        Object resBody = scheduleRollCallServiceV5.excuteSignIn(account, signInDTO);
        return new ResponseEntity<Object>(resBody, HttpStatus.OK);
    }

}
