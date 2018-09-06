
package com.aizhixin.cloud.studentpractice.task.entity;

import io.swagger.annotations.ApiModelProperty;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * 学生任务表
 * @author zhengning
 *
 */
@Entity(name = "SP_STUDENT_TASK")
@ToString
public class StudentTask extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
	
	@ApiModelProperty(value = "导师任务id", required = false)
	@NotNull
	@Column(name = "MENTOR_TASK_ID")
	@Getter @Setter private String mentorTaskId;
	
	@ApiModelProperty(value = "导师id", required = false)
	@NotNull
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "学生id", required = false)
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "学生名称", required = false)
	@Column(name = "STUDENT_NAME")
	@Getter @Setter private String studentName;
	
	@ApiModelProperty(value = "任务状态(uncommit:待提交,checkPending:待审核,backTo:已打回,finish:已完成)", required = false)
	@NotNull
	@Column(name = "STUDENT_TASK_STATUS")
	@Getter @Setter private String studentTaskStatus;
	
	@ApiModelProperty(value = "任务结果描述", required = false)
	@Column(name = "RESULT_DESCRIPTION")
	@Getter @Setter private String resultDescription;
	
	@ApiModelProperty(value = "评审分数", required = false)
	@Column(name = "REVIEW_SCORE")
	@Getter @Setter private String reviewScore;
	
	@ApiModelProperty(value = "评审打回次数", required = false)
	@Column(name = "BACK_TO_NUM")
	@Getter @Setter private int backToNum;
	
	@ApiModelProperty(value = "班级id", required = false)
	@Column(name = "CLASS_ID")
	@Getter @Setter private Long classId;
	
	@ApiModelProperty(value = "院系id", required = false)
	@Column(name = "COLLEGE_ID")
	@Getter @Setter private Long collegeId;
	
	@ApiModelProperty(value = "专业id", required = false)
	@Column(name = "PROFESSIONAL_ID")
	@Getter @Setter private Long professionalId;
	
	@ApiModelProperty(value = "学生学号", required = false)
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
	
	@ApiModelProperty(value = "实践企业名称", required = false)
	@Column(name = "ENTERPRISE_NAME")
	@Getter @Setter private String enterpriseName;
	
	@ApiModelProperty(value = "实践课程id", required = false)
	@Column(name = "WEEK_TASK_ID")
	@Getter @Setter private String weekTaskId;
	
	@ApiModelProperty(value = "机构id", required = false)
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称", required = false)
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
}
