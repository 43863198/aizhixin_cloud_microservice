
package com.aizhixin.cloud.studentpractice.score.entity;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractEntity;
import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * 实践成绩表
 * @author zhengning
 *
 */
@Entity(name = "SP_SCORE")
public class Score extends AbstractStringIdEntity {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "学号")
	@Column(name = "JOB_NUM")
	@Getter @Setter private String jobNum;
	
	@ApiModelProperty(value = "学生id")
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	@Column(name = "STUDENT_NAME")
	@Getter @Setter private String studentName;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter private Long orgId;
	
	@ApiModelProperty(value = "企业导师id")
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "企业导师名称")
	@Column(name = "MENTOR_NAME")
	@Getter @Setter private String mentorName;
	
	@ApiModelProperty(value = "辅导员id")
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称")
	@Column(name = "COUNSELOR_NAME")
	@Getter @Setter private String counselorName;
	
	@ApiModelProperty(value = "班级id")
	@Column(name = "CLASS_ID")
	@Getter @Setter private Long classId;
	
	@ApiModelProperty(value = "院系id")
	@Column(name = "COLLEGE_ID")
	@Getter @Setter private Long collegeId;
	
	@ApiModelProperty(value = "专业id")
	@Column(name = "PROFESSIONAL_ID")
	@Getter @Setter private Long professionalId;
	
	@ApiModelProperty(value = "班级名称")
	@Column(name = "CLASS_NAME")
	@Getter @Setter private String className;
	
	@ApiModelProperty(value = "专业名称")
	@Column(name = "PROFESSIONAL_NAME")
	@Getter @Setter private String professionalName;
	
	@ApiModelProperty(value = "学院名称")
	@Column(name = "COLLEGE_NAME")
	@Getter @Setter private String collegeName;
	
	@ApiModelProperty(value = "参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "企业id")
	@Column(name = "ENTERPRISE_ID")
	@Getter @Setter private Long enterpriseId;
	
	@ApiModelProperty(value = "企业名称")
	@Column(name = "ENTERPRISE_NAME")
	@Getter @Setter private String enterpriseName;
	
	@ApiModelProperty(value = "签到成绩")
	@Column(name = "SIGN_SCORE")
	@Getter @Setter private Double signScore;
	
	@ApiModelProperty(value = "日志成绩")
	@Column(name = "SUMMARY_SCORE")
	@Getter @Setter private Double summaryScore;
	
	@ApiModelProperty(value = "实践报告成绩")
	@Column(name = "REPORT_SCORE")
	@Getter @Setter private Double reportScore;
	
	@ApiModelProperty(value = "任务成绩")
	@Column(name = "TASK_SCORE")
	@Getter @Setter private Double taskScore;
	
	@ApiModelProperty(value = "总成绩")
	@Column(name = "TOTAL_SCORE")
	@Getter @Setter private Double totalScore;
	
}
