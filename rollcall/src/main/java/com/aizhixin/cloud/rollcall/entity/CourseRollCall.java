package com.aizhixin.cloud.rollcall.entity;

import com.aizhixin.cloud.rollcall.common.entity.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

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

}
