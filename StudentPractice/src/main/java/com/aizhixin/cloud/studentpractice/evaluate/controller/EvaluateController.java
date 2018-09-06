/**
 * 
 */
package com.aizhixin.cloud.studentpractice.evaluate.controller;

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
import com.aizhixin.cloud.studentpractice.common.domain.UserInfoDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.remote.PublicMobileService;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.RedisDataService;
import com.aizhixin.cloud.studentpractice.evaluate.core.EvaluateCode;
import com.aizhixin.cloud.studentpractice.evaluate.domain.EvaluateDomain;
import com.aizhixin.cloud.studentpractice.evaluate.domain.QueryEvaluateDomain;
import com.aizhixin.cloud.studentpractice.evaluate.entity.Evaluate;
import com.aizhixin.cloud.studentpractice.evaluate.service.EvaluateService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/evaluate")
@Api(description = "实践报告API")
public class EvaluateController {

	@Autowired
	private EvaluateService evaluateService;
	@Autowired
	private PublicMobileService publicMobileService;
	@Autowired
	private RedisDataService redisDataService;
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
	@ApiOperation(httpMethod = "POST", value = "创建/修改实践评价", response = Void.class, notes = "创建/修改实践评价<br><br><b>@author 郑宁</b>")
	public Evaluate add(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>advice:评价建议<br>evaluateType:评价类型(s:学生自评,stc:学生对辅导员,stm:学生对导师,cts:辅导员对学生,mts:导师对学生)<br>counselorId：辅导员id,counselorName：辅导员名称,couJobNum:辅导员工号(评价辅导员时必填)<br>mentorId：导师id,mentorName：导师名称(评价导师时必填)<br>studentId：学生id,studentName：学生名称,stuJobNum:学生学号(评价学生时必填)<br><b>选填:、</b><br>firstEvaluate：第一项评分(0-10分,两分为一颗星)<br>secondEvaluate：第二项评分(0-10分,两分为一颗星)<br>") @Valid @RequestBody EvaluateDomain domain,
			BindingResult bindingResult) {
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		// UserInfoDomain dto = new UserInfoDomain();
		// dto.setId(169688L);
		// Set<String> role = new HashSet<String>();
		// role.add(RoleCode.ROLE_STUDENT);
		// dto.setRoleNames(role);
		// dto.setName("杨坤");
		// dto.setWorkNo("8");
		// dto.setOrgId(144L);

		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		domain.setOrgId(dto.getOrgId());
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStudentId(dto.getId());
			domain.setStudentName(dto.getName());
			domain.setStuJobNum(dto.getWorkNo());
		} else if (roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)) {// 辅导员
			domain.setCounselorId(dto.getId());
			domain.setCounselorName(dto.getName());
			domain.setCouJobNum(dto.getWorkNo());
		} else {
			domain.setMentorId(dto.getId());
			domain.setMentorName(dto.getName());
		}
		checkData(domain, bindingResult);
		Evaluate evaluate = evaluateService.save(domain, dto);
		redisDataService.delEvaluateInfor(dto.getId(),evaluate.getGroupId());
		return evaluate;
	}

	private void checkData(EvaluateDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if (StringUtils.isEmpty(domain.getEvaluateType())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "实践评价类型不能为空");
		} else {
			// if(EvaluateCode.EVALUATE_STUDENT_SELF.equals(domain.getEvaluateType())){
			// }
			if (EvaluateCode.EVALUATE_COUNSELOR_TO_STUDENT.equals(domain
					.getEvaluateType())
					|| EvaluateCode.EVALUATE_MENTOR_TO_STUDENT.equals(domain
							.getEvaluateType())) {
				if (null == domain.getStudentId()) {
					throw new CommonException(ErrorCode.ID_IS_REQUIRED,
							"被评价学生id不能为空");
				}
				if (null == domain.getStudentName()) {
					throw new CommonException(ErrorCode.ID_IS_REQUIRED,
							"被评价学生名称不能为空");
				}
			}
			if (EvaluateCode.EVALUATE_STUDENT_TO_COUNSELOR.equals(domain
					.getEvaluateType())) {
				if (null == domain.getCounselorId()) {
					throw new CommonException(ErrorCode.ID_IS_REQUIRED,
							"被评价辅导员id不能为空");
				}
			}
			if (EvaluateCode.EVALUATE_STUDENT_TO_MENTOR.equals(domain
					.getEvaluateType())) {
				if (null == domain.getMentorId()) {
					throw new CommonException(ErrorCode.ID_IS_REQUIRED,
							"被评价导师id不能为空");
				}
			}
		}
		if (StringUtils.isEmpty(domain.getAdvice())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "实践评价建议不能为空");
		}
	}

	@RequestMapping(value = "/detail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "实践评价详情", response = Void.class, notes = "实践评价详情<br><br><b>@author 郑宁</b>")
	public EvaluateDomain detail(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "evalId 评价id", required = true) @RequestParam(value = "evalId", required = true) String evalId) {
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}

		return evaluateService.findDetail(evalId);
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "根据条件判断是否实践评价", response = Void.class, notes = "根据条件判断是否实践评价<br><br><b>@author 郑宁</b>")
	public PageData page(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>groupId:辅导员所在实践参与计划id(辅导员角色查询必填)<br>flag:查询标识[ieval:我评价的,evalme:评价我的;默认为ieval]<b>选填:、</b>stuName:学生姓名/学号<br>") @RequestBody QueryEvaluateDomain domain) {
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
//		 UserInfoDomain dto = new UserInfoDomain();
//		 dto.setId(161361L);
//		 Set<String> role = new HashSet<String>();
//		 role.add(RoleCode.ROLE_CLASSROOMTEACHER);
//		 dto.setRoleNames(role);
//		 dto.setName("杨坤");
//		 dto.setWorkNo("004");
//		 dto.setOrgId(215L);

		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		PageData page = null;
		boolean flag = false;
		if (EvaluateCode.EVALUATE_TYPE_I_EVALUATE.equals(domain.getFlag())
				&& StringUtils.isEmpty(domain.getStuName())) {
			page = redisDataService.getEvaluateInfor(dto.getId(),domain.getGroupId());
			flag = true;
		}
		if (null == page) {
			if(null == domain.getGroupId()){
				Set<String> roles = dto.getRoleNames();
				if (roles.contains(RoleCode.ROLE_STUDENT)) {
					StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
					if(null != stuDTO){
						domain.setGroupId(stuDTO.getTrainingGroupId());
					}else{
						return new PageData();
					}
				}
			}
			page = evaluateService.page(domain, dto);
			if (flag) {
				if (null != page) {
					redisDataService.cacheEvaluateInfor(dto.getId(),domain.getGroupId(),page);
				}
			}
		}
		return page;
	}

}
