/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import java.util.Date;

/**
 * @author zhen.pan
 *
 */
@ApiModel(description="学周信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class WeekDomain {
	
	@ApiModelProperty(value = "ID", allowableValues = "range[1,infinity]", position=1)
	protected Long id;

	@ApiModelProperty(value = "名称", position=2)
	protected String name;
	
	@ApiModelProperty(value = "学期ID", required = true)
	@NotNull
	@Digits(fraction = 0, integer = 20)
	private Long semesterId;
	
	@ApiModelProperty(value = "学期名称")
	private String semesterName;

	@ApiModelProperty(value = "学期编码")
	private String semesterCode;

	@ApiModelProperty(value = "起始时间 ", required = true)
	@NotNull(message ="起始时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startDate;

	@ApiModelProperty(value = "终止时间 ", required = true)
	@NotNull(message ="终止时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "第几周 ")
	@NotNull(message ="第几周不能为空")
	private  Integer no;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	
	@NotNull(message ="操作用户id不能为空")
	@ApiModelProperty(value = "操作用户id ")
	private Long userId;
	
	public WeekDomain() {}
	public WeekDomain(Long id,Integer no) {
		this.id = id;
		this.no = no;
	}
	public WeekDomain(Long id, String name, Date startDate, Date endDate, Integer no, Date createdDate) {
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.no = no;
		this.createdDate = createdDate;
	}
	
	public WeekDomain(Long id, String name, Date startDate, Date endDate, Integer no, Date createdDate,Long semesterId) {
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.no = no;
		this.createdDate = createdDate;
		this.semesterId = semesterId;
	}
}
