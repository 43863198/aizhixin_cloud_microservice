/**
 * 
 */
package com.aizhixin.cloud.orgmanager.company.domain;

import com.aizhixin.cloud.orgmanager.common.domain.IdNameDomain;
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
@ApiModel(description="学期信息")
@Data
@EqualsAndHashCode(callSuper=false)
public class SemesterDomain extends IdNameDomain {
	@ApiModelProperty(value = "学校ID", required = true)
	@Digits(fraction = 0, integer = 20)
	private Long orgId;
	
	@ApiModelProperty(value = "学校名称", required = false)
	private String orgName;
	@ApiModelProperty(value = "学期编码")
	private String code;

//	@ApiModelProperty(value = "学年ID", required = true)
//	@NotNull
//	private String yearId;
//
//	@ApiModelProperty(value = "学年名称")
//	private String yearName;

	@ApiModelProperty(value = "起始时间 ", required = true)
	@NotNull(message ="起始时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startDate;

	@ApiModelProperty(value = "终止时间 ", required = true)
	@NotNull(message ="结束时间不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "周数量")
	private  Integer numWeek;

	@ApiModelProperty(value = "创建日期")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate;
	
	@ApiModelProperty(value = "操作用户id")
	private Long userId;
	
	public SemesterDomain() {}
	public SemesterDomain(Long orgId, Long id, String name, Date startDate, Date endDate, Integer numWeek, Date createdDate) {
		super(id, name);
		this.orgId = orgId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.numWeek = numWeek;
		this.createdDate = createdDate;
	}
	public SemesterDomain(Long id, String name, Date startDate, Date endDate, Integer numWeek, Date createdDate) {
		super(id, name);
		this.startDate = startDate;
		this.endDate = endDate;
		this.numWeek = numWeek;
		this.createdDate = createdDate;
	}
	public SemesterDomain(Long id, String name, Date startDate, Date endDate, Integer numWeek, Date createdDate, String code) {
		this(id, name, startDate, endDate, numWeek, createdDate);
		this.code = code;
	}

//	public SemesterDomain(Long id, String name, Date startDate, Date endDate, Integer numWeek, Date createdDate, String yearId, String yearName) {
//		this(id, name, startDate, endDate, numWeek, createdDate);
//		this.yearId = yearId;
//		this.yearName = yearName;
//	}
}
