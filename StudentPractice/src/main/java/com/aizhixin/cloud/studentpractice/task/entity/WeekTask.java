
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
 * 实践周任务表
 * @author zhengning
 *
 */
@Entity(name = "SP_WEEK_TASK")
@ToString
public class WeekTask extends AbstractStringIdEntity {
	
	private static final long serialVersionUID = 1L;

	
	@ApiModelProperty(value = "备注")
	@Column(name = "REMARK")
	@Getter @Setter private String remark;
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter protected Long orgId;
	
	@ApiModelProperty(value = "实践第几周")
	@Column(name = "WEEK_NO")
	@Getter @Setter private String weekNo;
	
	@ApiModelProperty(value = "任务标题")
	@Column(name = "TASK_TITLE")
	@Getter @Setter protected String taskTitle;
	
	@ApiModelProperty(value = "开始时间")
	@Column(name = "BEGIN_DATE")
	@Getter @Setter private Date beginDate;
	
	@ApiModelProperty(value = "结束时间")
	@Column(name = "END_DATE")
	@Getter @Setter private Date endDate;
	
	@ApiModelProperty(value = "学时")
	@Column(name = "CLASS_HOUR")
	@Getter @Setter private Integer classHour;
}
