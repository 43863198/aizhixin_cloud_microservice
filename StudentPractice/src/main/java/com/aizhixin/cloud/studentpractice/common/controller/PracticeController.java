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
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.PageUtil;
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.CountDomain;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageStatusDTO;
import com.aizhixin.cloud.studentpractice.common.domain.TrainingGroupInfoDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.remote.OrgMangerService;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.util.DateUtil;
import com.aizhixin.cloud.studentpractice.task.service.PracticeTaskService;
import com.aizhixin.cloud.studentpractice.task.service.WeekTaskTeamService;


@RestController
@RequestMapping("/v1/practice")
@Api(value = "实践API", description = "实践API")
public class PracticeController {

    private final Logger log = LoggerFactory
            .getLogger(PracticeController.class);

    @Autowired
    private OrgMangerService orgMangerService;
    @Autowired
    @Lazy
    private AuthUtilService authUtilService;
    @Autowired
    private PracticeTaskService practiceTaskService;
    @Autowired
    private WeekTaskTeamService weekTaskTeamService;

    
    @RequestMapping(value = "/grouplistbytid",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "（辅导员APP）根据teacherId查询实践参与计划列表", response = Void.class, notes = "（辅导员APP）根据teacherId查询实践参与计划列表 <br>@zhengning")
    public PageData<TrainingGroupInfoDTO> queryGroupListByTid(
    		@RequestHeader("Authorization") String token,
    		@ApiParam(value = "pageNumber 第几页",required = false) @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
    		@ApiParam(value = "pageSize 每页多少条",required = false) @RequestParam(value = "pageSize",required = false) Integer pageSize,
            @ApiParam(value = "teacherId 班主任老师id",required = true) @RequestParam(value = "teacherId" ) Long teacherId,
            @ApiParam(value = "flag 实践计划是否结束标识(end:已结束,notOver:未结束,默认为未结束)",required = false) @RequestParam(value = "flag",required = false) String flag){
    	Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
					"未授权");
		}
		
		return orgMangerService.queryGroupListByTid(pageNumber,pageSize,teacherId, flag);
    }
    
    @RequestMapping(value = "/synstuinfor",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "同步实践计划学生信息", response = Void.class, notes = "同步实践计划学生信息 <br>@jianwei.wu")
    public void queryTrainGroupInfo(
            @ApiParam(value = "groupId 实践计划id", required = true) @RequestParam(value = "groupId")  Long groupId){
    	 practiceTaskService.synStuInfor(groupId);;
    }
    
    @RequestMapping(value = "/whetherissued",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断实践计划是否下发过指定的实践课程", response = Void.class, notes = "判断实践计划是否下发过指定的实践课程 <br>@zhengning")
    public List<CountDomain> whetherIssued(
            @ApiParam(value = "weekTaskId 实践课程id", required = true) @RequestParam(value = "weekTaskId")  String weekTaskId,
            @ApiParam(value = "orgId 机构id", required = true) @RequestParam(value = "orgId")  Long orgId){
    	return weekTaskTeamService.findWeekTaskByGroupIds(weekTaskId,orgId);
    }
}
