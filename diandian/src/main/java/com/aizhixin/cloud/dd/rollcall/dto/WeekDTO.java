package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(description = "学周信息")
@Data
@EqualsAndHashCode(callSuper = false)
public class WeekDTO {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private Long id;

	/*
	 * 学校机构id
	 */
	private Long organId;

	/*
	 * 学期id
	 */
	private Long semesterId;

	/*
	 * 学周名
	 */
	private String name;

	/*
	 * 起始时间
	 */
	private String startDate;

	/*
	 * 终止时间
	 */
	private String endDate;

	/*
	 * 数据状态
	 */
	private String status;
}
