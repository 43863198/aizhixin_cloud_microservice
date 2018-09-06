/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QueryReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.ReportDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Report;
import com.aizhixin.cloud.studentpractice.summary.service.ReportService;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/report")
@Api(description = "实践报告API")
public class ReportController {

	@Autowired
	private ReportService reportService;
	@Autowired
	private AuthUtilService authUtilService;
	
	/**
	 * 创建实践报告
	 * 
	 * @param domain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "创建/修改实践报告", response = Void.class, notes = "创建/修改实践报告<br><br><b>@author 郑宁</b>")
	public Report add(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>reportTitle:报告标题<br>commitStatus：提交状态save保存,commit提交<b>选填:、</b><br>description:报告描述<br>fileList:附件信息(附件和描述不可同时为空)") @Valid @RequestBody ReportDomain domain,
			BindingResult bindingResult) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
					"未授权");
		}
		checkData(domain, bindingResult);
		return reportService.save(domain, dto);
	}


	private void checkData(ReportDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if(StringUtils.isEmpty(domain.getReportTitle())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践报告标题不能为空");
		}
		if(StringUtils.isEmpty(domain.getDescription()) && domain.getFileList().isEmpty()){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"报告描述和附件不能同时为空");
		}
	}
	
	@RequestMapping(value = "/review", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "审核实践报告", response = Void.class, notes = "审核实践报告<br><br><b>@author 郑宁</b>")
	public Report review(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>id:报告id、<br>status：已通过[finish],被打回[backTo]<br>advice:审核建议<br>") @Valid @RequestBody ReportDomain domain,
			BindingResult bindingResult) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
					"未授权");
		}
		checkData1(domain, bindingResult);
		return reportService.review(domain);
	}
	
	private void checkData1(ReportDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if(StringUtils.isEmpty(domain.getId())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践报告id不能为空");
		}
		if(StringUtils.isEmpty(domain.getStatus())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践报告审核状态不能为空");
		}
		if(StringUtils.isEmpty(domain.getAdvice())){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"实践报告审核意见不能为空");
		}
	}

	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除实践报告", response = Void.class, notes = "删除实践报告<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 报告id", required = true) @RequestParam(value = "id", required = true) String id
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		if(StringUtils.isEmpty(id)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"删除的实践报告id不能为空");
		}
		
		reportService.delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cancel", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "撤回提交实践报告", response = Void.class, notes = "删除实践报告<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> cancel(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 报告id", required = true) @RequestParam(value = "id", required = true) String id
			) {
		Map<String, Object> result = new HashMap<String, Object>();
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		if(StringUtils.isEmpty(id)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"撤销的实践报告id不能为空");
		}
		
		reportService.cancel(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询报告信息", response = Void.class, notes = "分页查询报告信息<br><br><b>@author 郑宁</b>")
	public Map<String, Object> page(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b><br>groupId:实践计划id<br>creatorName:学生姓名<br>ReportTitle:报告标题<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>dayNum:筛选类型:当天(1)一周(7)三个月(90)") @RequestBody QueryReportDomain domain
			) {
		try{
			AccountDTO dto = authUtilService.getSsoUserInfo(token);
			if (null == dto || null == dto.getId()) {
				throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
						"未授权");
			}
			
			
			List<String> roles = dto.getRoleNames();
			if (roles.contains(RoleCode.ROLE_STUDENT)) {
				domain.setUserId(dto.getId());
				if(null == domain.getGroupId()){
					StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
					if(null != stuDTO){
						domain.setGroupId(stuDTO.getTrainingGroupId());
					}else{
						return new HashMap<String, Object>();
					}
				}
				return  reportService.findPage(domain,token);
			}else{//辅导员
				domain.setCounselorId(dto.getId());
				return  reportService.findPage(domain,token);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(),
					"查询出错");
		}
		
	}
	
	
	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "报告详情", response = Void.class, notes = "报告详情<br><br><b>@author 郑宁</b>")
	public ReportDomain detail(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "id 报告id", required = true) @RequestParam(value = "id", required = true) String id
			) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
					"未授权");
		}
		if(StringUtils.isEmpty(id)){
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"删除的周任务的id不能为空");
		}
		
		return reportService.findDetail(id);
	}
}
