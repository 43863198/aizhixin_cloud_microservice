package com.aizhixin.cloud.orgmanager.remote;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.aizhixin.cloud.orgmanager.common.domain.CountDomain;
import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;

@FeignClient(name = "student-practice")
public interface StudentPracticeService {

	@RequestMapping(value = "/v1/syn/syngroupname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "同步修改实践计划名称", response = Void.class, notes = "同步修改实践计划名称<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> synGroupName(
			@ApiParam(value = "<b>必填:</b><br>id:实践计划id<br>name:实践计划名称") @RequestBody IdNameDomain domain);

	@RequestMapping(value = "/v1/practice/synstuinfor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "同步实践计划学生信息", response = Void.class, notes = "同步实践计划学生信息 <br>@jianwei.wu")
	public void queryTrainGroupInfo(
			@ApiParam(value = "groupId 实践计划id", required = true) @RequestParam(value = "groupId") Long groupId);
	
	@RequestMapping(value = "/v1/practice/whetherissued",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(httpMethod = "GET", value = "判断实践计划是否下发过指定的实践课程", response = Void.class, notes = "判断实践计划是否下发过指定的实践课程 <br>@zhengning")
    public List<CountDomain> whetherIssued(
            @ApiParam(value = "weekTaskId 实践课程id", required = true) @RequestParam(value = "weekTaskId")  String weekTaskId,
            @ApiParam(value = "orgId 机构id", required = true) @RequestParam(value = "orgId")  Long orgId);
}