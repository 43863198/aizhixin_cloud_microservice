package com.aizhixin.cloud.dd.feedback.v1.controller;

import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.domain.PageData;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.ReturnConstants;
import com.aizhixin.cloud.dd.feedback.domain.FeedbackRecordDomain;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackRecordDTO;
import com.aizhixin.cloud.dd.feedback.service.RecordService;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackTempletType;
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

/**
 * @author hsh
 */
@RequestMapping("/api/phone/v1/feedback/teaching")
@RestController
@Api(value = "教学反馈相关API", description = "教学反馈相关API")
public class TeachingController {
    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private RecordService recordService;

    /**
     * 学生获取教学反馈列表
     */
    @RequestMapping(value = "/student/getFeelbackList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "学生获取教学反馈列表", response = Void.class, notes = "学生获取教学反馈列表<br>@author hsh")
    public ResponseEntity<?> getStuFeelbackList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<FeedbackRecordDomain> result = recordService.queryListByJobNum(pageable, account.getOrganId(), account.getPersonId(), FeedbackTempletType.TEACHING.getType());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 获取教学反馈列表
     */
    @RequestMapping(value = "/getFeelbackList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学反馈列表", response = Void.class, notes = "获取教学反馈列表<br>@author hsh")
    public ResponseEntity<?> getFeelbackList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "orgId") @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "学生名或者学号") @RequestParam(value = "stuName", required = false) String stuName,
            @ApiParam(value = "教师名或者工号") @RequestParam(value = "teacherName", required = false) String teacherName,
            @ApiParam(value = "课程名") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        PageData<FeedbackRecordDomain> result = recordService.queryList(pageable, orgId, stuName, teacherName, courseName, FeedbackTempletType.TEACHING.getType());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 获取教学反馈
     */
    @RequestMapping(value = "/getFeelback", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学反馈", response = Void.class, notes = "获取教学反馈<br>@author hsh")
    public ResponseEntity<?> getFeelback(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "id") @RequestParam(value = "id") Long id) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        FeedbackRecordDomain feedbackRecordDomain = recordService.queryFeedbackRecord(id);
        return new ResponseEntity(feedbackRecordDomain, HttpStatus.OK);
    }

    /**
     * 保存教学反馈
     */
    @RequestMapping(value = "/student/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存教学反馈", response = Void.class, notes = "保存教学反馈<br>@author hsh")
    public ResponseEntity<?> saveFeedback(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "模板") @RequestBody FeedbackRecordDTO feedback) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            feedback.setType(FeedbackTempletType.TEACHING.getType());
            recordService.saveRecord(feedback, account);
            result.put("success", Boolean.TRUE);
        } catch (Exception ex) {
            result.put("success", Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * 导出教学反馈列表
     */
    @RequestMapping(value = "/exportFeelbackList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "导出教学反馈列表", response = Void.class, notes = "导出教学反馈列表<br>@author hsh")
    public ResponseEntity<?> exportFeelbackList(
            @RequestHeader("Authorization") String accessToken,
            @ApiParam(value = "orgId") @RequestParam(value = "orgId", required = true) Long orgId,
            @ApiParam(value = "学生名或者学号") @RequestParam(value = "stuName", required = false) String stuName,
            @ApiParam(value = "教师名或者工号") @RequestParam(value = "teacherName", required = false) String teacherName,
            @ApiParam(value = "课程名") @RequestParam(value = "courseName", required = false) String courseName,
            @ApiParam(value = "pageNumber 起始页") @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @ApiParam(value = "pageSize 每页的限制数目") @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10000;
        }
        Pageable pageable = PageUtil.createNoErrorPageRequest(pageNumber, pageSize);
        String filename = "教学反馈.xlsx";
        String url = recordService.exportList(pageable, orgId, stuName, teacherName, courseName, FeedbackTempletType.TEACHING.getType(), filename);
        Map<String, Object> result = new HashMap<>();
        if (url != null) {
            result.put("fileName", filename);
            result.put(ReturnConstants.RETURN_MESSAGE, url);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.TRUE);
        } else {
            result.put("fileName", filename);
            result.put(ReturnConstants.RETURN_MESSAGE, null);
            result.put(ReturnConstants.RETURN_SUCCESS, Boolean.FALSE);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
