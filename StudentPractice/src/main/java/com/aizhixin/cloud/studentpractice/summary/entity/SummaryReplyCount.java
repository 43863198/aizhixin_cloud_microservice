
package com.aizhixin.cloud.studentpractice.summary.entity;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

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
 * 日报周报月报回复统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_SUMMARY_COUNT")
@ToString
public class SummaryReplyCount  {
	

	@ApiModelProperty(value = "ID")
	@Id
	@Column(name = "ID")
	@Getter @Setter private String id;

	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	@Column(name = "SUMMARY_TYPE")
	@Getter @Setter private String summaryType;
	
	@ApiModelProperty(value = "报告标题", required = false)
	@Column(name = "SUMMARY_TITLE")
	@Getter @Setter private String summaryTitle;
	
	@ApiModelProperty(value = "回复数量", required = false)
	@Column(name = "REPLY_NUM")
	@Getter @Setter private Integer replyNum;
	
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
	
	@ApiModelProperty(value = "学生id")
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	@Column(name = "COUNSELOR_ID")
	@Getter @Setter private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
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
	
	@ApiModelProperty(value = "年级")
	@Column(name = "GRADE")
	@Getter @Setter private String grade;
	
	@ApiModelProperty(value = "实践参与计划id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践参与计划名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter private String groupName;
	
	@ApiModelProperty(value = "创建时间")
    @CreatedDate
    @Column(name = "CREATED_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter @Setter private Date createdDate = new Date();

	@ApiModelProperty(value = "是否删除标志")
	@Column(name = "DELETE_FLAG")
	@Getter @Setter private Integer deleteFlag = DataValidity.VALID.getIntValue();
}
