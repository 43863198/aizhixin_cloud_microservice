/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;






import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.aizhixin.cloud.studentpractice.common.core.DataValidity;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践报告统计")
@Data
@EqualsAndHashCode(callSuper=false)
public class ReportStatisticsDomain {
	
	
	@ApiModelProperty(value = "实践报告标题", required = false)
	private String reportTitle;
	
	@ApiModelProperty(value = "学号")
	private String jobNum;
	
	@ApiModelProperty(value = "学生名称")
	private String studentName;
	
	@ApiModelProperty(value = "辅导员名称", required = false)
	private String counselorName;
	
	@ApiModelProperty(value = "班级名称")
	private String className;
	
	@ApiModelProperty(value = "专业名称")
	private String professionalName;
	
	@ApiModelProperty(value = "学院名称")
	private String collegeName;
	
	@ApiModelProperty(value = "参与计划名称")
	private String groupName;
	
	@ApiModelProperty(value = "年级")
	private String grade;
	
	@ApiModelProperty(value = "批阅时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date reviewTime = new Date();
	
	@ApiModelProperty(value = "是否提交", required = false)
	private boolean isCommit;
	
	@ApiModelProperty(value = "批阅建议", required = false)
	private String advice;
	
	@ApiModelProperty(value = "批阅状态:未提交[uncommit],待审核[checkPending],已通过[pass],未通过[notPass],被打回[backTo]", required = false)
	private String status;
	
	@ApiModelProperty(value = "提交时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdDate = new Date();

}
