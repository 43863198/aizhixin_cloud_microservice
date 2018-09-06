/**
 * 
 */
package com.aizhixin.cloud.studentpractice.score.controller;

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
import com.aizhixin.cloud.studentpractice.score.domain.QueryScoreDomain;
import com.aizhixin.cloud.studentpractice.score.domain.ScoreDetailDomain;
import com.aizhixin.cloud.studentpractice.score.domain.ScoreDomain;
import com.aizhixin.cloud.studentpractice.score.entity.Score;
import com.aizhixin.cloud.studentpractice.score.service.ScoreService;
import com.aizhixin.cloud.studentpractice.summary.core.ReportStatusCode;
import com.aizhixin.cloud.studentpractice.summary.domain.QueryReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.ReportDomain;
import com.aizhixin.cloud.studentpractice.summary.domain.SummaryDomain;
import com.aizhixin.cloud.studentpractice.summary.entity.Report;
import com.aizhixin.cloud.studentpractice.summary.service.ReportService;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.WeekTaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/score")
@Api(description = "实践成绩API")
public class ScoreController {

	@Autowired
	private ScoreService scoreService;
	@Autowired
	private AuthUtilService authUtilService;
	@Autowired
	private PublicMobileService publicMobileService;

	@RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "分页查询实践成绩信息", response = Void.class, notes = "分页查询实践成绩信息<br><br><b>@author 郑宁</b>")
	public PageData<ScoreDomain> page(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>选填:</b><br>groupId:当前所在实践计划id<br>keyWords:学号/姓名/参与计划名称(模糊匹配)<br>pageNumber:第几页<br>pageSize:每页数据的数目<br>sortFlag:排序标识(asc:创建时间升序,desc:创建时间倒序)<br>sortField:排序字段(SIGN_SCORE:签到成绩,SUMMARY_SCORE:日志成绩,REPORT_SCORE:实践报告成绩,TASK_SCORE:任务成绩,TOTAL_SCORE:总成绩)") @RequestBody QueryScoreDomain domain) {
		try {
			UserInfoDomain dto = publicMobileService.getUserInfo(token);
			if (null == dto || null == dto.getId()) {
				throw new CommonException(
						PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
			}

			Set<String> roles = dto.getRoleNames();
			if (roles.contains(RoleCode.ROLE_STUDENT)) {
				domain.setStuId(dto.getId());
				if (null == domain.getGroupId()) {
					StuInforDomain stuDTO = authUtilService.getMentorInfo(dto
							.getId());
					if (null != stuDTO) {
						domain.setGroupId(stuDTO.getTrainingGroupId());
					} else {
						return new PageData<ScoreDomain>();
					}
				}
			} else if (roles.contains(RoleCode.ROLE_CLASSROOMTEACHER)) {// 辅导员
				domain.setCounselorId(dto.getId());
			}

			PageData<ScoreDomain> pageData = scoreService.queryInforPage(
					domain, dto.getOrgId());
			if (null != domain.getGroupId()
					&& domain.getGroupId().longValue() > 0L) {
				List<ScoreDomain> scoreList = pageData.getData();
				String userIds = "";
				for (ScoreDomain scoreDomain : scoreList) {
					if (StringUtils.isEmpty(userIds)) {
						userIds += scoreDomain.getStudentId().toString();
					} else {
						userIds += "," + scoreDomain.getStudentId().toString();
					}
				}
				HashMap<Long, AccountDTO> avatarMap = authUtilService
						.getavatarUsersInfo(userIds);
				for (ScoreDomain scoreDomain : scoreList) {
					if (null != avatarMap.get(scoreDomain.getStudentId())) {
						scoreDomain.setStudentAvatar(avatarMap.get(
								scoreDomain.getStudentId()).getAvatar());
					}
				}
			}
			return pageData;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CommonException(
					PublicErrorCode.QUERY_EXCEPTION.getIntValue(), "查询出错");
		}

	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "成绩详情", response = Void.class, notes = "成绩详情<br><br><b>@author 郑宁</b>")
	public ScoreDetailDomain detail(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "studentId 学生id", required = true) @RequestParam(value = "studentId", required = true) Long studentId,
			@ApiParam(value = "groupId 参与计划id", required = true) @RequestParam(value = "groupId", required = true) Long groupId) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		if (null != studentId && studentId > 0) {
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"studentId不能为空或为0");
		}
		if (null != groupId && groupId > 0) {
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"groupId不能为空或为0");
		}

		// return scoreService.findByStuIdAndGroupId(studentId,groupId);
		return scoreService.findStatistics(studentId, groupId);
	}

	@RequestMapping(value = "/changescore", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "PUT", value = "修改学生成绩", response = Void.class, notes = "修改学生成绩<br><br><b>@author 郑宁</b>")
	public Score changeScore(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "<b>必填:</b><br>id:成绩id<br>totalScore:总成绩<br>") @RequestBody ScoreDomain domain) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		if (StringUtils.isEmpty(domain.getId())) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "成绩Id不能为空");
		}
		if (null != domain.getTotalScore() && domain.getTotalScore() > 0d) {
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "总成绩不能为空或为0");
		}

		return scoreService.changeScore(domain, dto);
	}

	@RequestMapping(value = "/auto", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "自动生成指定实践小组成绩", response = Void.class, notes = "自动生成成绩<br><br><b>@author 郑宁</b>")
	public void auto(
			@RequestHeader("Authorization") String token,
			@ApiParam(value = "groupId 参与计划id", required = true) @RequestParam(value = "groupId", required = true) Long groupId) {
		AccountDTO dto = authUtilService.getSsoUserInfo(token);
		if (null == dto || null == dto.getId()) {
			throw new CommonException(
					PublicErrorCode.AUTH_EXCEPTION.getIntValue(), "未授权");
		}
		if (null != groupId && groupId > 0) {
		} else {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED,
					"groupId不能为空或为0");
		}
		scoreService.autoScoreTask(groupId);
	}

}
