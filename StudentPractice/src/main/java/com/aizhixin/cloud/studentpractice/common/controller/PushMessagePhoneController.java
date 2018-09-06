package com.aizhixin.cloud.studentpractice.common.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.PageUtil;
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageStatusDTO;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingGroupInfoDTO;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.util.DateUtil;


@RestController
@RequestMapping("/v1/message")
@Api(value = "消息API", description = "针对消息操作API")
public class PushMessagePhoneController {

    private final Logger log = LoggerFactory
            .getLogger(PushMessagePhoneController.class);

    @Autowired
    private PushMessageService pushMessageService;
	@Autowired
	private AuthUtilService authUtilService;

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
    @ApiOperation(value = "用户获取推送的消息,并将其置为已读", httpMethod = "GET", response = Void.class, notes = "用户获取推送的消息<br><br> practiceTask 实践任务<br><br><b>@author 郑宁</b>")
    public ResponseEntity<?> get(

            @ApiParam(value = "模块 必填") @RequestParam(value = "module", required = true) String module,
            @ApiParam(value = "方法 必填") @RequestParam(value = "function", required = true) String function,
            @ApiParam(value = "offset 起始页") @RequestParam(value = "offset", required = false) Integer offset,
            @ApiParam(value = "limit 每页的限制数目") @RequestParam(value = "limit", required = false) Integer limit,
            @RequestHeader("Authorization") String accessToken) {

    	Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(accessToken);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}

        Sort sort = new Sort(Sort.Direction.DESC, "lastModifiedDate");
        result = pushMessageService
                .getMessageByModuleAndFunctionAndUserId(
                        PageUtil.createNoErrorPageRequestAndSort(offset, limit, sort),
                        module, function, dto.getId());
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
   
}
