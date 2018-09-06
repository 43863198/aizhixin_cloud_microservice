package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "教师端课程详情信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class CourseRollCallDTO {

	private Long id = 0l;

	private Long courseId;
	private String courseName = "";
	private String courseCode = "";
	/**
	 * 是否开启点名
	 */
	private String isOpen;

	/**
	 * 迟到时间
	 */
	private Integer lateTime;
	/**
	 * 旷课时间
	 */
	private Integer absenteeismTime;

	/**
	 * 重复使用
	 */
	private Integer reuser;
	/**
	 * 基础点名方式，点名设置
	 */
	private String rollCallTypeOrigin;

}
