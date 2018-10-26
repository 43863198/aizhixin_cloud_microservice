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
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.core.RoleCode;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.QueryCommentTotalDomain;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.AuthUtilService;
import com.aizhixin.cloud.studentpractice.common.service.RedisDataService;
import com.aizhixin.cloud.studentpractice.summary.core.SummaryCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QuerySummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryNumCountDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Summary;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryService;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.PeopleCountDetail;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/summary")
@Api(description = "日报周报月报API")
public class SummaryController {

	@Autowired
	private SummaryService summaryService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PeopleCountDetailService peopleCountDetailService;
	@Autowired
	private RedisDataService redisDataService;

	/**
	 * 创建日报周报月报
	 * 
	 * @param domain
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "创建日报周报月报", response = Void.class, notes = "创建日报周报月报<br><br><b>@author 郑宁</b>")
	public Summary add(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:、</b><br>summaryType:类型(日报[daily]，周报[weekly]，月报[monthly]),description:报告描述<br>publishStatus:发布状态:公开[open],非公开]private(默认为非公开)") @Valid @RequestBody SummaryDomain domain,
			BindingResult bindingResult) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		checkData(domain, bindingResult);
		if (StringUtils.isEmpty(domain.getPublishStatus())) {
			domain.setPublishStatus(SummaryCode.PUBLISH_STATUS_PRIVATE);
		}
		Summary summary = summaryService.save(domain, dto);
		redisDataService.delSummaryInfor(dto.getId(),summary.getGroupId());
		return summary;
	}

	private void checkData(SummaryDomain domain, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ObjectError e = bindingResult.getAllErrors().get(0);
			throw new CommonException(
					PublicErrorCode.SAVE_EXCEPTION.getIntValue(), e.toString());
		}
		if (StringUtils.isEmpty(domain.getSummaryType())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"报告类型[日报，周报，月报]不能为空");
		}
		if (StringUtils.isEmpty(domain.getDescription())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "描述不能为空");
		}
		// if(StringUtils.isEmpty(domain.getPublishStatus())){
		// throw new CommonException(ErrorCode.ID_IS_REQUIRED,
		// "发布状态不能为空");
		// }
	}

	// @RequestMapping(value = "/edit", method = RequestMethod.PUT, produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// @ApiOperation(httpMethod = "PUT", value = "修改日报周报月报", response =
	// Void.class, notes = "修改日报周报月报<br><br><b>@author 郑宁</b>")
	// public Summary edit(
	// @RequestHeader("Authorization") String token,
	// @ApiParam(value =
	// "<b>必填:、</b><br>id,<br>summaryType:类型(日报[daily]，周报[weekly]，月报[monthly]),description:报告描述<br>publishStatus:发布状态:公开[open],非公开]private")
	// @Valid @RequestBody SummaryDomain domain,
	// BindingResult bindingResult) {
	// AccountDTO dto = authUtilService.getSsoUserInfo(token);
	// if (null == dto || null == dto.getId()) {
	// throw new CommonException(PublicErrorCode.AUTH_EXCEPTION.getIntValue(),
	// "未授权");
	// }
	// checkData(domain, bindingResult);
	// return summaryService.save(domain, dto);
	// }

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "DELETE", value = "删除实践周任务", response = Void.class, notes = "删除实践周任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> delete(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "id 报告id", required = true) @RequestParam(value = "id", required = true) String id) {
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

		if (StringUtils.isEmpty(id)) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "删除的周任务的id不能为空");
		}

		Summary summary = summaryService.delete(id);
		redisDataService.delSummaryInfor(dto.getId(),summary.getGroupId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询报告信息", response = Void.class, notes = "分页查询报告信息<br><br><b>@author 郑宁</b>")
	public Map<String, Object> page(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:</b><br>groupId:实践计划id<br>creatorName:学生姓名<br>summaryTitle:报告标题<br>summaryType:类型(日报[daily]，周报[weekly]，月报[monthly])<br>publishStatus:发布状态:公开[open],非公开]private(不传为查询所有状态)<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>dayNum:筛选类型:当天(1)一周(7)三个月(90)") @RequestBody QuerySummaryDomain domain) {
		try {
			AccountDTO dto = authUtilService.getSsoUserInfo(token);
			if (null == dto || null == dto.getId()) {
				throw new CommonException(
						PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
			}

			Map<String, Object> result = null;
			boolean flag = false;
		
			List<String> roles = dto.getRoleNames();
			if (roles.contains(RoleCode.ROLE_STUDENT)) {
				if(null == domain.getGroupId()){
					StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
					if(null != stuDTO){
						domain.setGroupId(stuDTO.getTrainingGroupId());
					}else{
						return new HashMap<String, Object>();
					}
				}
			}
			if(null == domain.getDayNum() && StringUtils.isEmpty(domain.getSummaryTitle()) && StringUtils.isEmpty(domain.getSummaryType()) && StringUtils.isEmpty(domain.getCreatorName()) && 1== domain.getPageNumber()){
				flag = true;
			}
			if(flag){
				result = redisDataService.getSummaryInfor(dto
						.getId(),domain.getGroupId());
			}
			if (null == result) {
				if (roles.contains(RoleCode.ROLE_STUDENT)) {
					domain.setUserId(dto.getId());
					result = summaryService.findPage(domain, token);
				} else if (roles.contains(RoleCode.ROLE_COM_TEACHER)) {
					domain.setMentorId(dto.getId());
					result = summaryService.findPage(domain, token);
				} else {// 辅导员
					domain.setCounselorId(dto.getId());
					result = summaryService.findPage(domain, token);
				}
				if (null != result && !result.isEmpty()) {
					if(flag){
						redisDataService.cacheSummaryInfor(dto
						.getId(),domain.getGroupId(), result);
					}
				}
			}else{
				if(null != result.get(ApiReturnConstants.DATA)){
					
					List<SummaryDomain> dataList = (List<SummaryDomain>)result.get(ApiReturnConstants.DATA);
					HashSet<String> idList = new HashSet<String>();
					for (SummaryDomain summaryDto : dataList) {
						idList.add(summaryDto.getId());
					}
					
					QueryCommentTotalDomain queryCountDTO = new QueryCommentTotalDomain();
					queryCountDTO.setSourceIds(idList);
					queryCountDTO.setModule(PushMessageConstants.MODULE_TASK);

					HashMap<String, Integer> commentTotalMap = authUtilService
							.getCommentTotalCount(queryCountDTO, token);
					if (null != commentTotalMap && !commentTotalMap.isEmpty()) {
						for (SummaryDomain summaryDto : dataList) {
							if (null != commentTotalMap.get(summaryDto.getId())) {
								summaryDto.setCommentNum(commentTotalMap.get(summaryDto.getId()));
							}
						}
						result.put(ApiReturnConstants.DATA, dataList);
					}
				}
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CommonException(
					PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "查询出错");
		}

	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "报告详情", response = Void.class, notes = "报告详情<br><br><b>@author 郑宁</b>")
	public SummaryDomain detail(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "id 报告id", required = true) @RequestParam(value = "id", required = true) String id) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		if (StringUtils.isEmpty(id)) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "删除的周任务的id不能为空");
		}

		return summaryService.findDetail(id);
	}

	@RequestMapping(value = "/stucount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "学生提交报告数量和实践考勤统计", response = Void.class, notes = "学生提交报告数量统计<br><br><b>@author 郑宁</b>")
	public SummaryNumCountDomain stuCount(
			@RequestHeader("Authorization") String token) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		SummaryNumCountDomain domain = new SummaryNumCountDomain();
		StuInforDomain stuDTO = authUtilService.getMentorInfo(dto.getId());
		PeopleCountDetail peopleCountDetail = peopleCountDetailService
				.findByStuIdAndGroupId(dto.getId(), stuDTO.getTrainingGroupId());
		if (null != peopleCountDetail) {

			domain.setGroupId(peopleCountDetail.getGroupId());
			domain.setGroupName(peopleCountDetail.getGroupName());

			if (null != peopleCountDetail.getDailyNum()
					&& peopleCountDetail.getDailyNum().intValue() > 0) {
				domain.setDailyNum(peopleCountDetail.getDailyNum());
			}
			if (null != peopleCountDetail.getWeeklyNum()
					&& peopleCountDetail.getWeeklyNum().intValue() > 0) {
				domain.setWeeklyNum(peopleCountDetail.getWeeklyNum());
			}
			if (null != peopleCountDetail.getMonthlyNum()
					&& peopleCountDetail.getMonthlyNum().intValue() > 0) {
				domain.setMonthlyNum(peopleCountDetail.getMonthlyNum());
			}

			if (null != peopleCountDetail.getSignInNormalNum()
					&& peopleCountDetail.getSignInNormalNum().intValue() > 0) {
				domain.setSignInNormalNum(peopleCountDetail
						.getSignInNormalNum());
			}
			if (null != peopleCountDetail.getSignInTotalNum()
					&& peopleCountDetail.getSignInTotalNum().intValue() > 0) {
				domain.setSignInTotalNum(peopleCountDetail.getSignInTotalNum());
			}
			if (null != peopleCountDetail.getLeaveNum()
					&& peopleCountDetail.getLeaveNum().intValue() > 0) {
				domain.setLeaveNum(peopleCountDetail.getLeaveNum());
			}
		}
		return domain;
	}
	
	
//	@RequestMapping(value = "/review", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "PUT", value = "日报周报月报批阅/打分", response = Void.class, notes = "日报周报月报批阅/打分<br><br><b>@author 郑宁</b>")
//	public Summary review(
//			@RequestHeader("Authorization") String token,
//			@ApiParam(value = "<b>必填:、</b><br>id:周日志id<br><b>选填:、</b>summaryScore:日志分数") @Valid @RequestBody SummaryDomain domain,
//			BindingResult bindingResult) {
//		AccountDTO dto = authUtilService.getSsoUserInfo(token);
//		if (null == dto || null == dto.getId()) {
//			throw new CommonException(
//					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
//		}
//		if (StringUtils.isEmpty(domain.getId())) {
//			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "周日志id不能为空");
//		}
//		Summary summary = summaryService.review(domain);
//		redisDataService.delSummaryInfor(dto.getId(),summary.getGroupId());
//		return summary;
//	}
}
