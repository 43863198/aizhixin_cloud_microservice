/**
 * 
 */
package com.aizhixin.cloud.studentpractice.evaluate.domain;




import java.util.Date;
import java.util.Set;

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




@ApiModel(description="实践评价查询")
@Data
@EqualsAndHashCode(callSuper=false)
public class QueryEvaluateDomain {
	
	
	@ApiModelProperty(value = "辅导员所在实践参与计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "学生姓名", required = false)
	private String stuName;
	
	@ApiModelProperty(value = "ieval:我评价的,evalme:评价我的;默认为ieval", required = false)
	private String flag;
	
	
}
