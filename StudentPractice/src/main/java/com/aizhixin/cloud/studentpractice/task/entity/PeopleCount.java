
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
 * 实践人数统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_PEOPLE_COUNT")
@ToString
public class PeopleCount  {
	
	@ApiModelProperty(value = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@Getter @Setter protected Long id;

	@ApiModelProperty(value = "班级名称")
	@Column(name = "CLASS_NAME")
	@Getter @Setter protected String className;
	
	@ApiModelProperty(value = "班级id")
	@Column(name = "CLASS_ID")
	@Getter @Setter private Long classId;
	
	@ApiModelProperty(value = "院系名称")
	@Column(name = "COLLEGE_NAME")
	@Getter @Setter protected String collegeName;
	
	@ApiModelProperty(value = "院系id")
	@Column(name = "COLLEGE_ID")
	@Getter @Setter private Long collegeId;
	
	@ApiModelProperty(value = "专业名称")
	@Column(name = "PROFESSIONAL_NAME")
	@Getter @Setter protected String professionalName;
	
	@ApiModelProperty(value = "专业id")
	@Column(name = "PROFESSIONAL_ID")
	@Getter @Setter private Long professionalId;
	
	@ApiModelProperty(value = "总学生数")
	@Column(name = "STU_NUM")
	@Getter @Setter protected Integer stuNum = 0;
	
	@ApiModelProperty(value = "参与人数")
	@Column(name = "PRACTICE_NUM")
	@Getter @Setter protected Integer praticeNum = 0;
	
	@ApiModelProperty(value = "未参与人数")
	@Column(name = "NOT_PRACTICE_NUM")
	@Getter @Setter protected Integer notPraticeNum = 0;
	
	@ApiModelProperty(value = "提交过日志或任务的学生人数")
	@Column(name = "JOIN_NUM")
	@Getter @Setter protected Integer joinNum = 0;
	
	@ApiModelProperty(value = "未提交过日志或任务的学生人数")
	@Column(name = "NOT_JOIN_NUM")
	@Getter @Setter protected Integer notJoinNum = 0;
	
	@ApiModelProperty(value = "实践日志周志提交总数")
	@Column(name = "SUMMARY_NUM")
	@Getter @Setter protected Integer summaryNum = 0;
	
	@ApiModelProperty(value = "实践报告提交总数")
	@Column(name = "REPORT_NUM")
	@Getter @Setter protected Integer reportNum = 0;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter protected Long orgId;
	
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
