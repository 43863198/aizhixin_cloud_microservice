/**
 * 
 */
package com.aizhixin.cloud.studentpractice.summary.domain;




import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;




@ApiModel(description="实践报告查询")
@Data
@EqualsAndHashCode(callSuper=false)
public class QueryReportDomain {
	
	@ApiModelProperty(value = "报告标题", required = false)
	private String reportTitle;
	
	@ApiModelProperty(value = "用户id", required = false)
	private Long userId;
	
	@ApiModelProperty(value = "实践计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "状态:未提交[uncommit],待审核[checkPending],已通过[finish],被打回[backTo]", required = false)
	private String status;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "学号/姓名", required = false)
	private String keyWords;
	
}
