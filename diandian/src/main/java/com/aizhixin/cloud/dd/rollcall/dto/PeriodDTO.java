/**
 * 
 */
package com.aizhixin.cloud.dd.rollcall.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author 郑宁
 *
 */
@ApiModel(description="课程节信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class PeriodDTO {
	
	@ApiModelProperty(value = "ID", required = false, allowableValues = "range[1,infinity]", position=1)
	protected Long id;

	@ApiModelProperty(value = "学校ID", allowableValues = "range[1,infinity)", position=15)
	@NotNull(message ="机构id不能为空")
	@Digits(fraction = 0, integer = 18)
	private Long orgId;
	
	@ApiModelProperty(value = "起始时间(格式为:HH:mm)", required = true)
	@NotNull(message ="起始时间不能为空")
	private String startTime;

	@ApiModelProperty(value = "终止时间(格式为:HH:mm)", required = true)
	@NotNull(message ="终止时间不能为空")
	private String endTime;
	
	@ApiModelProperty(value = "第几节 ", required = false)
	@NotNull(message ="第几节不能为空")
	private  Integer no;

	@ApiModelProperty(value = "创建日期", required = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	
	@NotNull(message ="操作用户id不能为空")
	@ApiModelProperty(value = "操作用户id ", required = false)
	private Long userId;

	@NotNull(message ="课程节名称")
	@ApiModelProperty(value = "课程节名称 ", required = false)
	private String name;

	@ApiModelProperty(value = "教室")
	private String classroom;

	@ApiModelProperty(value = "教学班id")
	private Long teachingClassId;

	@ApiModelProperty(value = "教师名称")
	private String teacherName;

	@ApiModelProperty(value = "课程名称")
	private String courseName;
	
	public PeriodDTO() {}
	public PeriodDTO(Long id,Integer no) {
		this.id = id;
		this.no = no;
	}
	public PeriodDTO(Long id,String startTime, String endTime, Integer no, Date createdDate,Long orgId) {
		this.id = id;
		this.orgId = orgId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.no = no;
		this.createdDate = createdDate;
	}
}
