package com.aizhixin.cloud.dd.rollcall.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.aizhixin.cloud.dd.common.entity.AbstractEntity;

@Entity(name = "DD_COURSEROLLCALL")
@ToString
public class CourseRollCall extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 课程id
	 */
	@NotNull
	@Column(name = "COURSE_ID")
	@Getter
	@Setter
	private Long courseId;

	/**
	 * 教师ID
	 */
	@NotNull
	@Column(name = "TEACHER_ID")
	@Getter
	@Setter
	private Long teacherId;

	/**
	 * 是否开启点名
	 */
	@Column(name = "ISOPEN")
	@Getter
	@Setter
	private String isOpen;

	/**
	 * 点名方式
	 */
	@NotNull
	@Column(name = "ROLL_CALL_TYPE")
	@Getter
	@Setter
	private String rollCallType;

	/**
	 * 迟到时间
	 */
	@Column(name = "LATE_TIME")
	@Getter
	@Setter
	private Integer lateTime;

	/**
	 * 旷课时间
	 */
	@Column(name = "absenteeism_time")
	@Getter
	@Setter
	private Integer absenteeismTime;

}
