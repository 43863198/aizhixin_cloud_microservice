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




@ApiModel(description="日报，周报，月报查询")
@Data
@EqualsAndHashCode(callSuper=false)
public class QuerySummaryDomain {
	
	@ApiModelProperty(value = "报告标题", required = false)
	private String summaryTitle;
	
	@ApiModelProperty(value = "类型:日报[daily],周报[weekly],月报[monthly]", required = false)
	private String summaryType;
	
	@ApiModelProperty(value = "辅导员id", required = false)
	private Long counselorId;
	
	@ApiModelProperty(value = "实践计划id", required = false)
	private Long groupId;
	
	@ApiModelProperty(value = "企业导师id", required = false)
	private Long mentorId;
	
	@ApiModelProperty(value = "用户id", required = false)
	private Long userId;
	
	@ApiModelProperty(value = "发布状态:公开[open],非公开[private],保存[save]", required = false)
	private String publishStatus;
	
	@ApiModelProperty(value = "第几页", required = false)
	private Integer pageNumber;
	
	@ApiModelProperty(value = "每页数据的数目", required = false)
	private Integer pageSize;
	
	@ApiModelProperty(value = "排序标识(asc:创建时间升序,desc:创建时间倒序)", required = false)
	private String sortFlag;
	
	@ApiModelProperty(value = "排序字段(SUMMARY_TYPE:类型(日报[daily]，周报[weekly]，月报[monthly]),PUBLISH_STATUS:布状态:公开[open],非公开]private,保存[save])", required = false)
	private String sortField;
	
	@ApiModelProperty(value = "筛选类型:当天(1)一周(7)一个月(30)", required = false)
	private Integer dayNum;
	
	@ApiModelProperty(value = "创建者名称", required = false)
	private String creatorName;
	
}
