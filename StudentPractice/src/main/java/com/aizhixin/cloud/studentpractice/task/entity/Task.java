
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
 * 实践任务表
 * @author zhengning
 *
 */
@Entity(name = "SP_TASK")
@ToString
public class Task extends AbstractStringIdEntity {
	
	private static final long serialVersionUID = 1L;

	/*
	 * 任务名称
	 */
	@NotNull
	@Column(name = "TASK_NAME")
	@Getter @Setter private String taskName;
	/*
	 * 任务描述
	 */
	@Column(name = "DESCRIPTION")
	@Getter @Setter private String description;
	
	/*
	 * 机构id
	 */
	@Column(name = "org_id")
	@Getter @Setter private Long orgId;
	/*
	 * 周任务id
	 */
	@Column(name = "WEEK_TASK_ID")
	@Getter @Setter private String weekTaskId;
	
	@ApiModelProperty(value = "学时")
	@Column(name = "CLASS_HOUR")
	@Getter @Setter private Integer classHour;

	/**
	 * 描述
	 */
	@Column(name = "describe_Info")
	@Getter@Setter
	private String describe;
	
}
