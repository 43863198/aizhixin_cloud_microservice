
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
 * 实践周任务分配实践组表
 * @author zhengning
 *
 */
@Entity(name = "SP_WEEK_TASK_TEAM")
@ToString
public class WeekTaskTeam extends AbstractStringIdEntity {
	
	private static final long serialVersionUID = 1L;

	
	
	@ApiModelProperty(value = "机构id")
	@Column(name = "ORG_ID")
	@Getter @Setter protected Long orgId;
	
	@ApiModelProperty(value = "实践组id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "周任务id")
	@Column(name = "WEEK_TASK_ID")
	@Getter @Setter protected String weekTaskId;
	
	@ApiModelProperty(value = "备注")
	@Column(name = "REMARK")
	@Getter @Setter private String remark;
	
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
