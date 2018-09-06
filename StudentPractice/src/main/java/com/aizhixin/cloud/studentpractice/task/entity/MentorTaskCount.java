
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
 * 导师任务完成情况统计表
 * @author zhengning
 *
 */
@Entity(name = "SP_MENTOR_TASK_COUNT")
@ToString
public class MentorTaskCount extends AbstractStringIdEntity {
	
	private static final long serialVersionUID = 1L;

	/*
	 * 企业导师任务id
	 */
	@NotNull
	@Column(name = "MENTOR_TASK_ID")
	@Getter @Setter private String mentorTaskId;
	
	/*
	 * 待审核统计结果
	 */
	@Column(name = "CHECK_PENDING_NUM")
	@Getter @Setter private Long checkPendingNum;
	
	/*
	 * 已完成统计结果
	 */
	@Column(name = "FINISH_NUM")
	@Getter @Setter private Long finishNum;
}
