/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.controller;

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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.aizhixin.cloud.studentpractice.common.PageData;
import com.aizhixin.cloud.studentpractice.common.PageDomain;
import com.aizhixin.cloud.studentpractice.common.core.ApiReturnConstants;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PublicErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.UserInfoDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.remote.PublicMobileService;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.evaluate.core.EvaluateCode;
import com.aizhixin.cloud.studentpractice.evaluate.domain.QueryEvaluateDomain;
import com.aizhixin.cloud.studentpractice.evaluate.service.EvaluateService;
import com.aizhixin.cloud.studentpractice.score.domain.CounselorCountDomain;
import com.aizhixin.cloud.studentpractice.score.domain.QueryScoreDomain;
import com.aizhixin.cloud.studentpractice.score.domain.ScoreDomain;
import com.aizhixin.cloud.studentpractice.score.service.CounselorCountService;
import com.aizhixin.cloud.studentpractice.summary.domain.EnterpriseCountDomain;
import com.aizhixin.cloud.studentpractice.summary.service.EnterpriseCountService;
import com.aizhixin.cloud.studentpractice.summary.service.ReportService;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryReplyCountService;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStatisticalPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StatisticalDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskStatisticsDomain;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountService;
import com.aizhixin.cloud.studentpractice.task.service.SignStatisticalService;
import com.aizhixin.cloud.studentpractice.task.service.StatisticalService;
import com.aizhixin.cloud.studentpractice.task.service.TaskStatisticalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/taskstatistics")
@Api(description = "实践任务统计API")
public class TaskStatisticsController {
	
	private static Logger log = LoggerFactory
			.getLogger(TaskStatisticsController.class);

	@Autowired
	private TaskStatisticalService taskStatisticsService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	@Lazy
	private SummaryReplyCountService summaryReplyCountService;
	@Autowired
	@Lazy
	private EnterpriseCountService enterpriseCountService;
	@Autowired
	@Lazy
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	@Lazy
	private PeopleCountService peopleCountService;
	@Autowired
	@Lazy
	private ReportService reportService;
	@Autowired
	@Lazy
	private SignStatisticalService signStatisticalService;
	@Autowired
	@Lazy
	private CounselorCountService counselorCountService;
	@Autowired
	private PublicMobileService publicMobileService;
	@Autowired
	@Lazy
	private StatisticalService statisticalService;
	@Autowired
	@Lazy
	private EvaluateService evaluateService;
	
//	@RequestMapping(value = "/stutasklist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "学生实践任务统计", response = Void.class, notes = "学生实践任务统计<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> stuTaskStatistics(
//			@RequestHeader("Authorization") String token,
//			@ApiParam(value = "<b>选填:、</b><br>stuName:学生名称(支持模糊查询)、<br>enterpriseName:实践企业名称(不支持模糊查询)、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result = taskStatisticsService.stuTaskStatistics(domain, dto.getOrgId());
//		
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/taskchart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "按院系，专业，班级统计实践任务图", response = Void.class, notes = "按院系，专业，班级统计实践任务图<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> taskChart(
//			@RequestHeader("Authorization") String token,
//            @ApiParam(value = "classId 班级id") @RequestParam(value = "classId", required = false) Long classId,
//            @ApiParam(value = "professionalId 专业id") @RequestParam(value = "professionalId", required = false) Long professionalId,
//            @ApiParam(value = "collegeId 院系id") @RequestParam(value = "collegeId", required = false) Long collegeId
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.countChart(dto.getOrgId(), classId, professionalId, collegeId));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	
//	@RequestMapping(value = "/peoplecountlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "实践人数统计列表", response = Void.class, notes = "实践人数统计列表<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> peopleCount(
//			@RequestHeader("Authorization") String token,
//			@ApiParam(value = "<b>选填:、</b><br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result = taskStatisticsService.getPeopleCountPage(domain, dto.getOrgId());
//		
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	
//	@RequestMapping(value = "/peopledetaillist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "POST", value = "班级实践统计列表", response = Void.class, notes = "班级实践统计列表<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> peopleCountDetail(
//			@RequestHeader("Authorization") String token,
//			@ApiParam(value = "<b>必填填:、</b><br>classId:班级id、<br><b>选填:、</b><br>stuName:学生名称(支持模糊查询)、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		
//		if(null != domain.getClassId() && domain.getClassId().longValue() > 0L){
//			result = taskStatisticsService.getPeopleCountDetailPage(domain, dto.getOrgId());
//		}else{
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "classId(班级Id)不能为空");
//		}
//		
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/peoplecountchart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "按院系，专业，班级人数统计", response = Void.class, notes = "按院系，专业，班级人数统计<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> peopleCountChart(
//			@RequestHeader("Authorization") String token,
//            @ApiParam(value = "professionalId 专业id") @RequestParam(value = "professionalId", required = false) Long professionalId,
//            @ApiParam(value = "collegeId 院系id") @RequestParam(value = "collegeId", required = false) Long collegeId
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.queryPeopleCount(dto.getOrgId(), professionalId, collegeId));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/peoplecountsumchart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "实践人数汇总统计", response = Void.class, notes = "实践人数汇总统计<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> peopleCountSummaryChart(
//			@RequestHeader("Authorization") String token
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.peopleCountChart(dto.getOrgId(), null, null, null));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/enterprisecountchart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "实践公司人数统计", response = Void.class, notes = "实践公司人数统计<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> enterpriseCountChart(
//			@RequestHeader("Authorization") String token
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.enterpriseCountChart(dto.getOrgId()));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/practicelocationchart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "实践公司所在省市", response = Void.class, notes = "实践公司所在省市<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> enterpriseLocationChart(
//			@RequestHeader("Authorization") String token
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.getEnterpriseLocation(dto.getOrgId()));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/enterprisenamelist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "实践公司名称列表", response = Void.class, notes = "实践公司名称列表<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> enterpriseNameList(
//			@RequestHeader("Authorization") String token
//			) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
//			result.put(ApiReturnConstants.CODE,
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
//			result.put(ApiReturnConstants.CAUSE, "未授权");
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.UNAUTHORIZED);
//		}
//		result.put(ApiReturnConstants.DATA, taskStatisticsService.getEnterpriseList(dto.getOrgId()));
//		return  new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
	
	
	
	@RequestMapping(value = "/summarydetailpage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生参与过程明细表", response = Void.class, notes = "学生参与过程明细表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> summaryDetailPage(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b><br>keyWords:学生姓名或学号、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStuId(dto.getId());
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			domain.setCounselorId(dto.getId());
		}
		
		result = taskStatisticsService.summaryDetailStatistics(domain,dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/summaryreplypage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生周日志明细", response = Void.class, notes = "学生周日志明细<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> summaryReplyPage(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b>groupId:实践计划id<br>keyWords:学生姓名或学号、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStuId(dto.getId());
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			domain.setCounselorId(dto.getId());
		}
		
		result = summaryReplyCountService.summaryReplyPage(domain, dto.getOrgId(), token);
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/summarypage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生周日志统计(辅导员web端)", response = Void.class, notes = "学生周日志统计(辅导员web端)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> summaryPage(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b>groupId:实践计划id<br>keyWords:学生姓名或学号、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStuId(dto.getId());
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			domain.setCounselorId(dto.getId());
		}
		
		result = summaryReplyCountService.summaryPage(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/enterprisecountpage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "实践企业统计表", response = Void.class, notes = "实践企业统计表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> enterpriseCountPage(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b><br>enterpriseName:企业名称、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
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
		PageData<EnterpriseCountDomain> pageData = enterpriseCountService.enterpriseCountStatistics(domain, dto.getOrgId());
		
		PageDomain p = new PageDomain();
		p.setPageNumber(pageData.getPage().getPageNumber());
		p.setPageSize(pageData.getPage().getPageSize());
		p.setTotalElements(pageData.getPage().getTotalElements());
		p.setTotalPages(pageData.getPage().getTotalPages());
		result.put(ApiReturnConstants.PAGE, p);
		result.put(ApiReturnConstants.DATA, pageData.getData());

		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/activedetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生激活明细表", response = Void.class, notes = "学生激活明细表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> practiceActive(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b><br>keyWords:学生姓名或学号、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
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
		result = peopleCountDetailService.practiceDetailStatistics(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/joindetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生参与明细表", response = Void.class, notes = "学生参与明细表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> practiceJoin(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b><br>keyWords:学生姓名或学号、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
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
		result = peopleCountDetailService.practiceJoinStatistics(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/totalcount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "实践教学汇总统计", response = Void.class, notes = "实践教学汇总统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> practiceTotalCount(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b><br>keyWords:班级名称、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
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
		result = peopleCountService.practiceTotalStatistics(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/report", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "学生实习报告成绩统计", response = Void.class, notes = "学生实习报告成绩统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> report(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b>groupId：实践计划id<br>keyWords:班级名称、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStuId(dto.getId());
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			domain.setCounselorId(dto.getId());
		}
		
		result = reportService.reportStatistics(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/sign", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "签到统计汇总表", response = Void.class, notes = "签到统计汇总表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> sign(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:、</b>groupId:实践计划id<br>keyWords:班级名称、<br>classId:班级id、<br>professionalId:专业id、<br>collegeId:院系id、<br>") @Valid @RequestBody QueryStuPageDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		UserInfoDomain dto = publicMobileService.getUserInfo(token);
		if (null == dto || null == dto.getId()) {
			result.put(ApiReturnConstants.RESULT, Boolean.FALSE);
			result.put(ApiReturnConstants.CODE,
					PublicErrorCode.AUTH_EXCEPTION.getIntValue());
			result.put(ApiReturnConstants.CAUSE, "未授权");
			return new ResponseEntity<Map<String, Object>>(result,
					HttpStatus.UNAUTHORIZED);
		}
		
		Set<String> roles = dto.getRoleNames();
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			domain.setStuId(dto.getId());
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			domain.setCounselorId(dto.getId());
		}
		result = signStatisticalService.signDetailStatistics(domain, dto.getOrgId());
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/signdetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "签到统计明细表", response = Void.class, notes = "签到统计明细表<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> signDetail(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b>stuId:学生id<br>groupId:实践参与计划id") @Valid @RequestBody QueryStuPageDomain domain) {
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
		result = signStatisticalService.signDetailPage(domain);
		
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/counselorcount", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "老师指导过程明细表", response = Void.class, notes = "老师指导过程明细表<br><br><b>@author 郑宁</b>")
	public PageData<CounselorCountDomain> counselorCount(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b><br>keyWords:学号/姓名/参与计划名称(模糊匹配)<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>") @RequestBody QueryScoreDomain domain
			) {
		try{
			AccountDTO dto = authUtilService.getSsoUserInfo(token);
			if (null == dto || null == dto.getId()) {
				throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
						"未授权");
			}
			
			return  counselorCountService.queryInforPage(domain,dto.getOrgId());
		}catch(Exception ex){
			ex.printStackTrace();
			throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(),
					"查询出错");
		}
		
	}
	
	
	@RequestMapping(value = "/cstatistics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "辅导员APP统计", response = Void.class, notes = "辅导员APP统计<br><br><b>@author 郑宁</b>")
	public PageData<StatisticalDomain> counselortatistics(
			@RequestHeader("Authorization") String token,
			 @ApiParam(value = "<b>选填:</b><br>stuId:学生id<br>groupId:当前所在实践计划id<br>keyWords:学号/姓名(模糊匹配)<br>sortFlag:排序标识(asc:创建时间升序,desc:创建时间倒序)<br>sortField:排序字段(PASS_NUM:任务完成数,SIGNIN_NORMAL_NUM:正常打卡数,SUMMARY_TOTAL_NUM:已提交周日志数)<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>") @RequestBody QueryStatisticalPageDomain domain
			) {
		try{
			AccountDTO dto = authUtilService.getSsoUserInfo(token);
			if (null == dto || null == dto.getId()) {
				throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
						"未授权");
			}
			
			PageData<StatisticalDomain> pageData = statisticalService.findStatisticsPage(domain);
			if (null != domain.getGroupId()
					&& domain.getGroupId().longValue() > 0L) {
				List<StatisticalDomain> statisticalList = pageData.getData();
				String userIds = "";
				for (StatisticalDomain scoreDomain : statisticalList) {
					if (StringUtils.isEmpty(userIds)) {
						userIds += scoreDomain.getStudentId().toString();
					} else {
						userIds += "," + scoreDomain.getStudentId().toString();
					}
				}
				HashMap<Long, AccountDTO> avatarMap = authUtilService
						.getavatarUsersInfo(userIds);
				for (StatisticalDomain scoreDomain : statisticalList) {
					if(null != avatarMap.get(scoreDomain.getStudentId())){
						scoreDomain.setStudentAvatar(avatarMap.get(scoreDomain.getStudentId()).getAvatar());
					}
				}
			}
			return  pageData;
		}catch(Exception ex){
			ex.printStackTrace();
			throw new CommonException(PublicErrorCode.QUERY_EXCEPTION.getIntValue(),
					"查询出错");
		}
		
	}
	
	
	@RequestMapping(value = "/evaluate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "实践评价统计", response = Void.class, notes = "实践评价统计<br><br><b>@author 郑宁</b>")
	public PageData page(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>groupId:实践参与计划id<br>flag:查询标识[ieval:我评价的,evalme:评价我的;默认为ieval]<b>选填:、</b>stuName:学生姓名/学号<br>") @RequestBody QueryEvaluateDomain domain) {
		UserInfoDomain dto = publicMobileService.getUserInfo(token);

		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		Set<String> roles = dto.getRoleNames();
		String roleName = "";
		if (roles.contains(RoleCode.ROLE_STUDENT)) {
			roleName = RoleCode.ROLE_STUDENT;
		}else if(roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)){//辅导员
			roleName = RoleCode.ROLE_CLASSROOMTEACHER;
		}
		
		return evaluateService.queryInforPage(domain, roleName,dto.getId());
	}
	
}
