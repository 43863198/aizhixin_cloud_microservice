package com.aizhixin.cloud.dd.feedback.v1.controller;

import com.aizhixin.cloud.dd.common.core.ApiReturn;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.feedback.dto.FeedbackTempletDTO;
import com.aizhixin.cloud.dd.feedback.service.TempletService;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackQuesType;
import com.aizhixin.cloud.dd.feedback.utils.FeedbackTempletType;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hsh
 */
@RequestMapping("/api/phone/v1/feedback/templet")
@RestController
@Api(value = "教学反馈模板相关API", description = "教学反馈模板相关API")
public class TempletController {

    @Autowired
    private DDUserService ddUserService;

    @Autowired
    private TempletService templetService;

    /**
     * 初始化模板
     */
    @RequestMapping(value = "/initTemplet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "初始化模板", response = Void.class, notes = "初始化模板<br>@author hsh")
    public ResponseEntity<?> initTemplet(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "orgId") @RequestParam(value = "orgId") Long orgId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        templetService.initTemplet(orgId);
        Map<String, String> reuslt = new HashMap<>();
        reuslt.put("result", "true");
        return new ResponseEntity(reuslt, HttpStatus.OK);
    }

    /**
     * 获取教学反馈模板
     */
    @RequestMapping(value = "/getTeachingTemplet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取教学反馈模板", response = Void.class, notes = "获取教学反馈模板<br>@author hsh")
    public ResponseEntity<?> listCounsellorGroup(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "orgId") @RequestParam(value = "orgId") Long orgId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        FeedbackTempletDTO ft = templetService.findTempletByOrgid(FeedbackTempletType.TEACHING.getType(), orgId);
        return new ResponseEntity(ft, HttpStatus.OK);
    }

    /**
     * 保存教学反馈模板
     */
    @RequestMapping(value = "/saveTeachingTemplet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存教学反馈模板", response = Void.class, notes = "保存教学反馈模板<br>@author hsh")
    public ResponseEntity<?> saveTeachingTemplet(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "模板") @RequestBody FeedbackTempletDTO templet) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        templetService.saveTemplet(FeedbackTempletType.TEACHING.getType(), templet);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }

    /**
     * 获取督导反馈模板
     */
    @RequestMapping(value = "/getSteeringTemlet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "获取督导反馈模板", response = Void.class, notes = "获取督导反馈模板<br>@author hsh")
    public ResponseEntity<?> getSteeringTemlet(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "orgId") @RequestParam(value = "orgId") Long orgId) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        FeedbackTempletDTO ft = templetService.findTempletByOrgid(FeedbackTempletType.STEERING.getType(), orgId);
        return new ResponseEntity(ft, HttpStatus.OK);
    }

    /**
     * 保存督导反馈模板
     */
    @RequestMapping(value = "/saveSteeringTemplet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "POST", value = "保存督导反馈模板", response = Void.class, notes = "保存督导反馈模板<br>@author hsh")
    public ResponseEntity<?> saveSteeringTemplet(@RequestHeader("Authorization") String accessToken, @ApiParam(value = "模板") @RequestBody FeedbackTempletDTO templet) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        templet.setQuesType(FeedbackQuesType.DAFEN.getType());
        templetService.saveTemplet(FeedbackTempletType.STEERING.getType(), templet);
        return new ResponseEntity(ApiReturn.message(Boolean.TRUE, null, null), HttpStatus.OK);
    }
}
