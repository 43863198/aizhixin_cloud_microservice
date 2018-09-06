
package com.aizhixin.cloud.studentpractice.task.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
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
 * 任务统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_TASK_STATISTICAL")
@ToString
public class TaskStatistical  {
	
	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter protected Long id;

	@ApiModelProperty(value = "学号")
	@Column(name = "JOB_NUM")
	@Getter @Setter protected String jobNum;
	
	@ApiModelProperty(value = "班级id")
	@Column(name = "CLASS_ID")
	@Getter @Setter private Long classId;
	
	@ApiModelProperty(value = "院系id")
	@Column(name = "COLLEGE_ID")
	@Getter @Setter private Long collegeId;
	
	@ApiModelProperty(value = "专业id")
	@Column(name = "PROFESSIONAL_ID")
	@Getter @Setter private Long professionalId;
	
	@ApiModelProperty(value = "学生id")
	@Column(name = "STUDENT_ID")
	@Getter @Setter private Long studentId;
	
	@ApiModelProperty(value = "学生名称")
	@Column(name = "STUDENT_NAME")
	@Getter @Setter protected String studentName;
	
	@ApiModelProperty(value = "实践企业名称")
	@Column(name = "ENTERPRISE_NAME")
	@Getter @Setter protected String enterpriseName;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter protected Long orgId;
	
	@ApiModelProperty(value = "导师名称")
	@Column(name = "MENTOR_NAME")
	@Getter @Setter protected String mentorName;
	
	@ApiModelProperty(value = "总任务数")
	@Column(name = "TOTAL_NUM")
	@Getter @Setter protected int totalNum;
	
	@ApiModelProperty(value = "通过任务数")
	@Column(name = "PASS_NUM")
	@Getter @Setter protected int passNum;
	
	@ApiModelProperty(value = "未通过任务数")
	@Column(name = "NOT_PASS_NUM")
	@Getter @Setter protected int notPassNum;
	
	@ApiModelProperty(value = "被打回任务数")
	@Column(name = "BACK_TO_NUM")
	@Getter @Setter protected int backToNum;
	
	@ApiModelProperty(value = "待审核任务数")
	@Column(name = "CHECK_PENDING_NUM")
	@Getter @Setter protected int checkPendingNum;
	
	@ApiModelProperty(value = "未提交任务数")
	@Column(name = "UNCOMMIT_NUM")
	@Getter @Setter protected int uncommitNum;
	
	@ApiModelProperty(value = "任务平均分数")
	@Column(name = "AVG_SCORE")
	@Getter @Setter protected Double avgScore;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter protected Date createdDate = new Date();


	@ApiModelProperty(value = "最后一次修改时间")
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter protected Date lastModifiedDate = new Date();

	@ApiModelProperty(value = "是否删除标志")
	@Column(name = "DELETE_FLAG")
	@Getter @Setter protected Integer deleteFlag = DataValidity.VALID.getIntValue();
}
