package com.aizhixin.cloud.dd.messege.v1.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.dd.common.core.ApiReturnConstants;
import com.aizhixin.cloud.dd.common.core.PageUtil;
import com.aizhixin.cloud.dd.common.utils.DateFormatUtil;
import com.aizhixin.cloud.dd.common.utils.TokenUtil;
import com.aizhixin.cloud.dd.constant.PushMessageConstants;
import com.aizhixin.cloud.dd.constant.RoleConstants;
import com.aizhixin.cloud.dd.rollcall.dto.AccountDTO;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageDTOV2;
import com.aizhixin.cloud.dd.rollcall.dto.PushMessageStatusDTO;
import com.aizhixin.cloud.dd.rollcall.service.DDUserService;
import com.aizhixin.cloud.dd.messege.service.PushMessageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/phone/v1")
@Api(value = "消息API", description = "针对消息操作API")
public class PushMessagePhoneController {

    private final Logger log = LoggerFactory.getLogger(PushMessagePhoneController.class);

    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private DDUserService ddUserService;

    /**
     * 用户获取推送的消息
     *
     * @param module
     * @param function
     * @param offset
     * @param limit
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "用户获取推送的消息,并将其置为已读", httpMethod = "GET", response = Void.class, notes = "用户获取推送的消息<br><br> rollcall 点名模块   leave 请假模块 <br> student_remind 学生提醒(rollcall) teacher_notice 上课老师请假通知(leave)  teacher_approval 请假审批通知 (leave) student_notice 请假审批通知(leave)  revert_notice 回复模块(revert)<br><b>@author 杨立强</b>")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String accessToken,
                                 @ApiParam(value = "模块 必填") @RequestParam(value = "module", required = true) String module,
                                 @ApiParam(value = "方法 必填") @RequestParam(value = "function", required = true) String function,
                                 @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
                                 @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
        Object result = pushMessageService.getMessageByModuleAndFunctionAndUserId(
                PageUtil.createNoErrorPageRequestAndSort(offset, limit, sort), module, function, account.getId());
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    /**
     * 用户获取推送的消息的状态
     *
     * @param module
     * @param function
     * @param accessToken
     * @return
     */
    @RequestMapping(value = "/getstatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "用户获取推送的消息", httpMethod = "GET", response = Void.class, notes = "用户获取推送的消息<br><br><b>@author 杨立强</b>")
    public ResponseEntity<?> getstatus(@RequestHeader("Authorization") String accessToken,
                                       @ApiParam(value = "模块 选填") @RequestParam(value = "module", required = false) String module,
                                       @ApiParam(value = "方法 选填") @RequestParam(value = "function", required = false) String function) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        List<PushMessageStatusDTO> result = pushMessageService.getMessageStatus(module, function, account.getId());
        if (null != result && result.size() == 0) {
            result.add(getDefaultData(account.getRole()));
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    public PushMessageStatusDTO getDefaultData(String role) {
        PushMessageStatusDTO pmDto = new PushMessageStatusDTO();
        pmDto.setModule(PushMessageConstants.MODULE_LEAVE);
        if (!RoleConstants.ROLE_STUDENT.equals(role)) {
            pmDto.setFunction(PushMessageConstants.FUNCTION_TEACHER_APPROVAL);
        } else {
            pmDto.setFunction(PushMessageConstants.FUNCTION_STUDENT_NOTICE);
        }
        pmDto.setLastPushTime(DateFormatUtil.format(new Date()));
        pmDto.setPushCount(0);
        pmDto.setNotRead(0);
        return pmDto;
    }

    /**
     * 删除消息
     *
     * @param accessToken
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除消息", httpMethod = "DELETE", response = Void.class, notes = "删除消息")
    public ResponseEntity<?> deleteMessage(@RequestHeader("Authorization") String accessToken,
                                           @ApiParam(value = "id 消息id 必填") @RequestParam(value = "id", required = true) Long id) {
        AccountDTO account = ddUserService.getUserInfoWithLogin(accessToken);
        if (account == null) {
            return new ResponseEntity<Object>(TokenUtil.tokenValid(), HttpStatus.UNAUTHORIZED);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            pushMessageService.delete(id);
            result.put(ApiReturnConstants.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            result.put(ApiReturnConstants.SUCCESS, false);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "批量保存消息", httpMethod = "POST", response = Void.class, notes = "批量保存消息")
    public ResponseEntity<Map<String, Object>> saveMessage(@RequestBody PushMessageDTOV2 pushMessageDTOV2) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isEmpty(pushMessageDTOV2.getModule())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "模块不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(pushMessageDTOV2.getFunction())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "模块函数不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(pushMessageDTOV2.getTitle())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "模块主题不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        if (StringUtils.isEmpty(pushMessageDTOV2.getContent())) {
            result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
            result.put(ApiReturnConstants.CAUSE, "模块消息内容不能为空");
            return new ResponseEntity<>(result, HttpStatus.EXPECTATION_FAILED);
        }
        pushMessageService.saveListPushMessagev2(pushMessageDTOV2);
        result.put(ApiReturnConstants.RESULT, Boolean.TRUE);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
