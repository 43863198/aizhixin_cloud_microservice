
package com.aizhixin.cloud.studentpractice.task.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.aizhixin.cloud.studentpractice.common.entity.AbstractStringIdEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * 导师任务表
 * @author zhengning
 *
 */
@Entity(name = "SP_MENTOR_TASK")
@ToString
public class MentorTask extends AbstractStringIdEntity {
	private static final long serialVersionUID = 5532895497747016693L;
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
	
	
	@ApiModelProperty(value = "开始时间")
	@Column(name = "BEGIN_DATE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.DATE)
	@Getter @Setter private Date beginDate;
	
	/*
	 * 任务截至日期 
	 */
    @Column(name = "DEAD_LINE")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(javax.persistence.TemporalType.DATE)
	@Getter @Setter private Date deadLine;
	/*
	 * 任务状态
	 */
	@Column(name = "MENTOR_TASK_STATUS")
	@Getter @Setter private String mentorTaskStatus;
	/*
	 * 任务进度
	 */
	@Column(name = "PROGRESS")
	@Getter @Setter private String progress;
	/*
	 * 已打分人数
	 */
	@Column(name = "score_num")
	@Getter @Setter private int scoreNum;
	/*
	 * 导师id
	 */
	@NotNull
	@Column(name = "MENTOR_ID")
	@Getter @Setter private Long mentorId;
	/*
	 * 导师名称
	 */
	@Column(name = "MENTOR_NAME")
	@Getter @Setter private String mentorName;
	/*
	 * 任务创建者名称
	 */
	@Column(name = "CREATOR_NAME")
	@Getter @Setter private String creatorName;
	
	/*
	 * 创建着角色
	 */
	@Column(name = "creator_role")
	@Getter @Setter private String creatorRole;
	/*
	 * 周任务id
	 */
	@Column(name = "WEEK_TASK_ID")
	@Getter @Setter private String weekTaskId;
	
	@ApiModelProperty(value = "学时")
	@Column(name = "CLASS_HOUR")
	@Getter @Setter private Integer classHour;
	
	@ApiModelProperty(value = "实践组id")
	@Column(name = "GROUP_ID")
	@Getter @Setter private Long groupId;
	
	@ApiModelProperty(value = "实践小组名称")
	@Column(name = "GROUP_NAME")
	@Getter @Setter protected String groupName;
	
	/*
	 * 任务id
	 */
	@Column(name = "TASK_ID")
	@Getter @Setter private String taskId;

	/**
	 * 详情导师描述
	 */
	@Column(name = "describe_Info")
	@Getter@Setter
	private String describe;

	/**
	 * 详情导师描述图片集合
	 */
	@Column(name = "img_list")
	@Setter@Getter
	private String imgList;
}
