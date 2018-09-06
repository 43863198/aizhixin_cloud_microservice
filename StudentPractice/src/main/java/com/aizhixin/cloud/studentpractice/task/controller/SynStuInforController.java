/**
 * 
 */
package com.aizhixin.cloud.studentpractice.task.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import com.aizhixin.cloud.studentpractice.common.service.RedisDataService;
import com.aizhixin.cloud.studentpractice.score.service.CounselorCountService;
import com.aizhixin.cloud.studentpractice.summary.service.EnterpriseCountService;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryReplyCountService;
import com.aizhixin.cloud.studentpractice.summary.service.SummaryService;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.MentorTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryStuPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.QueryTaskPageDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuInforDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.StuTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDetailDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.repository.StudentTaskRepository;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskCountService;
import com.aizhixin.cloud.studentpractice.task.service.MentorTaskService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountDetailService;
import com.aizhixin.cloud.studentpractice.task.service.PeopleCountService;
import com.aizhixin.cloud.studentpractice.task.service.PracticeTaskService;
import com.aizhixin.cloud.studentpractice.task.service.ReviewTaskService;
import com.aizhixin.cloud.studentpractice.task.service.SignStatisticalService;
import com.aizhixin.cloud.studentpractice.task.service.StudentTaskService;
import com.aizhixin.cloud.studentpractice.task.service.TaskStatisticalService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * 同步学生信息数据
 * 
 * @author 郑宁
 *
 */
@RestController
@RequestMapping("/v1/syn")
@Api(description = "同步学生信息API")
public class SynStuInforController {

	@Autowired
	private StudentTaskRepository stuTaskRepository;
	@Autowired
	private AuthUtilService authUtilService;
    @Autowired
	private MentorTaskCountService mentorTaskCountService;
    @Autowired
	private MentorTaskService mentorTaskService;
    @Autowired
	private TaskStatisticalService taskStatisticalService;
    @Autowired
   	private SummaryService summaryService;
    @Autowired
   	private SummaryReplyCountService summaryReplyCountService;
    @Autowired
	private EnterpriseCountService enterpriseCountService;
    @Autowired
   	private  PeopleCountDetailService peopleCountDetailService;
    @Autowired
   	private  PeopleCountService peopleCountService;
    @Autowired
   	private SignStatisticalService signStatisticalService;
    @Autowired
    private CounselorCountService counselorCountService;
    @Autowired
    private PracticeTaskService practiceTaskService;
    @Autowired
    private  RedisDataService redisDataService;
    
	/**
	 * 查询学生是否分配有企业导师
	 * @param token
	 * @return
	 */
//	@RequestMapping(value = "/stuinfor", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiOperation(httpMethod = "GET", value = "同步实践学生信息(补全历史学生数据的学号，所在院系id，所在专业id，所在行政班id)", response = Void.class, notes = "同步实践学生信息<br><br><b>@author 郑宁</b>")
//	public ResponseEntity<Map<String, Object>> synStuInfor() {
//		Map<String, Object> result = new HashMap<String, Object>();
//		
//		List<StudentTask> stuTaskList = stuTaskRepository.findDistinctMentorIdByDeleteFlag(DataValidity.VALID.getIntValue());
//		for(StudentTask stuTask :stuTaskList){
//			PageData data =	authUtilService.getStudentInfo(stuTask.getMentorId(), null, 1, Integer.MAX_VALUE);
//				if(null != data){
//				List<StuInforDomain> stuList = data.getData();
//				for(StuInforDomain stu:stuList){
//					stuTaskRepository.synStuInfor(stu.getJobNum(), stu.getClassId(), stu.getProfessionalId(), stu.getCollegeId(), stu.getMentorCompanyName(),stu.getOrgId(), stu.getId());
//				}
//			}
//		}
//			
//		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/tasknum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步企业导师任务完全情况", response = Void.class, notes = "手动同步企业导师任务完全情况<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> updateMentorTaskNum() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		 mentorTaskCountService.saveCountTask();	
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/counttask", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步导师任务完成情况统计", response = Void.class, notes = "手动同步导师任务完成情况统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> saveCountTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
          mentorTaskCountService.saveCountTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/taskstatistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步统计实践任务", response = Void.class, notes = "手动同步统计实践任务<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> taskStatistics() {
		Map<String, Object> result = new HashMap<String, Object>();
		
          taskStatisticalService.taskStatistics(null);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	} 
	
	@RequestMapping(value = "/countpeoplenum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步实践学校学生信息", response = Void.class, notes = "手动同步实践学校学生信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countPeopleNum() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		taskStatisticalService.countPeopleNum(null);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/countsummarynum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步统计学生日报周报月报信息", response = Void.class, notes = "手动同步统计学生日报周报月报信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countSummaryNumTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
        summaryService.countSummaryNumTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/countsummaryreply", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步统计学生日志回复统计信息", response = Void.class, notes = "手动同步统计学生日志回复统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> summaryReplyNumCountTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		summaryReplyCountService.summaryReplyNumCountTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/countenterprise", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步统计企业实践学生数量", response = Void.class, notes = "手动同步统计企业实践学生数量<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countEnterpriseTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		enterpriseCountService.enterpriseCountTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/practicejoin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步更新学生是否提交实践任务或日志信息", response = Void.class, notes = "手动同步更新学生是否提交实践任务或日志信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> practiceJoin() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		peopleCountDetailService.updateJoinPracticeTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/countjoin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步更新学生参与实践人数统计信息", response = Void.class, notes = "手动同步更新学生参与实践人数统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countJoin() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		peopleCountService.updateJoinStuNumTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/countsummarybyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步班级学生提交日志周志统计信息", response = Void.class, notes = "手动同步班级学生提交日志周志统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countSummaryByClassTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		peopleCountService.updateSummaryNumTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/countreportbystu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步学生提交实践报告统计信息", response = Void.class, notes = "手动同步学生提交实践报告统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> ountReportByStu() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		peopleCountDetailService.updateStuReportStatusTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/countreportbyclass", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步更新班级学生提交实践报告统计信息", response = Void.class, notes = "手动同步更新班级学生提交实践报告统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> countReportByClass() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		peopleCountService.updateReportNumTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/countsign", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步更新实践考勤统计信息", response = Void.class, notes = "手动同步更新实践考勤统计信息<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> synSignInforTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		signStatisticalService.synSignInforTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/initgroupid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动同步更新学生任务所在实践参与计划id", response = Void.class, notes = "手动同步更新学生任务所在实践参与计划id<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> initGroupId() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		mentorTaskService.initStuTaskGroupId();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/clearcount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动清除统计表数据(任务,日志,报告统计表),会影响报表统计请谨慎使用", response = Void.class, notes = "手动清除统计表数据(任务,日志,报告统计表)<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> clearCount() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		taskStatisticalService.clearCount();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/counselorcount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动更新辅导员指导过程统计", response = Void.class, notes = "手动更新辅导员指导过程统计<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> counselorCountTask() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		counselorCountService.counselorCountTask();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/opencallgroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动开启进行中实践计划的导员点名", response = Void.class, notes = "手动开启进行中实践计划的导员点名<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> openCallGroup() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		counselorCountService.openCounselorCallTask();;
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/closecallgroup", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动关闭已结束实践计划的导员点名", response = Void.class, notes = "手动关闭已结束实践计划的导员点名<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> closeCallGroup() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		counselorCountService.closeCounselorCallTask();;
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/syngroupname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "POST", value = "同步修改实践计划名称", response = Void.class, notes = "同步修改实践计划名称<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> synGroupName(@ApiParam(value = "<b>必填:</b><br>id:实践计划id<br>name:实践计划名称") @RequestBody IdNameDomain domain) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		practiceTaskService.synGroupName(domain);
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/clearredis", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "手动清除redis缓存", response = Void.class, notes = "手动清除redis缓存<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> clearRedis() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		redisDataService.clearAllCache();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/initjobnum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(httpMethod = "GET", value = "初始化日志周志学生学号", response = Void.class, notes = "初始化日志周志学生学号<br><br><b>@author 郑宁</b>")
	public ResponseEntity<Map<String, Object>> initJobNum() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		summaryService.initJobNum();
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}
}
