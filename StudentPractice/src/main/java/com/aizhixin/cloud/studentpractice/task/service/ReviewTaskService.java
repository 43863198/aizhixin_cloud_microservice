package com.aizhixin.cloud.studentpractice.task.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.core.ErrorCode;
import com.aizhixin.cloud.studentpractice.common.core.PushMessageConstants;
import com.aizhixin.cloud.studentpractice.common.domain.AccountDTO;
import com.aizhixin.cloud.studentpractice.common.domain.IdNameDomain;
import com.aizhixin.cloud.studentpractice.common.domain.PushMessageDTO;
import com.aizhixin.cloud.studentpractice.common.exception.CommonException;
import com.aizhixin.cloud.studentpractice.common.service.PushMessageService;
import com.aizhixin.cloud.studentpractice.common.service.PushService;
import com.aizhixin.cloud.studentpractice.task.core.MessageCode;
import com.aizhixin.cloud.studentpractice.task.core.TaskStatusCode;
import com.aizhixin.cloud.studentpractice.task.domain.FileDomain;
import com.aizhixin.cloud.studentpractice.task.domain.ReviewTaskDomain;
import com.aizhixin.cloud.studentpractice.task.domain.TaskDomain;
import com.aizhixin.cloud.studentpractice.task.entity.File;
import com.aizhixin.cloud.studentpractice.task.entity.MentorTask;
import com.aizhixin.cloud.studentpractice.task.entity.ReviewTask;
import com.aizhixin.cloud.studentpractice.task.entity.StudentTask;
import com.aizhixin.cloud.studentpractice.task.entity.WeekTask;
import com.aizhixin.cloud.studentpractice.task.repository.ReviewTaskRepository;

@Transactional
@Service
public class ReviewTaskService {

	@Autowired
	private ReviewTaskRepository reviewTaskRepository;
	@Autowired
	private StudentTaskService stuTaskService;
	@Autowired
	private FileService fileService;
	@Autowired
	private MentorTaskService mentorTaskService;
	@Autowired
	private PushService pushService;
	@Autowired
	@Lazy
	private WeekTaskService weekTaskService;

	public ReviewTask save(ReviewTask reTask) {
		return reviewTaskRepository.save(reTask);
	}

	public ReviewTask findByStuTaskIdAndScoreNotNull(String stuTaskId) {
		return reviewTaskRepository
				.findOneByDeleteFlagAndReviewScoreIsNotNullAndStudentTaskId(
						DataValidity.VALID.getIntValue(), stuTaskId);
	}
	
	public List<ReviewTask> findByStuTaskId(String stuTaskId) {
		return reviewTaskRepository
				.findAllByDeleteFlagAndStudentTaskIdOrderByCreatedDateDesc(
						DataValidity.VALID.getIntValue(), stuTaskId);
	}

	public ReviewTask review(ReviewTaskDomain domain, Long userId, String token) {

		StudentTask studentTask = stuTaskService.findById(domain
				.getStudentTaskId());
		MentorTask menTask = null;
		if (null == studentTask) {
			throw new CommonException(ErrorCode.ID_IS_REQUIRED, "未查询到相关学生任务信息");
		}

		if (TaskStatusCode.TASK_STATUS_BACK_TO.equals(domain.getReviewResult())) {
			int backToNum = studentTask.getBackToNum();
			if (2 == backToNum) {
				throw new CommonException(ErrorCode.PARAMS_CONFLICT,
						"学生任务评审打回不能超过2次");
			} else {
				studentTask.setBackToNum(++backToNum);
			}
			domain.setReviewScore(null);
		} else {// 更新导师任务进度
			Long scoreNum = stuTaskService
					.countScoreNotNullByMentorTaskId(studentTask
							.getMentorTaskId());
			Long totalNum = stuTaskService.countAllByMentorTaskId(studentTask
					.getMentorTaskId());

			// 计算任务进度
			double sNum = scoreNum;
			double tNum = totalNum;
			double rNum = (++sNum) / tNum;
			BigDecimal bd = new BigDecimal(rNum);
			double progess = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();

			menTask = mentorTaskService.findById(studentTask.getMentorTaskId());
			menTask.setScoreNum(scoreNum.intValue()+1);
			menTask.setProgress(String.valueOf(progess));
			mentorTaskService.save(menTask);

			studentTask.setReviewScore(domain.getReviewScore());
		}
		studentTask.setStudentTaskStatus(domain.getReviewResult());
		stuTaskService.save(studentTask);

		ReviewTask reTask = new ReviewTask();
		BeanUtils.copyProperties(domain, reTask);
		String reTaskId = UUID.randomUUID().toString();
		reTask.setId(reTaskId);
		reTask.setCreatedBy(userId);
		reTask = save(reTask);

		// 保存评审任务上传附件
		if (null != domain.getFileList() && domain.getFileList().size() > 0) {
			List<File> fileList = new ArrayList<File>();
			for (FileDomain fileDomain : domain.getFileList()) {
				File file = new File();
				String fileId = UUID.randomUUID().toString();
				file.setId(fileId);
				file.setFileName(fileDomain.getFileName());
				file.setSrcUrl(fileDomain.getSrcUrl());
				file.setSourceId(reTaskId);
				file.setCreatedBy(userId);
				fileList.add(file);
			}
			fileService.saveList(fileList);
		}

		pushCheckMessage(domain, studentTask.getStudentId(), studentTask, menTask);

		return reTask;
	}

	private void pushCheckMessage(ReviewTaskDomain domain, Long userId,
			 StudentTask studentTask, MentorTask menTask) {
		
		List<Long> ids = new ArrayList<Long>();
		ids.add(userId);
		if (null == menTask) {
			menTask = mentorTaskService.findById(studentTask.getMentorTaskId());
		}
		if (TaskStatusCode.TASK_STATUS_BACK_TO.equals(domain.getReviewResult())) {
		
			   PushMessageDTO msg = new PushMessageDTO();
			   
				msg.setTaskName(menTask.getTaskName());
	         	if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
	         		msg.setWeekTaskId(menTask.getWeekTaskId());
	         		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
	         		msg.setWeekTaskName(weekTask.getTaskTitle());
	         	}
	         	msg.setMentorTaskId(menTask.getId());
	         	msg.setStuTaskId(studentTask.getId());
	         	msg.setStuTaskStatus(TaskStatusCode.TASK_STATUS_BACK_TO);
			   
	         	msg.setBusinessContent(menTask.getId());
				msg.setContent(MessageCode.MESSAGE_HEAD
						.concat(MessageCode.MESSAGE_CHECK_TASK_BACK_TO
								.replace("{1}",
										menTask.getTaskName())));
				msg.setFunction(PushMessageConstants.MODULE_TASK);
				msg.setModule(PushMessageConstants.MODULE_TASK);
				msg.setTitle(MessageCode.MESSAGE_TITLE_CHECK_TASK);
				msg.setUserIds(ids);
				pushService.addPushList(msg);
			
		} else if (TaskStatusCode.TASK_STATUS_FINISH.equals(domain
				.getReviewResult())) {
			
			 PushMessageDTO msg = new PushMessageDTO();
			 
			 msg.setTaskName(menTask.getTaskName());
			 if(!StringUtils.isEmpty(menTask.getWeekTaskId())){
	         		msg.setWeekTaskId(menTask.getWeekTaskId());
	         		WeekTask weekTask = weekTaskService.findById(menTask.getWeekTaskId());
	         		msg.setWeekTaskName(weekTask.getTaskTitle());
	         	}
	         	msg.setMentorTaskId(menTask.getId());
	         	msg.setStuTaskId(studentTask.getId());
	         	msg.setBusinessContent(studentTask.getReviewScore());
	         	msg.setStuTaskStatus(TaskStatusCode.TASK_STATUS_FINISH);
	         	
	         	msg.setBusinessContent(menTask.getId());
				msg.setContent(MessageCode.MESSAGE_HEAD
						.concat(MessageCode.MESSAGE_CHECK_TASK_SCORE
								.replace("{1}",
										menTask.getTaskName())));
				msg.setFunction(PushMessageConstants.MODULE_TASK);
				msg.setModule(PushMessageConstants.MODULE_TASK);
				msg.setTitle(MessageCode.MESSAGE_TITLE_CHECK_TASK);
				msg.setUserIds(ids);
				pushService.addPushList(msg);
			
		} 
	}
}
