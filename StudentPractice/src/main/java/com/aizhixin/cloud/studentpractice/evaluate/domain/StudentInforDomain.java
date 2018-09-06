/**
 * 
 */
package com.aizhixin.cloud.studentpractice.evaluate.domain;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ApiModel(description="学生信息")
@Data
@ToString
public class StudentInforDomain  implements java.io.Serializable{
	
	@ApiModelProperty(value = "学生ID", required = false)
	public Long id;

	@ApiModelProperty(value = "学生名称", required = false)
	public String name;
	
	@ApiModelProperty(value = "学生学号", required = false)
	public String jobNum;
	
	@ApiModelProperty(value = "学生头像", required = false)
	public String avatar;
	
	@ApiModelProperty(value = "导师id", required = false)
	public Long mentorId;
	
	@ApiModelProperty(value = "导师名称", required = false)
	public String mentorName;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "辅导员工号", required = false)
	private String counselorJobNum;
	
	@ApiModelProperty(value = "评价id", required = false)
	private String evaluateId;
	
	@ApiModelProperty(value = "所属实践计划名称", required = false)
	private String groupName;
	
	@ApiModelProperty(value = "学生所在行政班名称", required = false)
	public String stuClassName;
	
	@ApiModelProperty(value = "导师所在公司名称", required = false)
	public String mentorCompanyName;
	
	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
}
